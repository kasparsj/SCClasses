Composition {
	var <type;
	var <players;
	var <tracks;
	var <times;
	var <weights;
	var <>repeats = 1;
	var <>clock;
	var <>quant;

	*new { |tracks = nil|
		^super.newCopyArgs(Ppar, []).tracks_(tracks);
	}

	play { |clock, protoEvent, quant|
		this.prPlay(this.asPattern, clock, protoEvent, quant);
	}

	playTrack { |key, clock, protoEvent, quant|
		this.prPlay(this.prTrack(tracks[key], clock, protoEvent, quant));
	}

	prPlay { |pattern, clock, protoEvent, quant|
		players = players.add(pattern.play((clock ? this.clock), protoEvent, (quant ? this.quant)));
	}

	stop {
		if (players.size > 0) {
			players.do { |p| p.stop };
			players = [];
		};
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

	asPtpar { |tracks, repeats, times|
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

	asPwrand { |tracks, repeats, weights|
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
		^Pwrand(tracksArray, weightsArray.normalizeSum, repeats);
	}

	prTrack { |track|
		^if (track.class == Composition, {
			track.asPattern;
		}, track);
	}

	mapToEvent { |protoEvent, dict|
		var newTracks = ();
		dict = dict.asDict;
		tracks.pairsDo { |key, track|
			if (track.class == Composition) {
				newTracks[key] = track.mapToEvent(protoEvent, dict);
			} {
				newTracks[key] = track.copy;
				if (dict[key] != nil) {
					var pbind = newTracks[key];
					if (pbind.respondsTo(\patternpairs).not) {
						pbind = pbind.pattern;
					};
					pbind.patternpairs = pbind.patternpairs.addAll([
						\mapFunc, Pfunc { |event|
							var pairs = dict[key].value(event);
							if (pairs != nil) {
								var mappedEvent = protoEvent.copy;
								mappedEvent[\dur] = event[\dur];
								mappedEvent[\amp] = event[\amp];
								mappedEvent.putPairs(pairs);
								mappedEvent.play;
							};
							true;
						}
					]);
				};
			}
		};
		^this.copy.tracks_(newTracks);
	}

	mapToOsc {
		// todo: implement
	}
}
