SwarmSynth {
    var <>synthDef, <>defaultParams, <>synths, <>params;

    *new { |synthDef, defaultParams|
		^super.newCopyArgs(synthDef, defaultParams, [], []);
    }

	parseParams { |i, params, j|
		if (params.isFunction) {
			^params.(i, this.params[i], j);
		} {
			^if (params.notNil, params, []);
		}
	}

	mergeParams { |p1, p2|
		var dict = Dictionary.newFrom(p1 ? []);
		(p2 ? []).pairsDo { |key, value|
			dict.put(key, value);
		};
		^dict.asPairs;
	}

	prUpdateParams { |i, parsedParams|
		var size = this.params.size;
		if (i >= size) {
			this.params = this.params.addAll(Array.fill(i+1-size, nil));
		};
		this.params[i] = this.mergeParams(this.params[i], parsedParams);
	}

    prCreateSynth { |i, params|
		var size = synths.size;
		if (i >= size) {
			this.synths = this.synths.addAll(Array.fill(i+1-size, nil));
		};
		this.synths[i] = Synth(this.synthDef, params);
		this.prUpdateParams(i, params);
    }

	prUpdateSynth { |i, params|
		synths[i].set(*params);
		this.prUpdateParams(i, params);
	}

	prSet { |i, params, j=0, createIfNotExists=true|
		var parsed;
		if (synths[i].isNil) {
			if (createIfNotExists) {
				parsed = this.mergeParams(this.parseParams(i, this.defaultParams, j), this.parseParams(i, params, j));
				this.prCreateSynth(i, parsed);
			}
		} {
			parsed = this.parseParams(i, params, j);
			this.prUpdateSynth(i, parsed);
		};
	}

	prXset { |i, params, j=0|
		var merged = this.mergeParams(this.params[i], this.parseParams(i, params, j));
		this.closeGate(i);
		this.set(merged, i);
	}

	size {
		^synths.size;
	}

	add { |params, num=1, at=nil|
		if (at.isNil) {
			at = this.size;
		};
		num.do { |i|
			this.set(params, (at + i));
		};
	}

    set { |params, from, to, createIfNotExists=true|
		var parsed;
		if (from.isNil) {
			from = 0;
			to = this.size-1;
		};
		if (to.isNil) {
			this.prSet(from, params, 0, createIfNotExists);
		} {
			(from..to).do { |i, j|
				this.prSet(i, params, j, createIfNotExists);
			};
		};
	}

	xset { |params, from, to|
		var merged;
		if (from.isNil) {
			from = 0;
			to = this.size-1;
		};
		if (to.isNil) {
			this.prXset(from, params, 0);
		} {
			(from..to).do { |i, j|
				this.prXset(i, params, j);
			};
		};
	}

	fadeIn { |amp=1, from, to|
		this.set([\amp, amp], from, to, false);
	}

	fadeOut { |from, to|
		this.set([\amp, 0], from, to, false);
	}

	prClose { |i|
		if (i >= 0 and: { i < this.size }) {
			synths[i] = nil;
			params[i] = nil;
		};
	}

    closeGate { |from, to|
		this.set([\gate, 0], from, to, false);
		if (from.isNil) {
			from = 0;
			to = this.size-1;
		};
		// release will happen automatically after fadeTime
		if (to.isNil) {
			this.prClose(from);
		} {
			(from..to).do { |i, j|
				this.prClose(i);
			};
		}
    }

	prRelease { |i|
		if (i >= 0 and: { i < this.size }) {
			synths[i].free;
			this.prClose(i);
		}
	}

	release { |from, to|
		if (from.isNil) {
			from = 0;
			to = this.size-1;
		};
		if (to.isNil) {
			this.prRelease(from);
		} {
			(from..to).do { |i|
				this.prRelease(i);
			};
		};
	}

	removeNil {
		var i = this.size - 1;
		while ({ i >= 0 }) {
			if (synths[i].isNil) {
				synths.removeAt(i);
				params.removeAt(i);
			};
			i = i - 1;
		};
	}

	asString {
        ^params.asString;
	}
}

/* additive synth example
(
SynthDef(\harmonic, {|out=0, freq=440, detune=0.3, lfo=0.1, fadeTime=1, gate=1, amp=1|
	var amp2 = amp, detune2 = detune, freq2 = freq, phase, sig, pan, lfo2 = lfo, env;
	env = EnvGen.kr(Env.adsr(fadeTime, 0.001, 1, fadeTime), gate, doneAction: 2);
	lfo2 = LFNoise0.kr(lfo2, 1.0);
	amp2 = LFNoise1.kr(lfo2, amp2);
	amp2 = LFNoise1.kr(lfo2, amp2);
	detune2 = LFNoise1.kr(lfo2, detune2).bipolar.midiratio;
	freq2 = freq2 * detune2;
	phase = LFNoise1.kr(lfo2).range(-2pi, 2pi);
	sig = SinOsc.ar(freq2, phase, amp2);
	sig = HPF.ar(sig, 100);
	sig = RLPF.ar(sig, 10000, 50);
	pan = LFNoise1.kr(lfo2).range(-1, 1);
	sig = Pan2.ar(sig, pan);
	Out.ar(0, sig * env);
}).add;
)
(
var root = 36.midicps;
var harmonics = 10;
harmonics.do { |i|
	var bf = 20;
	bf.do { |j|
		var freq, amp, phase, pan;
		freq = root * ((1+i*0.75)**2)+exprand(0.1, 2);
		amp = 1/(i+1)*0.1;
		phase = rrand(-2pi, 2pi);
		pan = rrand(-1, 1);
		~swarm.xset({[freq: freq, amp: amp, phase: phase, pan: pan]}, i*bf+j);
	};
}
)
*/