SwarmControl {
	var <ndef, <numOscs, <>params;

    *new { |ndef, numOscs|
		^super.newCopyArgs(ndef, numOscs, Dictionary.new);
    }

	getPairs { |i|
		var params = [];
		this.params.keys.do { |key|
			params = params.addAll([key, this.params.at(key)[i]]);
		}
		^params;
	}

	getDict { |i|
		var dict = Dictionary.new;
		this.params.keys.do { |key|
			dict.put(key, this.params.at(key)[i]);
		}
		^dict;
	}

	parseParams { |i, params, j|
		if (params.isFunction) {
			^params.(i, this.getPairs(i), j);
		} {
			^if (params.notNil, params, []);
		}
	}

	prSetParams { |i, params|
		^params.pairsDo { |key, value|
			if (this.params[key].isNil) {
				this.params.put(key, Array.fill(numOscs, 0));
			};
			this.params.at(key)[i] = value;
		};
	}

	prSet { |params|
		params.pairsDo { |key, value|
			ndef.set(key, this.params.at(key));
		};
	}

	prXset { |params|
		params.pairsDo { |key, value|
			ndef.xset(key, this.params.at(key));
		};
	}

    set { |params, from, to|
		var parsed;
		if (from.isNil) {
			from = 0;
			to = numOscs-1;
		};
		if (to.isNil) {
			parsed = this.parseParams(from, params, 0);
			this.prSetParams(from, parsed);
		} {
			(from..to).do { |i, j|
				parsed = this.parseParams(i, params, j);
				this.prSetParams(i, parsed);
			};
		};
		this.prSet(parsed);
    }

	xset { |params, from, to|
		var parsed;
		if (from.isNil) {
			from = 0;
			to = numOscs-1;
		};
		if (to.isNil) {
			parsed = this.parseParams(from, params, 0);
			this.prSetParams(from, parsed);
		} {
			(from..to).do { |i, j|
				parsed = this.parseParams(i, params, j);
				this.prSetParams(i, parsed);
			};
		};
		this.prXset(parsed);
    }

	reset { |from, to|
		var dict;
		if (from.isNil) {
			from = 0;
			to = numOscs-1;
		};
		dict = this.getDict(from);
		dict.pairsDo { |key, value|
			dict.put(key, 0);
		};
		this.set(dict.asPairs, from, to);
	}

    fadeIn { |amp=1, from, to|
		if (from.isNil) {
			from = 0;
			to = numOscs-1;
		};
		this.xset([\amp, amp], from, to);
    }

    fadeOut { |from, to|
		if (from.isNil) {
			from = 0;
			to = numOscs-1;
		};
		this.xset([\amp, 0], from, to);
    }

	asString {
        ^params.asString;
	}
}