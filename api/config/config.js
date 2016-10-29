var path = require('path'),
    rootPath = path.normalize(__dirname + '/..'),
    env = process.env.NODE_ENV || 'localhost';

var config = {
  localhost: {
    root: rootPath,
    app: {
      name: 'api'
    },
    port: process.env.PORT || 3000,
    db: 'mongodb://localhost/epicheck'
  },

  docker: {
    root: rootPath,
    app: {
      name: 'api'
    },
    port: process.env.PORT || 3000,
    db: 'mongodb://database/epicheck'
  }
};

module.exports = config[env];
