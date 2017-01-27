/**
 * Copyright 2016-2017 Symphony Integrations - Symphony LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.symphonyoss.integration.webhook.jira;

import static org.symphonyoss.integration.webhook.jira.JiraEventConstants.WEBHOOK_EVENT;

import org.symphonyoss.integration.service.model.Configuration;
import com.symphony.logging.ISymphonyLogger;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.json.JsonUtils;
import org.symphonyoss.integration.logging.IntegrationBridgeCloudLoggerFactory;
import org.symphonyoss.integration.webhook.WebHookIntegration;
import org.symphonyoss.integration.webhook.WebHookPayload;
import org.symphonyoss.integration.webhook.exception.WebHookParseException;
import org.symphonyoss.integration.webhook.jira.parser.JiraParser;
import org.symphonyoss.integration.webhook.jira.parser.JiraParserException;
import org.symphonyoss.integration.webhook.jira.parser.NullJiraParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * Implementation of a WebHook to integrate with JIRA, rendering it's messages.
 *
 * Created by Milton Quilzini on 04/05/16.
 */
@Component
public class JiraWebHookIntegration extends WebHookIntegration {

  private static final ISymphonyLogger LOG =
      IntegrationBridgeCloudLoggerFactory.getLogger(JiraWebHookIntegration.class);

  @Autowired
  private NullJiraParser defaultJiraParser;

  private Map<String, JiraParser> parsers = new HashMap<>();

  @Autowired
  private List<JiraParser> jiraBeans;

  @PostConstruct
  public void init() {
    for (JiraParser parser : jiraBeans) {
      List<String> events = parser.getEvents();
      for (String eventType : events) {
        this.parsers.put(eventType, parser);
      }
    }
  }

  @Override
  public void onConfigChange(Configuration conf) {
    super.onConfigChange(conf);

    String jiraUser = conf.getType();
    for (JiraParser parser : parsers.values()) {
      parser.setJiraUser(jiraUser);
    }
  }

  @Override
  public String parse(WebHookPayload input) throws WebHookParseException {
    try {
      JsonNode rootNode = JsonUtils.readTree(input.getBody());
      Map<String, String> parameters = input.getParameters();

      String webHookEvent = rootNode.path(WEBHOOK_EVENT).asText();
      String eventTypeName = rootNode.path("issue_event_type_name").asText();

      JiraParser parser = getParser(webHookEvent, eventTypeName);
      String formattedMessage = parser.parse(parameters, rootNode);

      return super.buildMessageML(formattedMessage, webHookEvent);
    } catch (IOException e) {
      throw new JiraParserException("Something went wrong while trying to convert your message to the expected format", e);
    }
  }

  /**
   * Get the JIRA Parser based on the event.
   *
   * @param webHookEvent
   * @param eventTypeName
   * @return
   */
  private JiraParser getParser(String webHookEvent, String eventTypeName) {
    JiraParser result = parsers.get(eventTypeName);

    if (result == null) {
      result = parsers.get(webHookEvent);
    }

    if (result == null) {
      return defaultJiraParser;
    }

    return result;
  }

}

