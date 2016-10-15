var express = require('express');
var mongoose = require('mongoose');
var Activity = mongoose.model('Activity');
var Student = mongoose.model('Student');
var router = express.Router();
var async = require("async");

module.exports = function(app) {
  app.use('/activities/', router);
};

router.get('/', function(req, res, next) {
    Activity.find().populate('students').exec(function(err, activities) {
        res.status(200).json({
            activities: activities
        });
    });
});

router.post('/add', function(req, res, next) {
  if (req.body["actiTitle"] && req.body["dateFrom"] && req.body["dateTo"] && req.body["moduleTitle"] && req.body["scholarYear"] &&
  req.body["codeModule"] && req.body["codeInstance"] && req.body["codeActi"] && req.body["codeEvent"] && req.body["students"]) {
    var activity = new Activity({
      actiTitle: req.body["actiTitle"],
      dateFrom: req.body["dateFrom"],
      dateTo: req.body["dateTo"],
      moduleTitle: req.body["moduleTitle"],
      scholarYear: req.body["scholarYear"],
      codeModule: req.body["codeModule"],
      codeInstance: req.body["codeInstance"],
      codeActi: req.body["codeActi"],
      codeEvent: req.body["codeEvent"],
      students: []
    });
    async.forEach(req.body["students"], function(student, callback) {
      if (student && student.email) {
        Student.findOne({
          email: student.email
        }, function(err, stud) {
          if (!stud) {
            var newStud = new Student({
              email: student.email,
              activities: []
            });
            newStud.save(function(err) {
              if (err) {
                res.status(500).json(err);
              } else {
                Student.findOne({
                  email: student.email
                }, function(err, sstud) {
                  activity.students.push(sstud);
                  callback();
                });
              }
            });
          } else {
            activity.students.push(stud);
            callback();
          }
        });
      }
    }, function(err) {
      activity.save(function(err) {
        if (err) {
          res.status(500).json(err);
        } else {
          Activity.findOne({
            codeActi: req.body["codeActi"],
            codeEvent: req.body["codeEvent"]
          }).populate("students").exec(function(err, activity) {
            if (!activity) {
              res.status(500).json(err);
            } else {
              async.forEach(activity.students, function (student, callback) {
                console.log(student);
                student.activities.push(activity);
                student.save();
                callback();
              }, function(err) {
                if (err) {
                  res.status(500).json(err);
                } else {
                  res.status(200).json({
                    message: "Activity added."
                  });
                }
              });
            }
          });
        }
      });
    });
  } else {
    res.status(222).json({
      message: "missing parameter."
    });
  }
});

router.post('/link', function(req, res, next) {
    if (req.body["idActivity"] && req.body["email"]) {
      Activity.findOne({
        _id: req.body["idActivity"]
      }, function(err, activity) {
          if (activity) {
            Student.findOne({
                email: req.body["email"]
            }, function (err, student) {
                if (student !== null) {
                  activity.students.push(student);
                  res.status(200).json({
                      message: "student linked to activity"
                  });
                } else {
                  res.status(404).json({
                      message: "student not found"
                  });
                }
            });
          } else {
            res.status(404).json('activity not found');
          }
      });
    } else {
        res.status(222).json({
          message: "Need more information to add student to activity"
        });
    }
});

router.delete('/:id', function(req, res, next) {
    Activity.findOne({
        _id: req.params["id"]
    }).remove().exec(function(err) {
        if (err) {
            res.status(500).json(err);
        } else {
            res.status(200).json({
                message: "Activity removed from base"
            });
        }
    });
});
