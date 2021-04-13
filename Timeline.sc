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

	}

	play { |section_ = 0, repeats_ = 1|
		repeats = repeats_;
		repeat = 0;
		(playFunc ? {}).value;
		this.goto(section_);
	}

	stop {
		clock.clear;
		isPlaying = false;
		(stopFunc ? {}).value;
	}

	goto { |section_|
		section = section_;
		isPlaying = true;
		clock.play(this.play_, quant);
	}

	play_ {
		clock.clear;
		"playing % (%)".format(section, clock.beats).postln;
		sections[(section*2)+1].value;
		"scheduling %".format(sections[(section*2)]).postln;
		clock.sched(sections[(section*2)], { this.next() });
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
			this.play_();
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
