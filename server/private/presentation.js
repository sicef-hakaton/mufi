var mongoose = require('mongoose'),
    Schema = mongoose.Schema,
    db = mongoose.createConnection('mongodb://localhost/evan');

var Votes = new Schema({
    value: { type: Number, min: -1, max: 100 },
    timeStamp: { type: Date, default: Date.now },
    id_user: { type: String }
});

var Presentation = new Schema({
    name: { type: String, required: true, trim: true, unique: true },
    password: { type: String, required: true },
    
    prof_id: { type: String, required: true },
    created: { type: Date, default: Date.now },

    startTime: { type: Date },
    
    votesTimeline: [Votes],
    slidesChanging: [{
        timeStamp: { type: Date, default: Date.now },
        slide: { type: Number, min: 0 }
    }]
});



var model = db.model('Presentation', Presentation);

exports = module.exports = model;