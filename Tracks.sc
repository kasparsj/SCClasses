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
		which.do { |key|
			if (players[key].isPlaying.not) {
				players[key] = tracks[key].play(clock ? defaultClock);
			};
		};
	}

	stop { |which = nil|
		which = which ? tracks.keys;
		which.do { |key|
			if (players[key] != nil) {
				players[key].stop;
			};
		};
	}

}
