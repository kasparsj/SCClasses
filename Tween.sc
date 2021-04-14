Tween {
	classvar <routines;

	*initClass {
		routines = ();
	}

	*fromTo { |obj, param, startValue, endValue, duration, options = nil|
		var r = routines[obj.identityHash];
		options = options ? ();
		if (r != nil) {
			if (r[param] != nil) {
				r[param].stop;
				r.removeAt(param);
			};
		} {
			routines[obj.identityHash] = ();
			r = routines[obj.identityHash];
		};
		r[param] = {
			var step = (options[\step] ? 0.1);
			var env = Env([startValue, endValue], [duration], options[\curve] ? \lin);
			var time = 0;
			while ({time <= duration}, {
				var value = env.at(time);
				this.prSet(obj, param, value);
				if (options[\onUpdate] != nil) {
					options[\onUpdate].value(value);
				};
				step.wait;
				time = time + step;
			});
			if (options[\onComplete] != nil) {
				options[\onComplete].value;
			};
			r.removeAt(param);
		}.fork;
		^r[param];
	}

	*to { |obj, param, endValue, duration, options = nil|
		^this.fromTo(obj, param, this.prGet(obj, param), endValue, duration, options);
	}

	*from { |obj, param, startValue, duration, options = nil|
		^this.fromTo(obj, param, startValue, this.prGet(obj, param), duration, options);
	}

	*prGet { |obj, param|
		^if (obj.respondsTo(param)) {
			obj.perform(param);
		} {
			obj[param];
		};
	}

	*prSet { |obj, param, value|
		var setter = (param.asString ++ "_").asSymbol;
		if (obj.respondsTo(setter)) {
			obj.perform(setter, value);
		} {
			obj[param] = value;
		};
	}
}
