ProxyFactory {
	classvar <>server;

	*initClass {
		server = Server.default;
	}

	*ar {
		// todo: implement
	}

	*kr { |numChannels = 1|
		^if (currentEnvironment.respondsTo(\makeProxy), {
			var proxy = currentEnvironment.makeProxy;
			proxy.defineBus(\control, numChannels);
			proxy;
		}, {
			NodeProxy.control(server, numChannels);
		});
	}

	*mul { |source, mul|
		^ProxyFactory.kr.source_{ \source.kr * \mul.kr }.map(
			\source, source,
			\mul, mul,
		);
	}

	*min { |source, min|
		^ProxyFactory.kr.source_({ \source.kr.min(min) }).map(
			\source, source,
			\min, min,
		);
	}

	*max { |source, max|
		^ProxyFactory.kr.source_({ \source.kr.max(max) }).map(
			\source, source,
			\max, max,
		);
	}

	*sin { |freq, mul|
		^ProxyFactory.kr.source_{  SinOsc.kr(\freq.kr, mul: \mul.kr) }.map(
			\freq, freq,
			\mul, mul,
		);
	}

	*saw { |freq, mul|
		^ProxyFactory.kr.source_{  LFSaw.kr(\freq.kr, mul: \mul.kr) }.map(
			\freq, freq,
			\mul, mul,
		);
	}

	*noise0i { |freq, min, max|
		^ProxyFactory.kr.source_{ LFNoise0.kr(\freq.kr).range(\min.kr, \max.kr).round }.set(
			\freq, freq,
			\min, min,
			\max, max,
		);
	}

	*noise1i { |freq, min, max|
		^ProxyFactory.kr.source_{ LFNoise1.kr(\freq.kr).range(\min.kr, \max.kr).round }.set(
			\freq, freq,
			\min, min,
			\max, max,
		);
	}

	*noise2i { |freq, min, max|
		^ProxyFactory.kr.source_{ LFNoise2.kr(\freq.kr).range(\min.kr, \max.kr).round }.set(
			\freq, freq,
			\min, min,
			\max, max,
		);
	}

	*value { |value|
		^ProxyFactory.kr.source_(value);
	}

	*fade { |value, fadeTime|
		^ProxyFactory.kr.source_(value).fadeTime_(fadeTime);
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
		^ProxyFactory.kr.source_(sources[degrees.size-1]).set(
			\freq, chfreq,
			\root, root,
			\degrees, degrees,
		);
		// wrong, but maybe sounds good
		//Select.kr(Select.kr(which, degrees).asInteger, (root + degrees));
	}

	*pulse { |freq = 1, width = 1.0, mul = 1.0, shape = \pulse|
		^ProxyFactory.kr.source_{
			LFPulse.kr(\freq.kr, width: \width.kr, mul: \mul.kr) * \width.kr.min(1.0)
		}.map(
			\freq, freq,
			\width, width,
			\mul, if ([\sin, \saw].includes(shape), {
				ProxyFactory.perform(shape, [freq*2, mul]);
			}, { mul }),
		);
	}

	*width { |freq = 1, min = 0, max = 2|
		^ProxyFactory.kr.source_{ LFNoise0.kr(\freq.kr).range(\min.kr, \max.kr).max(0).round * 0.5 }.map(
			\freq, freq,
			\min, min,
			\max, max,
		);
	}

	*pulseWidth { |chfreq = 1, minwidth = 0, maxwidth = 2, mul = 1.0, shape = \pulse|
		^ProxyFactory.pulse(chfreq, ProxyFactory.width(chfreq, minwidth, maxwidth), mul, shape);
	}

	*chVolMul { |tempo = 1, chfreq = 1.0, mul = 1.0|
		^ProxyFactory.kr.source_{ (1.0 / (\tempo.kr / \chfreq.kr)) * \mul.kr }.map(
			\tempo, tempo,
			\chfreq, chfreq,
			\mul, mul,
		);
	}

	*pan { |freq = 1, mul = 1.0|
		^ProxyFactory.kr.source_{ LFNoise2.kr(\freq.kr, -1.0, 1.0) * \mul.kr }.map(
			\freq, freq,
			\mul, mul,
		);
	}

	*freq { |midinote, harmonic|
		^ProxyFactory.kr.source_{ \midinote.kr.midicps * \harmonic.kr }.map(
			\midinote, midinote,
			\harmonic, harmonic,
		);
	}

	*melody { |chfreq, degrees = 0, harmonic = 1, root = 60|
		^ProxyFactory.freq(
			ProxyFactory.randMidi(chfreq, degrees, root),
			harmonic,
		);
	}

	*rhythm { |tempo = 1, chfreq = 1.0, minwidth = 0, maxwidth = 2, chvolmul = 1.0, shape = \pulse|
		^ProxyFactory.pulseWidth(
			chfreq,
			minwidth,
			maxwidth,
			ProxyFactory.chVolMul(tempo, chfreq, chvolmul),
			shape,
		);
	}
}