Tracks {

	var <tracks;
	var <defaultClock;
	var <players;

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
	}

	stop { |which = nil|
		which = which ? tracks.keys;
		which.do { |key|
			if (players[key] != nil) {
				this.prStop(key);
			};
		};
	}

	except { |which|
		^(tracks.keys - (if (which.isSymbol, { [which] }, { which }).asSet));
	}

	prStop { |key|
		players[key].stop;
		players.removeAt(key);
	}

}
