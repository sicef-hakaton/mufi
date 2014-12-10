window.slideChange = function(slide, count) {
	localStorage.setItem('slide', slide);
	localStorage.setItem('slideCount', count);
};

window.onbeforeunload = function(e) {
	localStorage.setItem('slide', -1);
};
