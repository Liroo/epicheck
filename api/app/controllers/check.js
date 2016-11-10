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
    var now = new Date();
    now = new Date(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate(), now.getUTCHours() + 1, now.getUTCMinutes(), now.getUTCSeconds())
    //now.setTime(now.getTime() - now.getTimezoneOffset()*60*1000 );
    var presence = new Presence({
      student: '',
      date: now,
      activity: '',
      present: (req.body["present"] ? req.body["present"] : true),
      force: (req.body["force"] ? req.body["force"] : true)
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
            var acti = student.activities.find(a => { return (a.toString() === activity._id.toString()); });
            if (!acti) {
              res.status(404).json({
                message: "student not registered"
              });
            } else {
              Presence.findOne({
                student: student,
                activity: activity
              }).exec(function(err, pres) {
                if (pres) {
                  res.status(409).json({
                    message: 'presence already defined'
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

router.delete('/:student/:activity', function(req, res, next) {
    Presence.findOne({
        student: req.params["student"],
        activity: req.params["activity"]
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
