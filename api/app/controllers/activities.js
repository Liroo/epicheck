var express = require('express');
var mongoose = require('mongoose');
var Activity = mongoose.model('Activity');
var Student = mongoose.model('Student');
var Presence = mongoose.model('Presence');
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

router.post('/get', function(req, res, next) {
  if (req.body["codeActi"] && req.body["codeEvent"]) {
    Activity.findOne({
      codeActi: req.body["codeActi"],
      codeEvent: req.body["codeEvent"]
    }).populate("students").exec(function(err, activity) {
      if (!activity) {
        res.status(500).json(err);
      } else {
        var studs = [];
        async.forEach(activity.students, function(student, callback) {
          var stud = {
            _id: student._id,
            email: student.email,
            presence: { date: null, present: false, force: false }
          };
          Presence.findOne({
            student: student,
            activity: activity
          }).exec(function (err, presence) {
            if (presence) {
              stud.presence.present = presence.present;
              stud.presence.date = presence.date;
              stud.presence.force = presence.force;
            }
            studs.push(stud);
            callback();
          });
        }, function(err) {
          if (err) {
            res.status(500).json(err);
          } else {
            console.log(studs);
            res.status(200).json({
              _id: activity._id,
              actiTitle: activity.actiTitle,
              dateFrom: activity.dateFrom,
              dateTo: activity.dateTo,
              moduleTitle: activity.moduleTitle,
              scholarYear: activity.scholarYear,
              codeModule: activity.codeModule,
              codeInstance: activity.codeInstance,
              codeActi: activity.codeActi,
              codeEvent: activity.codeEvent,
              students: studs
            });
          }
        });
      }
    });
  } else {
    res.status(222).json({
      message: 'missing arguments'
    });
  }
});

function setPresence(stud, activity, studentIntra, callback) {
  if (!(studentIntra.present.toString() === 'null') || !(studentIntra.date.toString() === 'null')) {
    var now = new Date();
    now = new Date(now.getUTCFullYear(), now.getUTCMonth(), now.getUTCDate(), now.getUTCHours() + 1, now.getUTCMinutes(), now.getUTCSeconds());
    Presence.findOne({
      student: stud,
      activity: activity
    }).exec(function(err, pres) {
      if (pres) {
        pres.present = ((studentIntra.present.toString() === 'present' || studentIntra.present.toString() === 'N/A') ? true : false);
        pres.force = (studentIntra.date.toString() === 'null' && !(studentIntra.present.toString() === 'null') ? true : false);
        pres.save(function(err) {
          callback();
        });
      } else {
        var presence = new Presence({
          student: stud,
          date: (studentIntra.date.toString() === 'null' ? now : studentIntra.date.toString()),
          present: ((studentIntra.present.toString() === 'present' || studentIntra.present.toString() === 'N/A') ? true : false),
          force: (studentIntra.date.toString() === 'null' && !(studentIntra.present.toString() === 'null') ? true : false),
          activity: activity
        });
        presence.save(function (err) {
          callback();
        });
      }
    });
  } else {
    Presence.findOne({
      student: stud,
      activity: activity
    }).remove().exec(function(err) {
      callback();
    });
  }
}

function linkUserActivity(stud, activity, studentIntra, callback) {
  console.log("student intra = ", studentIntra);
  if (!activity.students.find(s => s.toString() === stud._id.toString())) {
    console.log("activity studs + ", stud.email);
    //console.log("stud id = ", stud._id);
    activity.students.push(stud);
  }
  if (!stud.activities.find(a => a.toString() === activity._id.toString())) {
      console.log("student activity + ", activity.actiTitle);
      //console.log("stud actis = ", stud.activities);
      stud.activities.push(activity);
      stud.save(function (err) {
        setPresence(stud, activity, studentIntra, callback);
      });
  } else {
    setPresence(stud, activity, studentIntra, callback);
  }
}

function linkStudents(req, res, activity) {
  async.forEach(req.body["students"], function(student, callback) {
    console.log("student = " + student.login);
    Student.findOne({
      email: student.login
    }, function(err, stud) {
      if (!stud) {
        var newStud = new Student({
          email: student.login,
          activities: []
        });
        newStud.save(function(err, newstud) {
          if (err) {
            res.status(500).json(err);
          } else {
            linkUserActivity(newstud, activity, student, callback);
          }
        });
      } else {
        console.log("student found");
        linkUserActivity(stud, activity, student, callback);
      }
    });
  }, function(err) {
    if (err) {
      res.status(500).json(err);
    } else {
      activity.save();
      res.status(200).json({
        message: 'Activity added'
      });
    }
  });
}

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
    Activity.findOne({
      codeActi: req.body["codeActi"],
      codeEvent: req.body["codeEvent"]
    }).exec(function(err, act) {
      if (!act) {
        activity.save(function(err, activity) {
          if (err) {
            res.status(500).json(err);
          } else {
            linkStudents(req, res, activity);
          }
        });
      } else {
        console.log("acti found" + act);
        linkStudents(req, res, act);
      }
    });
  } else {
    res.status(222).json({
      message: 'missing parameter'
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
