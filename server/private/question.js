var mongoose = require('mongoose'),
    Schema = mongoose.Schema,
    db = mongoose.createConnection('mongodb://localhost/evan');


var Question = new Schema({
    text: { type: String, required: true, trim: true, unique: true },
    
    presentation_id: { type: String, required: true },
    created: { type: Date, default: Date.now },

    votes: [{ type: String }]
});



var model = db.model('Question', Question);

exports = module.exports = model;