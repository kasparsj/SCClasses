ProxyFactory {
	classvar <>server;

	*initClass {
		server = Server.default;
	}

	*ar { |numChannels = 1|ProxySpace
		^this.prInit(NodeProxy.audio(server, numChannels));
	}

	*kr { |numChannels = 1|
		^this.prInit(NodeProxy.control(server, numChannels));
	}

	*prInit { |proxy|
		if (currentEnvironment.respondsTo(\initProxy)) {
			currentEnvironment.initProxy(proxy);
		}
		^proxy;
	}

	*mul { |source, mul|
		^this.kr.source_{ \source.kr * \mul.kr }.map(
			\source, source,
			\mul, mul,
		);
	}

	*min { |source, min|
		^this.kr.source_({ \source.kr.min(min) }).map(
			\source, source,
			\min, min,
		);
	}

	*max { |source, max|
		^this.kr.source_({ \source.kr.max(max) }).map(
			\source, source,
			\max, max,
		);
	}

	*sin { |freq, mul|
		^this.kr.source_{  SinOsc.kr(\freq.kr, mul: \mul.kr) }.map(
			\freq, freq,
			\mul, mul,
		);
	}

	*saw { |freq, mul|
		^this.kr.source_{  LFSaw.kr(\freq.kr, mul: \mul.kr) }.map(
			\freq, freq,
			\mul, mul,
		);
	}

	*noise0i { |freq, min, max|
		^this.kr.source_{ LFNoise0.kr(\freq.kr).range(\min.kr, \max.kr).round }.set(
			\freq, freq,
			\min, min,
			\max, max,
		);
	}

	*noise1i { |freq, min, max|
		^this.kr.source_{ LFNoise1.kr(\freq.kr).range(\min.kr, \max.kr).round }.set(
			\freq, freq,
			\min, min,
			\max, max,
		);
	}

	*noise2i { |freq, min, max|
		^this.kr.source_{ LFNoise2.kr(\freq.kr).clip2.range(\min.kr, \max.kr).round }.set(
			\freq, freq,
			\min, min,
			\max, max,
		);
	}

	*value { |value|
		^this.kr.source_(value);
	}

	*fade { |value, fadeTime|
		^this.kr.source_(value).fadeTime_(fadeTime);
	}

	*midi { |chfreq, degrees = 0, root = 60, harmonic = 1, cps = false|
		var source = { |size, cps = false|
			var code = [
				"var which = LFNoise0.kr(\\freq.kr).range(0, " ++ (size-1).asString ++ ").round;",
				"(\\root.kr + Select.kr(which, \\degrees.kr(" ++ (0..(size-1)).asString ++")))" ++ if(cps, { ".midicps" }, { "" }) ++ " * \\harmonic.kr(1);",
			];
			code.join("\n").compile;
		};
		if (degrees.isArray.not, { degrees = [degrees]; });
		^this.kr.source_(source.(degrees.size, cps)).set(
			\freq, chfreq,
			\root, root,
			\degrees, degrees,
			\harmonic, harmonic,
		);
		// wrong, but maybe sounds good
		//Select.kr(Select.kr(which, degrees).asInteger, (root + degrees));
	}

	*pulse { |freq = 1, width = 1.0, mul = 1.0, shape = \pulse|
		^this.kr.source_{
			LFPulse.kr(\freq.kr, width: \width.kr, mul: \mul.kr) * \width.kr.min(1.0)
		}.map(
			\freq, freq,
			\width, width,
			\mul, if ([\sin, \saw].includes(shape), {
				this.perform(shape, [freq*2, mul]);
			}, { mul }),
		);
	}

	*width { |freq = 1, min = 0, max = 2|
		^this.kr.source_{ LFNoise0.kr(\freq.kr).range(\min.kr, \max.kr).max(0).round * 0.5 }.map(
			\freq, freq,
			\min, min,
			\max, max,
		);
	}

	*pulseWidth { |chfreq = 1, minwidth = 0, maxwidth = 2, mul = 1.0, shape = \pulse|
		^this.pulse(chfreq, this.width(chfreq, minwidth, maxwidth), mul, shape);
	}

	*chVolMul { |tempo = 1, chfreq = 1.0, mul = 1.0|
		^this.kr.source_{ (1.0 / (\tempo.kr / \chfreq.kr)) * \mul.kr }.map(
			\tempo, tempo,
			\chfreq, chfreq,
			\mul, mul,
		);
	}

	*pan { |freq = 1, mul = 1.0|
		^this.kr.source_{ LFNoise2.kr(\freq.kr, -1.0, 1.0).clip2 * \mul.kr }.map(
			\freq, freq,
			\mul, mul,
		);
	}

	*freq { |midinote, harmonic|
		^this.kr.source_{ \midinote.kr.midicps * \harmonic.kr }.map(
			\midinote, midinote,
			\harmonic, harmonic,
		);
	}

	*rhythm { |tempo = 1, chfreq = 1.0, minwidth = 0, maxwidth = 2, chvolmul = 1.0, shape = \pulse|
		^this.pulseWidth(
			chfreq,
			minwidth,
			maxwidth,
			this.chVolMul(tempo, chfreq, chvolmul),
			shape,
		);
	}
}