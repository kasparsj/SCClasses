Timeline {
	classvar <instances;

	var <name;
	var <sections;
	var <clock;
	var <quant;
	var <>playFunc;
	var <>stopFunc;
	var <isPlaying = false;
	var <currentSection = -1;
	var <currentLoop;
	var <repeats = 1;
	var repeat;
	var loopTl;

	*initClass {
		instances = ();
	}

	*new { |name, sections, clock, quant = 1|
		var instance = instances[name.asSymbol];
		name = name.asSymbol;
		if (instance == nil) {
			instance = super.newCopyArgs(name, sections);
			instances[name] = instance;
			CmdPeriod.add { instance.deinit(); }
		} {
			instance.sections_(sections);

		};
		instance.clock_(clock);
		instance.quant_(quant);
		^instance;
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
		if (loopTl != nil, {
			loopTl.stop;
			currentLoop = nil;
		});
	}

	goto { |section_, quant_ = nil|
		currentSection = this.index(section_);
		clock.play({ this.prPlay() }, quant_ ? quant);
	}

	prPlay {
		var section = this.at(currentSection);
		"TL: playing % for %".format(section[0], section[1]).postln;
		this.prStop;
		this.prSetIsPlaying;
		section[2].value;
		clock.sched(section[1], { this.next() });
	}

	prSetIsPlaying {
		if (isPlaying.not) {
			(playFunc ? {}).value;
			isPlaying = true;
		};
	}

	next {
		currentSection = currentSection + 1;
		if (currentSection >= (sections.size / 3), {
			repeat = repeat + 1;
			if (repeat >= repeats, {
				^this.stop;
			});
			currentSection = 0;
		});
		if (isPlaying.not) {
			this.goto(currentSection);
		} {
			this.prPlay();
		};
	}

	prev {
		currentSection = currentSection - 1;
		if (currentSection < 0) {
			repeat = repeat - 1;
			currentSection = (sections.size / 3) - 1
		};
		this.goto(currentSection);
	}

	loop { |section_, quant_ = nil, repeats_ = inf|
		if (section_.isArray.not, {
			section_ = [section_];
		});
		currentLoop = section_;
		this.prLoop(quant_, repeats_);
	}

	prLoop { |quant_ = nil, repeats_ = nil|
		var secs = [];
		currentLoop.do { |sec|
			secs = secs.addAll(this.at(sec));
		};
		this.prSetIsPlaying;
		loopTl = Timeline(name.asString ++ "_loop", secs, clock, quant_ ? quant);
		loopTl.play(0, repeats_);
	}

	at { |key|
		var idx = this.index(key);
		^[sections[(idx*3)], sections[((idx*3)+1)], sections[((idx*3)+2)]];
	}

	put { |key, value|
		var idx = this.index(key);
		if (idx != nil) {
			sections[(idx*3)] = key;
			sections[((idx*3)+1)] = value[0];
			sections[((idx*3)+2)] = value[1];
		} {
			sections = sections.addAll([key, value[0], value[1]]);
		};
	}

	index { |key|
		^if (key.isSymbol, {
			block { |break|
				(sections.size / 3).do { |i|
					if (sections[(i*3)] == key) {
						break.value(i);
					};
				};
				break.value(nil);
			};
		}, { key });
	}

	sections_ { |values|
		sections = values;
		if (isPlaying) {
			if ((loopTl != nil) && (loopTl.isPlaying)) {
				this.prLoop(loopTl.quant, loopTl.repeats);
			} {
				this.prPlay;
			};
		};
	}

	clock_ { |value|
		clock = value;
		if (clock.permanent.not) {
			"TL: clock is not permanent".postln;
		};
	}

	quant_ { |value|
		quant = value;
	}
}
