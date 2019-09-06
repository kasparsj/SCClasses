WeightedPspawnProxy {
	classvar <allMethods;

	var <size;
	var <config;
	var <deltaProxy;
	var <valueProxy;
	var <valueStream;
	var <player;
	var <method;
	var <pspawn;

	*initClass {
		allMethods = [\seq, \par];
	}

	*new { |size = nil|
		var obj = super.newCopyArgs(size, WeightedPspawnProxyConfig.new(size), PatternProxy(), PatternProxy());
		obj.updateWeights;
		^obj;
	}

	updateWeights { |weights = nil|
		weights = weights ? config.weights.copy;
		config.mute.do { |p|
			if (p < weights.size) {
				weights[p] = 0;
			}
		};
		if (config.solo.size > 0) {
			weights.do { |w, p|
				if (config.solo.indexOf(p) == nil) {
					weights[p] = 0;
				} {
					weights[p] = 1;
				}
			}
		};
		weights = weights.normalizeSum;
		valueProxy.source = Pwrand((0..(weights.size-1)), weights, inf);
		valueStream = valueProxy.asStream;
	}

	weighted { |preferred = nil, prefWeight = 1|
		var next = valueStream.next;
		var prefFiltered = (preferred ? []).select({|i| config.weights[i] != 0});
		^(Array.fill((1/prefWeight).round.asInteger, next).addAll(prefFiltered)).choose;
	}

	play { |pattern, clock, quant|
		player = this.getPspawn(pattern).play(clock ? TempoClock.default, quant: quant.asQuant);
		^player;
	}

	stop {
		if (player != nil) {
			player.stop;
			player = nil;
		};
	}

	getPspawn { |pattern|
		if ((pspawn == nil) || (config.method != method)) {
			this.createPspawn(pattern);
		};
		^pspawn;
	}

	createPspawn { |pattern|
		this.stop;
		pspawn = Pspawn(Pbind(
			\method, config.method,
			\pattern, pattern,
			\delta, deltaProxy,
		));
		method = config.method;
		^pspawn;
	}
}

WeightedPspawnProxyConfig {
	var <>weights;
	var <>mute;
	var <>solo;
	var <>method;
	var <>repeat;

	*new { |size|
		^super.newCopyArgs(Array.fill(size, 1), [], [], \par, \inf);
	}
}
