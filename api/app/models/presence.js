var mongoose = require('mongoose'),
  Schema = mongoose.Schema;

var PresenceSchema = new Schema({
  date: {
    type: Date,
    default: Date.now,
    required: true
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
