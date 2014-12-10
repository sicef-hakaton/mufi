var mongoose = require('mongoose'),
    Schema = mongoose.Schema,
    db = mongoose.createConnection('mongodb://localhost/evan');


var User = new mongoose.Schema({
    name: { type: String, required: true, trim: true, unique: true },
    about: { type: String, trim: true },
    
    email: { type: String, required: true, trim: true, lowercase: true, unique: true },
    
    password: { type: String, required: true },
    created: { type: Date, default: Date.now }

});


User.methods.validPassword = function (password) {
    if (this.password === password) 
        return true;

    return false;
};

var model = db.model('User', User);

exports = module.exports = model;