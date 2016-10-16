var express = require('express');
var mongoose = require('mongoose');
var Activity = mongoose.model('Activity');
var Student = mongoose.model('Student');
var Presence = mongoose.model('Presence');
var router = express.Router();
var async = require("async");

module.exports = function(app) {
  app.use('/check/', router);
};

router.get('/', function(req, res, next) {
    res.status(200).json({
        message: "You seems lost, aren't you ?"
    });
});

router.get('/list', function(req, res, next) {
    Presence.find().populate('student').populate('activity').exec(function(err, presences) {
      if (err) {
          res.status(500).json(err);
      } else {
          res.status(200).json({
            presences: presences
          });
      }
    });
});

router.post('/', function(req, res, next) {
    if (req.body["id"] && req.body["email"]) {
        var presence = new Presence({
            student: '',
            activity: ''
        });
        Activity.findOne({
            _id: req.body["id"]
        }, function(err, activity) {
            if (!activity) {
                res.status(404).json({
                    message: "activity not found"
                });
            } else {
                presence.activity = activity;
                Student.findOne({
                    email: req.body["email"]
                }, function (err, student) {
                    if (!student) {
                        res.status(404).json({
                            message: "student not found"
                        });
                    } else {
                        presence.student = student;
                        presence.save(function(err) {
                            if (err) {
                                res.status(500).json(err);
                            } else {
                                res.status(200).json({
                                    message: "student has been marked present"
                                });
                            }
                        });
                    }
                });
            }
        });
    } else {
        res.status(222).json({
            message: "missing parameter."
        });
    }
});

router.delete('/:id', function(req, res, next) {
    Presence.findOne({
        _id: req.params["id"]
    }).remove().exec(function(err) {
        if (err) {
            res.status(500).json(err);
        } else {
            res.status(200).json({
                message: "Student presence has been removed from base"
            });
        }
    });
});