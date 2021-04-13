Timeline {

	var <>sections;
	var <clock;
	var <quant;
	var <>playFunc;
	var <>stopFunc;
	var <isPlaying = false;
	var <section = -1;
	var <repeats = 1;
	var repeat;
	var loopTl;

	*new { |sections, clock, quant = 1, playFunc = nil, stopFunc = nil|
		var instance = super.newCopyArgs(sections, clock, quant, playFunc, stopFunc);
		instance.init();
		^instance;
	}

	init {
		if (clock.permanent.not) {
			"Timeline: clock is not permanent".postln;
		};
		CmdPeriod.add { this.deinit(); }
	}

	deinit {
		isPlaying = false;
	}

	play { |section_ = 0, repeats_ = 1, quant_ = nil|
		repeats = repeats_;
		repeat = 0;
		this.goto(section_, quant_);
	}

	stop {
		this.prStop;
		isPlaying = false;
		loopTl = nil;
		(stopFunc ? {}).value;
	}

	prStop {
		// todo: not sure it's a good idea to clear the clock
		clock.clear;
	}

	goto { |section_, quant_ = nil|
		section = section_;
		clock.play({ this.prPlay() }, quant_ ? quant);
	}

	prPlay {
		var beats = sections[(section*2)];
		"Timeline: playing section % for %".format(section, beats).postln;
		this.prStop;
		this.prSetIsPlaying;
		sections[(section*2)+1].value;
		clock.sched(beats, { this.next() });
	}

	prSetIsPlaying {
		if (isPlaying.not) {
			(playFunc ? {}).value;
			isPlaying = true;
		};
	}

	next {
		section = section + 1;
		if (section >= (sections.size / 2), {
			repeat = repeat + 1;
			if (repeat >= repeats, {
				^this.stop;
			});
			section = 0;
		});
		if (isPlaying.not) {
			this.goto(section);
		} {
			this.prPlay();
		};
	}

	prev {
		section = section - 1;
		if (section < 0) {
			repeat = repeat - 1;
			section = (sections.size / 2) - 1
		};
		this.goto(section);
	}

	loop { |section_, quant_ = nil, repeats_ = inf|
		var secs = [];
		if (section_.isArray.not, {
			section_ = [section_];
		});
		section_.do {|sec|
			secs = secs.addAll([sections[sec*2], sections[(sec*2)+1]]);
		};
		this.prSetIsPlaying;
		if (loopTl == nil) {
			loopTl = Timeline.new(secs, clock, quant_ ? quant);
			loopTl.play(0, repeats_);
		} {
			loopTl.sections = secs;
			loopTl.goto(0);
		};
	}
}
