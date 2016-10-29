var express = require('express');
var mongoose = require('mongoose');
var router = express.Router();

module.exports = function(app) {
  app.use('/', router);
};

router.get('/', function(req, res, next) {
  res.status(200).json({
    message: "There is nothing here dude."
  })
});
