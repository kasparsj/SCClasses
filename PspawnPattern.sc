PspawnPattern {
	var <>func;
	var <>repeats;
	var <counter = 0;

	*new { |func, repeats = inf|
		^super.newCopyArgs(func, repeats);
	}

	asPfunc {
		^Pfunc {
			if (counter >= repeats) {
				nil;
			} {
				counter = counter + 1;
				func.value(counter);
			};
		};
	}
}
