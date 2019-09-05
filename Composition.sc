Composition {
	var <type;
	var <tracks;
	var <times;
	var <weights;
	var <>repeats = 1;

	*new { |tracks = nil|
		^super.newCopyArgs(Ppar).tracks_(tracks);
	}

	play { |clock, protoEvent, quant|
		this.asPtpar.play(clock, protoEvent, quant);
	}

	tracks_ { |newTracks|
		tracks = (newTracks ? ()).asDict;
	}

	times_ { |newTimes|
		times = (newTimes ? []).asArray;
		type = if (times.isEmpty, Ppar, Ptpar);
	}

	weights_ { |newWeights|
		weights = (newWeights ? []).asArray;
		type = if (weights.isEmpty, Ppar, Pwrand);
	}

	names {
		var names = [];
		tracks.pairsDo { |key, val|
			names = names.add(key)
		}
		^names;
	}

	asPattern { |... args|
		^switch (type,
			Ppar, { this.asPpar(*args) },
			Ptpar, { this.asPtpar(*args) },
			Pwrand, { this.asPwrand(*args) },
		);
	}

	asPpar { |tracks, repeats|
		tracks = tracks ? this.tracks;
		repeats = repeats ? this.repeats;
		tracks = (if (tracks.isArray, tracks, tracks.values)).collect { |track|
			this.prTrack(track);
		};
		^Ppar(tracks, repeats);
	}

	asPtpar { |times, tracks, repeats|
		times = (times ? this.times).asArray;
		tracks = (tracks ? this.tracks).asDict;
		repeats = repeats ? this.repeats;
		times.do { |key, i|
			if (tracks[key] != nil) {
				times[i] = this.prTrack(tracks[key]);
			};
		};
		^Ptpar(times, repeats);
	}

	asPwrand { |weights, tracks, repeats|
		var tracksArray, weightsArray;
		weights = (weights ? this.weights).asArray;
		tracks = (tracks ? this.tracks).asDict;
		repeats = repeats ? this.repeats;
		tracksArray = [];
		weightsArray = [];
		weights.pairsDo { |value, key|
			if (tracks[key] != nil) {
				tracksArray = tracksArray.add(this.prTrack(tracks[key]));
				weightsArray = weightsArray.add(value);
			};
		};
		^Pwrand(tracksArray, weightsArray, repeats);
	}

	prTrack { |track|
		^if (track.class == Composition, {
			track.asPattern;
		}, track);
	}
}
