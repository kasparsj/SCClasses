Session {
	classvar <instances;

	var <clips;
	var <defaultClock;
	var <players;
	var <mutes;

	*initClass {
		instances = ();
	}

	*new { |name, clips, clock = nil|
		var instance = instances[name.asSymbol];
		if (instance == nil) {
			instance = super.newCopyArgs(clips);
			instance.init();
			instances[name.asSymbol] = instance;
			//CmdPeriod.add { instance.deinit(); }
		} {
			instance.clips_(clips);

		};
		instance.clock_(clock);
		^instance;
	}

	init {
		this.clips_(clips);
		players = ();
		mutes = Set[];
	}

	at { |key|
		^clips[key];
	}

	put { |key, value|
		clips[key] = value;
	}

	play { |which = nil, clock = nil|
		which = which ? clips.keys;
		clock = clock ? defaultClock;
		which.do { |key|
			if (players[key] != nil) {
				if (players[key].clock != (clock ? TempoClock.default)) {
					this.prStop(key);
				};
			};
			players[key] = clips[key].play(clock);
		};
		this.prMuteUnmute;
	}

	stop { |which = nil|
		which = which ? clips.keys;
		which.do { |key|
			if (players[key] != nil) {
				this.prStop(key);
			};
		};
	}

	mute { |which|
		mutes = mutes ++ (if (which.isSymbol, { [which] }, { which }).asSet);
		this.prMuteUnmute;
	}

	solo { |which|
		mutes = this.except(which).asSet;
		this.prMuteUnmute
	}

	unmute { |which = nil|
		which = (if (which.isSymbol, { [which] }, { which });
		mutes = mutes - (which ? clips.keys).asSet);
		this.prMuteUnmute;
	}

	prMuteUnmute {
		players.keys.do { |key|
			if (mutes.includes(key)) {
				players[key].mute;
			} {
				players[key].unmute;
			};
		}
	}

	except { |which|
		^(clips.keys - (if (which.isSymbol, { [which] }, { which }).asSet));
	}

	prStop { |key|
		players[key].stop;
		players.removeAt(key);
	}

	clips_ { |values|
		clips = values;
		if (clips.isArray) {
			clips = Dictionary.newFrom(clips);
		};
	}

	clock_ { |value|
		defaultClock = value;
	}
}
