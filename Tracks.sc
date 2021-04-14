Tracks {

	var <tracks;
	var <defaultClock;
	var <players;
	var <mutes;

	*new { |tracks, clock = nil|
		var instance = super.newCopyArgs(tracks, clock);
		instance.init();
		^instance;
	}

	init {
		if (tracks.isArray, {
			tracks = Dictionary.newFrom(tracks);
		});
		players = ();
		mutes = Set[];
	}

	at { |key|
		^tracks[key];
	}

	put { |key, value|
		tracks[key] = value;
	}

	play { |which = nil, clock = nil|
		which = which ? tracks.keys;
		clock = clock ? defaultClock;
		which.do { |key|
			if (players[key] != nil) {
				if (players[key].clock != (clock ? TempoClock.default)) {
					this.prStop(key);
				};
			};
			players[key] = tracks[key].play(clock);
		};
		this.prMuteUnmute;
	}

	stop { |which = nil|
		which = which ? tracks.keys;
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
		mutes = mutes - (which ? tracks.keys).asSet);
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
		^(tracks.keys - (if (which.isSymbol, { [which] }, { which }).asSet));
	}

	prStop { |key|
		players[key].stop;
		players.removeAt(key);
	}

}
