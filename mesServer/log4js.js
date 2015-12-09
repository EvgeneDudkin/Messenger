var log4js = require('log4js');
log4js.configure({
  appenders: [
    { type: 'console', category: 'cons' },
    { type: 'file', filename: 'logs/LOGI.log', category: 'cons'},
    { type: 'file', filename: 'logs/LOGI.log', category: 'files'}
  ]
});

module.exports = log4js;