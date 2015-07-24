import React from 'react';
import Router from 'react-router';
import routes from './routes';
import ZeroClipboard from 'ZeroClipboard';

// make ZeroClipboard global
// for react-zeroclipboard
window.ZeroClipboard = ZeroClipboard;

// Set apiRoot if not defined in process.env
// To Do: Open modal with input field
if (!config.apiRoot) {
  console.warn('You need to set your apiRoot via localStorage');
  console.warn('e.g. localStorage["apiRootOverride"] = "https://path.to-api.com/v1/api"');
}

Router.run(routes, Router.HistoryLocation, Handler => React.render(<Handler />, document.body));
