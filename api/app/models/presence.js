var mongoose = require('mongoose'),
  Schema = mongoose.Schema;

var PresenceSchema = new Schema({
  date: {
    type: Date,
    default: Date.now,
    required: true
  },
  present: {
    type: Boolean,
    default: false
  },
  hasValid: {
    type: Boolean,
    default: false
  },
  force: {
    type: Boolean,
    default: false
  },
  student: {
    type: Schema.Types.ObjectId,
    ref: 'Student'
  },
  activity: {
    type: Schema.Types.ObjectId,
    ref: 'Activity'
  }
});

mongoose.model('Presence', PresenceSchema);
