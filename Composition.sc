Composition {
	var <tracks, <times;

	*new { |tracks = nil, times = nil|
		^super.new.tracks_(tracks).times_(times)
	}

	play { |clock, protoEvent, quant|
		this.asPtpar.play(clock, protoEvent, quant);
	}

	tracks_ { |newTracks|
		tracks = (newTracks ? ()).asDict;
	}

	times_ { |newTimes|
		times = (newTimes ? []).asArray;
	}

	names {
		var names = [];
		tracks.pairsDo { |key, val|
			names = names.add(key)
		}
		^names;
	}

	asPtpar { |tracks, times|
		tracks = tracks ? this.tracks;
		times = times ? this.times;
		times.do { |key, i|
			if (tracks[key] != nil) {
				times[i] = tracks[key];
			};
		};
		^Ptpar(times);
	}
}
