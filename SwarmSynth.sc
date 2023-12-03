SwarmSynth {
    var <>synthDef, <>defaultParams, <>synths, <>params;

    *new { |synthDef, defaultParams|
		^super.newCopyArgs(synthDef, defaultParams, [], []);
    }

	parseParams { |i, params|
		if (params.isFunction) {
			^params.(i);
		} {
			^if (params.notNil, params, []);
		}
	}

	mergeParams { |p1, p2|
		var dict = Dictionary.newFrom(p1);
		p2.pairsDo { |key, value|
			dict.put(key, value);
		};
		^dict.asPairs;
	}

	prRealParams { |i, params|
		^this.mergeParams(this.parseParams(i, this.defaultParams), this.parseParams(i, params));
	}

	prUpdateParams { |i, parsedParams|
		this.params[i] = this.mergeParams(this.params[i], parsedParams);
	}

    prCreateSynth { |params|
        ^Synth(this.synthDef, params);
    }

	size {
		^synths.size;
	}

	add { |num, params|
		num.do {
			var realParams = this.prRealParams(this.size, params);
			this.params = this.params.add(realParams);
			this.synths = this.synths.add(this.prCreateSynth(realParams));
		};
	}

	addAt { |i, num, params|
		num.do {
			var realParams = this.prRealParams(i, params);
			this.params = this.params.insert(i, realParams);
			this.synths = this.synths.insert(i, this.prCreateSynth(realParams));
		};
	}

	drop { |num|
		var newNumSynths = this.size - num;
		synths.drop(newNumSynths).do { |synth|
			synth.set(\gate, 0);
		};
		synths = synths.keep(newNumSynths);
		params = params.keep(newNumSynths);
	}

    set { |i, params|
        if(i < this.size, {
			var parsed = this.parseParams(i, params);
			synths[i].set(*parsed);
			this.prUpdateParams(i, parsed);
        });
    }

	setAll { |params|
		synths.do { |synth, i|
			var parsed = this.parseParams(i, params);
			synth.set(*parsed);
			this.prUpdateParams(i, parsed);
        };
	}

    fadeOut { |i|
        if(i < this.size, {
			var synth = synths.removeAt(i);
            synth.set(\gate, 0);
			params.removeAt(i);
        });
    }

	fadeOutAll {
		synths.do { |synth|
			synth.set(\gate, 0);
        };
        synths = [];
		params = [];
	}

	release { |i|
		var synth = synths.removeAt(i);
		synth.free;
		params.removeAt(i);
	}

    releaseAll {
        synths.do { |synth|
            synth.free;
        };
        synths = [];
		params = [];
    }

	asString {
        ^params.asString;
	}
}
