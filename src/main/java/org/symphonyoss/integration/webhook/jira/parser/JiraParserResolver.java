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
import org.symphonyoss.integration.webhook.parser.WebHookParserFactory;
import org.symphonyoss.integration.webhook.parser.WebHookParserResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves the parser factory based on MessageML version.
 * Created by rsanchez on 22/03/17.
 */
@Component
public class JiraParserResolver extends WebHookParserResolver {

  @Autowired
  private List<JiraParserFactory> factories;

  @Override
  protected List<WebHookParserFactory> getFactories() {
    return new ArrayList<WebHookParserFactory>(factories);
  }

}
