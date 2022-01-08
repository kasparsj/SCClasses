FindTempo {
	var <minTempo;
	var <maxTempo;
	var <tempoStep;

	*new { |minTempo, maxTempo, tempoStep|
		^super.newCopyArgs(minTempo ? 60, maxTempo ? 160, tempoStep ? 0.05);
	}

	rms { |markers|
		var tempoErrors = [];
		this.tempos().do { |tempo|
			var errors = this.errors(tempo, markers);
			var error = this.rmsError(errors);
			tempoErrors = tempoErrors.add(Dictionary[\tempo -> tempo, \error -> error]);
		};
		tempoErrors.sortBy(\error);
		^tempoErrors;
	}

	thresh { |markers, value|
		var tempoErrors = [];
		this.tempos().do { |tempo|
			var errors = this.errors(tempo, markers);
			var error = this.threshError(errors, value);
			tempoErrors = tempoErrors.add(Dictionary[\tempo -> tempo, \error -> error]);
		};
		tempoErrors.sortBy(\error);
		^tempoErrors;
	}

	rmsError { |errors|
		var totError = 0;
		errors.do { |error|
			totError = totError + (error * error);
		};
		^(totError / errors.size).sqrt;
	}

	threshError { |errors, value|
		var count = 0;
		errors.do { |error|
			if (error > value) {
				count = count + 1;
			};
		};
		^count;
	}

	errors { |bpmTempo, markers|
		var errors = [];
		if (markers.size > 1) {
			(1..(markers.size-1)).do { |i|
				var eventSec = markers[i] - markers[0];
				var error = this.beatError(eventSec, bpmTempo);
				errors = errors.add(error);
			};
		};
		^errors;
	}

	tempos {
		var numTempos = (maxTempo - minTempo) / tempoStep;
		var tempos = [];
		numTempos.do { |i|
			tempos = tempos.add(minTempo + (i*tempoStep););
		};
		^tempos;
	}

	beatError { |eventSec, bpmTempo|
		var beatInterval = 60 / bpmTempo;
		var beatNumber = (eventSec / beatInterval).round;
		^(beatNumber * beatInterval - eventSec).abs;
	}

}