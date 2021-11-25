NPFactory {
	*mul { |source, mul|
		^NodeProxy.new.source_{ \source.kr * \mul.kr }.map(
			\source, source,
			\mul, mul,
		);
	}

	*min { |source, min|
		^NodeProxy.new.source_({ \source.kr.min(min) }).map(
			\source, source,
			\min, min,
		);
	}

	*max { |source, max|
		^NodeProxy.new.source_({ \source.kr.max(max) }).map(
			\source, source,
			\max, max,
		);
	}

	*noise0i { |freq, min, max|
		^NodeProxy.new.source_{ LFNoise0.kr(\freq.kr).range(\min.kr, \max.kr).round }.map(
			\freq, freq,
			\min, min,
			\max, max,
		);
	}

	*noise1i { |freq, min, max|
		^NodeProxy.new.source_{ LFNoise1.kr(\freq.kr).range(\min.kr, \max.kr).round }.map(
			\freq, freq,
			\min, min,
			\max, max,
		);
	}

	*noise2i { |freq, min, max|
		^NodeProxy.new.source_{ LFNoise2.kr(\freq.kr).range(\min.kr, \max.kr).round }.map(
			\freq, freq,
			\min, min,
			\max, max,
		);
	}

	*value { |value|
		^NodeProxy.new.source_(value);
	}

	*fade { |value, fadeTime|
		^NodeProxy.new.source_(value).fadeTime_(fadeTime);
	}

	*randMidi { |chfreq, degrees = 0, root = 60|
		var sources = [
			{
				\root.kr + Select.kr(0, \degrees.kr([0]));
			},
			{
				var which = LFNoise0.kr(\freq.kr).range(0, 1).round;
				\root.kr + Select.kr(which, \degrees.kr([0, 1]));
			},
			{
				var which = LFNoise0.kr(\freq.kr).range(0, 2).round;
				\root.kr + Select.kr(which, \degrees.kr([0, 1, 2]));
			},
			{
				var which = LFNoise0.kr(\freq.kr).range(0, 3).round;
				\root.kr + Select.kr(which, \degrees.kr([0, 1, 2, 3]));
			},
			{
				var which = LFNoise0.kr(\freq.kr).range(0, 4).round;
				\root.kr + Select.kr(which, \degrees.kr([0, 1, 2, 3, 4]));
			},
			{
				var which = LFNoise0.kr(\freq.kr).range(0, 5).round;
				\root.kr + Select.kr(which, \degrees.kr([0, 1, 2, 3, 4, 5]));
			},
			{
				var which = LFNoise0.kr(\freq.kr).range(0, 6).round;
				\root.kr + Select.kr(which, \degrees.kr([0, 1, 2, 3, 4, 5, 6]));
			},
			{
				var which = LFNoise0.kr(\freq.kr).range(0, 7).round;
				\root.kr + Select.kr(which, \degrees.kr([0, 1, 2, 3, 4, 5, 6, 7]));
			},
		];
		if (degrees.isArray.not, { degrees = [degrees]; });
		^NodeProxy.new.source_(sources[degrees.size-1]).set(
			\freq, chfreq,
			\root, root,
			\degrees, degrees,
		);
		// wrong, but maybe sounds good
		//Select.kr(Select.kr(which, degrees).asInteger, (root + degrees));
	}

	*pulse { |freq, width = 1.0, mul = 1.0, shape = \pulse|
		^NodeProxy.new.source_{
			var width = \width.kr;
			var pulse = LFPulse.kr(\freq.kr, width: width, mul: \mul.kr);
			pulse * width.min(1.0);
		}.map(
			\freq, freq,
			\width, width,
			\mul, if (((shape != \pulse) && NPFactory.respondsTo(shape)), {
				NPFactory.perform(shape, [freq*2, mul]);
			}, { mul }),
		);
	}

	*width { |freq, min = 0, max = 2|
		^NodeProxy.new.source_{ LFNoise0.kr(\freq.kr).range(\min.kr, \max.kr).max(0).round * 0.5 }.map(
			\freq, freq,
			\min, min,
			\max, max,
		);
	}

	*pulseWidth { |chfreq, minwidth = 0, maxwidth = 2, mul = 1.0, shape = \pulse|
		^NPFactory.pulse(chfreq, NPFactory.width(chfreq, minwidth, maxwidth), mul);
	}

	*chVolMul { |tempo = 1, chfreq = 1.0, mul = 1.0|
		^NodeProxy.new.source_{ (1.0 / (\tempo.kr / \chfreq.kr)) * \mul.kr }.map(
			\tempo, tempo,
			\chfreq, chfreq,
			\mul, mul,
		);
	}

	*pan { |freq, mul = 1.0|
		^NodeProxy.new.source_{ LFNoise2.kr(\freq.kr, -1.0, 1.0) * \mul.kr }.map(
			\freq, freq,
			\mul, mul,
		);
	}

	*melody { |chfreq, degrees = 0, harmonic = 1, root = 60|
		^NPFactory.mul(
			NPFactory.randMidi(chfreq, degrees, root),
			if (harmonic.isArray, { NPFactory.noise2i(chfreq, harmonic[0], harmonic[1]) }, { harmonic }),
		)
	}

	*rhythm { |tempo = 1, chfreq = 1.0, minwidth = 0, maxwidth = 2, tempochfreqmul = 1.0, shape = \pulse|
		//var pulse = LFPulse.kr(freq, width: width, mul: LFSaw.kr(freq*2, mul: mul));
		//var pulse = LFPulse.kr(freq, width: width, mul: SinOsc.kr(freq*2, mul: mul));
		^NPFactory.pulseWidth(
			chfreq,
			minwidth,
			maxwidth,
			NPFactory.chVolMul(tempo, chfreq, tempochfreqmul, shape),
		)
	}
}