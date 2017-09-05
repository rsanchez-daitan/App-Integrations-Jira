import { MessageEnricherBase, getUserJWT, authorizeUser } from 'symphony-integration-commons';
import { commentIssue } from '../services/jiraApiCalls';

const commentDialog = require('../templates/commentDialog.hbs');
const assingDialog = require('../templates/assignDialog.hbs');
const erroDialog = require('../templates/errorDialog.hbs');
const messageML = require('../templates/messageML.hbs');

const hostPort = window.location.port === 443 ? '' : `:${window.location.port}`;
const baseUrl = `${window.location.protocol}//${window.location.hostname}${hostPort}/integration`;
const comment = 'testing the API through the FE';
const name = 'issueState-renderer';
const messageEvents = ['com.symphony.integration.jira.event.v2.state'];

const accessToken = '';
const auth = authorizeUser('https://previewjira.atlassian.net');
auth.then(result => console.log(result));

function renderComment(data) {
  const url = data.entity.issue.url;
  const issuename = data.entity.issue.key;
  return commentDialog({
    func: commentIssue(baseUrl, issuename, url, comment, accessToken),
  });
}

function renderAssignTo(data) {
  return assingDialog({
    url: data.entity.issue.url,
    key: data.entity.issue.key,
    name: data.entity.issue.assignee.displayName,
  });
}

export default class IssueStateEnricher extends MessageEnricherBase {
  constructor() {
    super(name, messageEvents);
  }

  enrich(type, entity) {
    const result = {
      template: messageML(),
      data: {
        assignTo: {
          service: name,
          label: 'Assign To',
          data: {
            entity,
            type: 'AssignTo',
          },
        },
        commentIssue: {
          service: name,
          label: 'Comment',
          data: {
            entity,
            type: 'Comment',
          },
        },
        frame: {
          src: 'https://localhost.symphony.com:8186/apps/jira/bundle.json',
          height: 200,
        },
      },
    };

    return result;
  }

  action(data) {
    let dialogTemplate = null;
    switch (data.type) {
      case 'Comment':
        if (auth === true) {
          dialogTemplate = renderComment(data);
          this.dialogsService.show('action', 'issueRendered-renderer', dialogTemplate, {}, {});
        }
        break;
      case 'AssignTo':
        dialogTemplate = renderAssignTo(data);
        this.dialogsService.show('action', 'issueRendered-renderer', dialogTemplate, {}, {});
        break;
      default:
        dialogTemplate = erroDialog();
        this.dialogsService.show('action', 'issueRendered-renderer', dialogTemplate, {}, {});
        break;
    }
  }
}
