var socket;
var presentationId = '9d6e8cf106942df29dbc2616f6d9f43b76fbc1fa';

$(function() {
	var presentID = document.URL.split('?')[0].split('/');
		presentID = presentID[presentID.length - 2];

	socket = io.connect("http://localhost:3000");
	socket.emit('joinProf', presentID);

	socket.on('newQuestion', function (data) {
		console.log(data);
		Questions.addQuestion(data.id, data.text);
	});
	socket.on('newVote', function (data) {
		Questions.setVoteCount(data.id, data.votes);
	});

	socket.on('answerPoll', function (data) {
		console.log(data);
		updateColumnDataPoint(1, data.choice-1);
	});


	var users = {};
	socket.on('sliderUpdate', function (data) {
		/* value, id_user */

		if (data.value < 0)
			delete users[data.id_user];
		else
			users[data.id_user] = data.newData;

	});
	setInterval(function  () {
		var sum = 0;
		var i = 0;
		for (var user in users) {
			sum += users[user];
			i++;
		}

		if (Math.round(sum/i) >= 0)
			updateLineData(new Date(), Math.round(sum/i));

	}, 3000);



	localStorage.setItem('slide', 0);
	$(window).on('storage', function (e) {
		if (localStorage.getItem('slide') == -1) {
			console.log('b');
			$('#navbar .btn').show();
			$('#slide').hide();
		} else {
			$('#navbar .btn').hide();
			$('#slide').show();
			$('#slide').html('Current slide: '+localStorage.getItem('slide')+'/'+localStorage.getItem('slideCount'));
		}
	});


	$('#ask-form .btn').click(function() {
		var answers = $(this).attr('data-number');
		$('#ask-form').animate({ height: 0 }, 700, function() {
			$(this).hide();
			$('#ask-results').show();
			$('#ask-results').animate({ height: $('#line_chart_div').height()+'px' }, 700, function() {
				initColumn(answers);
				$('#panel-back').show();
			});
		});

		socket.emit('newPoll', {number: answers});
	});

	$('#panel-back').click(function() {
		$(this).hide();
		$('#ask-results').animate({ height: 0 }, 700, function() {
			$(this).hide();
			$('#ask-form').show();
			$('#ask-form').animate({ height: $('#line_chart_div').height()+'px' }, 700);
		});
	});

	$('#navbar .btn').click(function() {
		$(this).hide();
		$('#slide').show();

		var params = [
			'height='+screen.height,
			'width='+screen.width,
			'fullscreen=yes'
		].join(',');
		var popup = window.open('http://localhost:3000/display/'+presentationId, presentationId, params); 
		popup.moveTo(0,0);
 	});

	function askShowResults() {
		$('.ask-form').animate({'width': '0'}, 700, function() {
			$(this).hide();
			$('.ask-results').show();
			$('.ask-results').animate({'width': '100%'}, 700);
		});
	}

	function askHideResults() {
		$('.ask-results').animate({'width': '0'}, 700, function() {
			$(this).hide();
			$('.ask-form').show();
			$('.ask-form').animate({'width': '100%'}, 700);
		});
	}
});

var Questions = {
	el: $('#questions'),

	data: [],

	addQuestion: function(id, text) {
		this.data.push({id: id, text: text, votes: 0});

		this.render();
	},

	deleteQuestion: function(id) {
		for (var i=0; i<this.data.length; i++) {
			if (this.data[i].id === id) {
				this.data.splice(i, 1);
				break;
			}
		}

		this.render();
	},

	setVoteCount: function(id, count) {
		for (var i=0; i<this.data.length; i++) {
			if (this.data[i].id === id) {
				this.data[i].votes = count;
				break;
			}
		}
		this.render();
	},

	render: function() {
		this.data.sort(function(a, b) {
			return b.votes - a.votes;
		});
		var html = '';
		for (var i=0; i<this.data.length; i++) {
			html += '<div class="question" data-qID="'+this.data[i].id+'">'+this.data[i].text+'<span class="badge badge-danger">'+this.data[i].votes+'</span></div>';
		}
		this.el.html(html);
		$('.question').click(function() {
			$(this).fadeOut(700, function() {
				$(this).remove();
			});

			var qID = $(this).attr('data-qid');

			socket.emit('deleteQuestion', qID);
		});

	}
};
