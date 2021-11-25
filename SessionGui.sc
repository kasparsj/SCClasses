SessionGui {
	var <session;
	var <window;

	var windowWidth = 960;
	var windowHeight = 480;
	var clipSize;
	var selectedClip = 0;

	*new { |session|
		^super.newCopyArgs(session).init;
	}

	init {
		this.createWindow;
		this.onResize;
		this.update;
	}

	createWindow {
		window = Window(
			name: "SessionGui",
			bounds: Rect.new(
				left: 100,
				top: 100,
				width: windowWidth,
				height: windowHeight
			),
			resizable: true
		);

		window.view.onResize = { |view|
			this.onResize;
		};
		window.view.keyDownAction = { |doc, char, mod, unicode, keycode, key|
			var i;
			switch(keycode,
				126, { this.setClipVol(-0.1); }, // up
				125, { this.setClipVol(0.1); }, // down
				123, { this.prevClip; }, // left
				124, { this.nextClip; }, // right
				49, { this.play; }, // space
				{
					keycode.postln;
					//if(char.isAlpha) {
					//	i = names.detectIndex { |x| x.asString[0] == char };
					//	i !? { filesMenu.value = i }
					//}
				}
			);
			this.update;

		};
	}

	onResize {
		var newSize = [window.bounds.width, window.bounds.height].maxItem.min(200);
		if (newSize != clipSize) {
			clipSize = newSize;
			window.layout = VLayout();
			window.front;
			this.createClips;
		};
	}

	createClips {
		var clips = [];
		session.clips.keys.do { |name|
			clips = clips.add(TextView.new.background_(Color.rand).minSize_(clipSize@clipSize).string_(name));
		};
		window.layout.add(HLayout(*clips));
	}

	prevClip {
		selectedClip = selectedClip - 1;
		if (selectedClip < 0) {
			selectedClip = session.clips.size - 1;
		};
	}

	nextClip {
		selectedClip = selectedClip + 1;
		if (selectedClip >= session.clips.size) {
			selectedClip = 0;
		};
	}

	setClipVol { |delta|
	}

	update {
	}
}