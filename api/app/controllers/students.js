var express = require('express');
var mongoose = require('mongoose');
var Student = mongoose.model('Student');
var Activity = mongoose.model('Activity');
var Presence = mongoose.model('Presence');
var router = express.Router();
var async = require("async");
var sql = require('mssql');

var sql_user = process.env.MSSQL_USER;
var sql_pass = process.env.MSSQL_PASS;
var sql_addr = process.env.MSSQL_ADDR;
var sql_base = process.env.MSSQL_BASE;

module.exports = function(app) {
  app.use('/students/', router);
};

router.get('/', function(req, res, next) {
    Student.find().exec(function(err, students) {
      res.status(200).json(students);
    });
});

router.post('/add', function(req, res, next) {
    if (req.body["email"]) {
        var student = new Student({
            email: req.body["email"],
            activities: []
        });
        if (req.body["activities"]) {
          async.forEach(req.body["activities"], function(activity, callback) {
              Student.findOne({
                codeActi: activity.codeActi,
                codeEvent: activity.codeEvent
              }, function(err, acti) {
                if (acti) {
                  student.activities.push(acti);
                }
              });
            callback();
          }, function(err) {
            student.save(function(err) {
                if (err) {
                    res.status(500).json(err);
                } else {
                    res.status(200).json({
                        message: "Student added."
                    });
                }
            });
          });
        }
    } else {
        res.status(222).json({
            message: "missing parameter."
        });
    }
});

router.post('/link', function(req, res, next) {
  if (req.body["idActivity"] && req.body["email"]) {
    Student.findOne({
      email: req.body["email"]
    }, 'email activities', function (err, student) {
      if (student !== null) {
        Activity.findOne({
          _id: req.body["idActivity"]
        }, function(err, activity) {
          if (!activity) {
            res.status(404).json({
              message: "activity not found"
            });
          } else {
            student.activities.push(activity);
            student.save();
            res.status(200).json({
              message: "activity linked to student"
            });
          }
        });
      } else {
        res.status(404).json({
          message: "student not found"
        });
      }
    });
  } else {
    res.status(222).json({
      message: "missing parameter"
    });
  }
});

router.delete('/:email', function(req, res, next) {
    Student.findOne({
        email: req.params["email"]
    }).remove().exec(function(err) {
        if (err) {
            res.status(500).json(err);
        } else {
            res.status(200).json({
                message: "Student removed from base"
            });
        }
    });
});

router.get('/get/:email', function (req, res, next){
  Student.findOne({
    email: req.params["email"]
  }).populate('activities').exec(function(err, student) {
    if (!student) {
      res.status(404).json({message: "user not found"});
    } else {
      var activities = [];
      async.forEach(student.activities, function(activity, callback) {
          var acti = {
            actiTitle: activity.actiTitle,
            dateFrom: activity.dateFrom,
            dateTo: activity.dateTo,
            moduleTitle: activity.moduleTitle,
            scholarYear: activity.scholarYear,
            codeModule: activity.codeModule,
            codeInstance: activity.codeInstance,
            codeActi: activity.codeActi,
            codeEvent: activity.codeEvent,
            presence: { date: null, present: false, force: false }
          }
          Presence.findOne({
            student: student,
            activity: activity
          }).exec(function (err, presence) {
            if (presence) {
              acti.presence.present = presence.present;
              acti.presence.force = presence.force;
              acti.presence.date = presence.date;
            }
            activities.push(acti);
            callback();
          });
      }, function(err) {
        if (!err)
          res.status(200).json({
              _id: student._id,
              email: student.email,
              activities: activities
          });
      });
    }
  });
});

router.post('/get', function(req, res, next) {
  if (req.body["id"]) {
    sql.connect("mssql://" + sql_user + ":" + sql_pass + "@" + sql_addr + "/" + sql_base).then(function() {
      new sql.Request().query("select * from nfc where nfc_id LIKE '" + req.body["id"] + "'").then(function(recordset) {
        if (recordset.length == 0) {
          res.status(404).json({message: "user not found"});
        } else {
          Student.findOne({
            email: recordset[0].email
          }, "email activities").populate('activities').exec(function(err, student) {
            if (student) {
              res.status(200).json(student);
            } else {
              res.status(200).json(
                { email: recordset[0].email, activities: [] }
              );
            }
          });
        }
      }).catch(function(err) {
        res.status(500).json({message: "error while searching user"});
      });
    }).catch(function(err) {
      res.status(500).json({message: "error while database connection"});
    });
  } else {
    res.status(222).json({
      message: "missing parameter"
    });
  }
});
