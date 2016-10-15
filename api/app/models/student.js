var mongoose = require('mongoose'),
  Schema = mongoose.Schema;

var StudentSchema = new Schema({
  email: {
    type: String,
    required: true,
    index: {
      unique: true
    }
  },
  activities: [{
    type: Schema.Types.ObjectId,
    ref: 'Activity',
    default: null
  }]
});

mongoose.model('Student', StudentSchema);
