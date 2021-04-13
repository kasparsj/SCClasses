Timeline {

	var <sections;
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
		if (isPlaying.not) {
			repeat = 0;
			(playFunc ? {}).value;
			this.goto(section_, quant_);
		};
	}

	stop {
		// todo: not sure this is a good idea
		clock.clear;
		isPlaying = false;
		(stopFunc ? {}).value;
	}

	goto { |section_, quant_ = nil|
		section = section_;
		isPlaying = true;
		clock.play({ this.prPlay() }, quant_ ? quant);
	}

	prPlay {
		var beats = sections[(section*2)];
		"Timeline: playing section % for %".format(section, beats).postln;
		clock.clear;
		sections[(section*2)+1].value;
		clock.sched(beats, { this.next() });
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

	loop { |section_, repeats_ = inf|
		var secs = [];
		if (section_.isArray.not, {
			section_ = [section_];
		});
		section_.do {|sec|
			secs = secs.addAll([sections[sec*2], sections[(sec*2)+1]]);
		};
		loopTl = Timeline.new(secs, clock, quant);
		loopTl.play(0, repeats_);
	}
}
