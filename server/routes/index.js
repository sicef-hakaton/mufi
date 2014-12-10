var express = require('express');
var fs = require('fs');
var request = require('request');
var crypto = require('crypto');
var googleapis = require('googleapis');
var router = express.Router();

var CLIENT_ID = '846221377468-jli3gaffq96jl4c5a74scpbnkvbc71ud.apps.googleusercontent.com',
CLIENT_SECRET = 'gNfIErIWeiIjIDAV7d86dTJs',
REDIRECT_URL = 'http://localhost:3000/oauth2callback';

var INJECT_JS = fs.readFileSync('inject.js');

var scopes = [
	'https://www.googleapis.com/auth/drive.file'
];
var oauth2Client = new googleapis.auth.OAuth2(CLIENT_ID, CLIENT_SECRET, REDIRECT_URL);
googleapis.options({ auth: oauth2Client });

function sha1file(filename, callback) {
	var shasum = crypto.createHash('sha1');

	var s = fs.ReadStream(filename);
	s.on('data', function(d) {
		shasum.update(d);
	});

	s.on('end', function() {
		var d = shasum.digest('hex');
		callback(d);
	});
}

router.get('/loginG', function(req, res) {
	var url = oauth2Client.generateAuthUrl({
	  access_type: 'online',
	  scope: scopes
	});
	res.redirect(url);
});

router.get('/oauth2callback', function(req, res) {
	oauth2Client.getToken(req.query.code, function(err, tokens) {
		if(!err) {
			oauth2Client.setCredentials(tokens);
			req.session.token = tokens;
			res.redirect('/upload');
		} else {
			console.log('error', err);
			res.send('error');
		}
	});
});

router.post('/sendFile', function(req, res) {
	sha1file(req.files.pptFile.path, function(sha1) {
		console.log(sha1);
		var pptFilename = global.publicPath + '/presenters/'+sha1+'.html';
		if (fs.existsSync(pptFilename)) {
			res.send(sha1);
		} else {
			var drive = googleapis.drive({ version: 'v2' });
			drive.files.insert({
				convert: true,
				resource: {
					title: req.files.pptFile.originalname,
					mimeType: req.files.pptFile.mimetype
				},
				media: {
					mimeType: req.files.pptFile.mimetype,
					body: fs.createReadStream(req.files.pptFile.path)
				}
			}, function(err, fileResponse) {
				if (!err) {
					drive.permissions.insert({
						fileId: fileResponse.id,
						resource: {
							type: 'anyone',
							role: 'reader'
						}
					}, function(err, resp) {
						request({
							url: fileResponse.embedLink,
							headers: {
								'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/38.0.2125.111 Chrome/38.0.2125.111 Safari/537.36',
								'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8'
							}
						}, function (error, response, body) {
							if (!error) {
								var newBody = body.replace(/\/static\/presentation\//g, 'https://docs.google.com/static/presentation/');
								newBody = newBody.replace('</body>', '<script>'+INJECT_JS+'</script></body>');

								fs.writeFileSync(pptFilename, newBody);
								//drive.files.delete({ fileId: fileResponse.id });
								res.send(sha1);
							} else {
								res.send(err);
							}
						});
					});
				} else {
					res.send(err);
				}
			});
		}
	});
});

router.get('/upload', function(req, res) {
	if (req.session.token) res.render('upload', { title: 'Express' });
	else res.redirect('/loginG');
});

router.get('/display/:id', function(req, res) {
	res.render('display-projector', { id: req.params.id });
});

router.get('/check/:id', function(req, res) {
	if (fs.existsSync(global.publicPath + '/presenters/'+req.params.id+'.html')) {
		res.send('true');
	} else {
		res.send('false');
	}
});

module.exports = router;
