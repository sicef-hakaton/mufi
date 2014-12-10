var express = require('express');
var path = require('path');
var logger = require('morgan');
var multer  = require('multer');

var app = express();

var User = require('./private/user');
var Presentation = require('./private/presentation');
var Question = require('./private/question');



var bodyParser = require('body-parser');
app.use( bodyParser.json() );       // to support JSON-encoded bodies
app.use(bodyParser.urlencoded({     // to support URL-encoded bodies
  extended: true
})); 

app.use(multer({ dest: './uploads/'}));
app.use(require('express-session')({secret: "UkijevaMama"}));

app.use(express.static(path.join(__dirname, 'public')));

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');


var passport = require('passport'),
    PassportLocalStrategy = require('passport-local').Strategy;

//Login  logika
passport.use(
    new PassportLocalStrategy({
        usernameField: 'email',
        passwordField: 'password'
    },

    function (username, password, done) {
        User.findOne({email: username}, function (err, user) {

            if (err) { return done(err); }

            if (!user) {
                return done(null, false, { message: 'User not found.'});
            }
            if (!user.validPassword(password)) {
                return done(null, false, { message: 'Incorrect password.'});
            }

            return done(null, {
                id: user._id,
                name: user.name,
                email: user.email
            });
        });
    })
);


passport.serializeUser(function (user, done) { done(null, user); });
passport.deserializeUser(function (obj, done) { done(null, obj); });
app.use(passport.initialize());
app.use(passport.session());


app.use('/', require('./routes/index'));

// uncomment after placing your favicon in /public
//app.use(favicon(__dirname + '/public/favicon.ico'));

app.get('/', function (req, res) {
    if (!req.user) {
        req.user = false;
        res.render('index', req);
    } else {
        Presentation.find({ prof_id: req.user.id }, function (err, presentations) {
            req.presents = presentations;
            res.render('homepage', req);
        });
    }
});


app.post('/login', passport.authenticate('local', { successRedirect: '/',
                                                    failureRedirect: '/login' }));
app.get('/logout', function (req, res) {
    req.logout();
    res.redirect('/');
});

app.get('/register', function(req, res) { res.render('register', {}); });
app.post('/register', function (req, res) {

    User.create({name: req.body.username, email: req.body.email, password: req.body.password }, function(err, user){
        if (err) {
            console.log(err);

            res.redirect('/register');
            return;
        }

        res.redirect('/'); 
    });
});

app.get('/presentation/new', function(req, res) {
    if (req.user)
        res.render("newPresentation", req);
    else
        res.redirect('/');
});
app.post('/presentation/new', function (req, res) {
    Presentation.create({ name: req.body.ime, password: req.body.sifra, prof_id: req.user.id }, function (err, present) {
        if (err) {
            console.log(err);
            
            res.redirect('/presentation/new');
            return;
        }

        res.redirect('/presentation/'+present._id+"/");
    });
});

app.get('/presentation/:id/', function (req, res) {
    Presentation.findOne({ _id: req.params.id }, function (err, present) {
        if (err) {
            console.log(err);
        }

        if (!req.user || (present.prof_id != req.user.id)) {
            res.redirect('/');
            return;
        }

        present.user = req.user;
        res.render('presentation', present);
    });
});

app.get('/presentation/:id/delete', function (req, res) {
    console.log("deleting " + req.params.id);
    if (!req.user) {
        res.redirect('/');
        return;
    }

    Presentation.findByIdAndRemove(req.params.id , function (err, offer) {
        if (err) {
            console.log(err);
        }
        res.redirect('/');  
    });
});



// catch 404 and forward to error handler
app.use(function(req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});


// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
    app.use(function(err, req, res, next) {
        res.status(err.status || 500);
        res.render('error', {
            message: err.message,
            error: err
        });
    });
}

var server = require('http').Server(app);  //za socket.io
server.listen(3000);

module.exports = app;


var oi = require('socket.io')(server);
oi.on('connection', function (socket) {
    var _present;
    console.log('usao ' + socket.id);

    //PROFESOR
    socket.on('joinProf', function (presentationID) {
        console.log(socket.id);
        socket.join(presentationID);
        _present = presentationID;
    });
    socket.on('deleteQuestion', function (questionID) {
        console.log("obrisano pitanje: ", questionID);
        Question.findOneAndRemove({ _id: questionID }, function () {
            socket.broadcast.to(_present).emit('questionDeleted', { id: questionID });
        });
    });
    
    socket.on('newPoll', function (data) {
        socket.broadcast.to(_present).emit('newPoll', data);
    });



    //UCENIK
    socket.on('joinStud', function (data) {
        Presentation.findOne({ name: data.roomName, password: data.roomPassword }, function (err, presentation) {
            if (presentation)
                User.findOne({ _id: presentation.prof_id }, function (err, proffessor) {
                    socket.join(presentation._id);
                    _present = presentation._id;


                    Question.find({ presentation_id: presentation._id }, function (err, questions) {
                        var newQ = new Array(questions.length);
                        for (var i=0; i<questions.length; i++) {
                            newQ[i] = {
                                votes: questions[i].votes.length,
                                text : questions[i].text,
                                id   : questions[i]._id
                            }
                        }

                        socket.emit("success", {
                            prof_name: proffessor.name,
                            presentation_name: presentation.name,
                            questions: newQ
                        });
                    });
                });
            else {
                socket.emit('fail');
            }
        });
    });
    
 
    socket.on('newQuestion', function (data) {
        Question.create({text: data.text, presentation_id: _present}, function (err, question) {
            if (err) {
                console.log(err);
                return;
            }

            socket.broadcast.to(_present).emit('newQuestion', {
                text : question.text,
                id   : question._id,
                votes: question.votes.length
            });
            socket.emit('newQuestion', {
                text : question.text,
                id   : question._id,
                votes: question.votes.length
            });
        });
    });
    
    socket.on('deleteVote', function (data) {
        var questionID = data.id;

        Question.findByIdAndUpdate( 
            questionID,
            { $pull: { "votes": socket.id }},
            { safe: true, upsert: true },
            function (err, presentation) {
                socket.broadcast.to(_present).emit('deleteVote', {
                    id_question: questionID,
                    id_user: socket.id
                });
        });
    });
    socket.on('newVote', function (data) {
        var questionID = data.id;
    
        Question.findByIdAndUpdate( 
            questionID,
            { $push: { "votes": socket.id }},
            { safe: true, upsert: true },
            function (err, presentation) {
                socket.broadcast.to(_present).emit('newVote', {
                    id_question: questionID,
                    id_user: socket.id
                });
        });
        
    });

    socket.on('answerPoll', function (data) {
        socket.broadcast.to(_present).emit('answerPoll', data);
    });

    socket.on('understandingLevelChanged', function (data) {
        data = data.level;
        
        Presentation.findByIdAndUpdate( 
            _present,
            { $push: { "votesTimeline": { "value": data, "id_user": socket.id }}},
            { safe: true, upsert: true },
            function (err, presentation) {
                socket.broadcast.to(_present).emit('sliderUpdate', {
                    newData: data,
                    id_user: socket.id
                });
        });
    });

    socket.on("disconnect", function () {
        console.log("disconnected "+socket.id);

        Presentation.findByIdAndUpdate( 
            _present,
            { $push: { "votesTimeline": { value: -1, id_user: socket.id }}},
            { safe: true, upsert: true },
            function (err, presentation) {
                socket.broadcast.to(_present).emit('sliderUpdate', {
                    newData: -1,
                    id_user: socket.id
                });
        });
    });

});