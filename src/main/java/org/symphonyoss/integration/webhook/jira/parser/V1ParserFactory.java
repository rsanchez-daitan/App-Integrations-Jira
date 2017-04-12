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

package org.symphonyoss.integration.webhook.jira.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.symphonyoss.integration.model.message.MessageMLVersion;
import org.symphonyoss.integration.webhook.jira.parser.v1.CommonJiraParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser factory for the MessageML v1.
 * Created by rsanchez on 21/03/17.
 */
@Component
public class V1ParserFactory extends JiraParserFactory {

  @Autowired
  private List<CommonJiraParser> beans;

  @Override
  public boolean accept(MessageMLVersion version) {
    return MessageMLVersion.V1.equals(version);
  }

  @Override
  protected List<JiraParser> getBeans() {
    return new ArrayList<JiraParser>(beans);
  }

}
