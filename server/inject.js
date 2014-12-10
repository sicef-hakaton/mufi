var SlideMonitor = {
	currentSlide: 0,

	nodeEvent: function() {
		var newSlide = parseInt(document.getElementById(':s').getAttribute('aria-posinset'));
		if (newSlide != this.currentSlide) {
			var slideCount = parseInt(document.getElementById(':s').getAttribute('aria-setsize'));
			this.currentSlide = newSlide;
			if (parent.slideChange) parent.slideChange(this.currentSlide, slideCount);
		}
	},

	init: function() {
		document.getElementsByClassName('punch-viewer-content')[0].addEventListener('DOMNodeInserted', SlideMonitor.nodeEvent.bind(SlideMonitor), false);
		setTimeout(SlideMonitor.nodeEvent.bind(SlideMonitor), 1000);
	}
};

SlideMonitor.init();