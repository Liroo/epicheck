var mongoose = require('mongoose'),
  Schema = mongoose.Schema;

var ActivitySchema = new Schema({
  actiTitle: {
    type: String,
    required: true
  },
  dateFrom: {
    type: Date,
    required: true
  },
  dateTo: {
    type: Date,
    required: true
  },
  moduleTitle: {
    type: String,
    required: true
  },
  scholarYear: {
    type: String,
    required: true
  },
  codeModule: {
    type: String,
    required: true
  },
  codeInstance: {
    type: String,
    required: true
  },
  codeActi: {
    type: String,
    required: true
  },
  codeEvent: {
    type: String,
    required: true
  },
  students: [{
    type: Schema.Types.ObjectId,
    ref: 'Student'
  }]
});

mongoose.model('Activity', ActivitySchema);
