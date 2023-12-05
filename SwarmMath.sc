SwarmMath {
	var <>freqs, <>partials, <>variations, <>math;

	*freqPartial { |e, mul=1, pow=2, offset=0, add=1|
		^(e.freq * (add+((e.partial+offset).abs*mul)**pow));
	}

	*ampPartial { |e, value, offset=0|
		e = e.copy;
		e.partial = (e.partial+offset).abs;
		e.partial1 = e.partial+1;
		e.p = e.partial;
		e.p1 = e.p+1;
		^value.value(e);
	}

	*ampPartialRec { |e, offset=0, pow=2|
		^(1.0 / ((1 + (e.p+offset).abs)) ** pow);
	}

	*ampPartialRecMod { |e, mod=2, offset=0, pow=2|
		^(1.0 / (1 + ((e.p+offset).abs % mod) ** pow));
	}

	*new { |freqs, partials=0, variations=1, math|
		^super.newCopyArgs(freqs, partials, variations, Dictionary.newFrom(math.asPairs));
	}

	size {
		^(freqs.size * partials * variations);
	}

	calc { |i ... params|
		var event = (), result = [];
		event.freq = freqs[(i / (partials * variations)).floor];
		event.partial = (i / variations).floor % partials;
		event.partial1 = event.partial + 1;
		event.variation = i % variations;
		event.f = event.freq;
		event.p = event.partial;
		event.p1 = event.p + 1;
		event.v = event.variation;
		event.size = this.size;
		params.do { |param|
			result = result.addAll([param, math[param].(event)]);
		};
		^result;
	}
}