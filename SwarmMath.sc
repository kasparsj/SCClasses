SwarmMath {
	var <>freqs, <>partials, <>variations, <>math;

	*freqPartial { |e, mul=1|
		^(e.freq * (1+e.partial*mul));
	}

	*freqPartialSq { |e, mul=1|
		^(e.freq * ((1+e.partial*mul)**2));
	}

	*ampPartialRec { |e, mul=1|
		^(1 / (e.partial+1) * mul);
	}

	*new { |freqs, partials=0, variations=0, math|
		^super.newCopyArgs(freqs, partials, variations, Dictionary.newFrom(math.asPairs));
	}

	size {
		^(freqs.size * partials * variations);
	}

	calc { |i ... params|
		var event = (), result = [];
		event.freq = freqs[(i / (partials * variations)).floor];
		event.partial = (i / variations).floor % partials;
		event.variation = i % variations;
		event.f = event.freq;
		event.p = event.partial;
		event.v = event.variation;
		params.do { |param|
			result = result.addAll([param, math[param].(event)]);
		};
		^result;
	}
}