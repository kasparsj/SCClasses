SoundTrack {
	var <video;
	var <times;
	var <seq;
	var <isPlaying = false;
	var <isPaused = false;

	*new { |video, times|
		var inst = super.newCopyArgs(video, times);
		CmdPeriod.add({
			if (inst.isPlaying, {
				inst.stop();
			});
		});
		^inst;
	}

	play { |from, to, loop|
		var times1;
		from = from ? 0;
		to = to ? (times.size - 1);
		loop = loop ? true;
		if (seq != nil) {
			seq.stop;
		};
		times1 = times[1..][from..to];
		if (from > 0) {
			times1 = times1 - times[1..][from-1];
		};
		seq = RedSeq((from..to), times1, \time);
		seq.onStop = {
			this.prStop();
		};
		seq.onLoop = {
			if (loop, {
				this.prPlay(from);
			}, {
				this.prStop();
			});
		};
		seq.onGoto = { |scheduled|
			if (scheduled != true) {
				video.seek(times[seq.currentSection]);
			};
		};
		seq.onPause = {
			isPaused = true;
			video.pause();
		};
		seq.onResume = {
			isPaused = false;
			video.resume();
		};
		this.prPlay(from);
	}

	prPlay { |from|
		isPlaying = true;
		seq.play;
		video.play(times[from]);
	}

	prStop {
		isPlaying = false;
		video.stop;
	}

	stop {
		seq.stop;
	}

	togglePause {
		if (isPaused, {
			this.resume();
		}, {
			this.pause();
		});
	}

	pause {
		seq.pause;
	}

	resume {
		seq.resume;
	}

	prevSection {
		seq.prev;
	}

	nextSection {
		seq.next;
	}
}
