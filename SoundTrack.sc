SoundTrack {
	var <video;
	var <beats;
	var <mode;
	var <seq;
	var <isPlaying = false;
	var <isPaused = false;

	*new { |video, beats, mode|
		var inst = super.newCopyArgs(video, beats, mode ? \beats);
		CmdPeriod.add({
			if (inst.isPlaying, {
				inst.stop();
			});
		});
		^inst;
	}

	play { |from, to, loop|
		var beats1;
		from = from ? 0;
		to = to ? (beats.size - 1);
		loop = loop ? true;
		if (seq != nil) {
			seq.stop;
		};
		if (mode == \beats) {
			beats1 = beats[from..to];
		} {
			beats1 = beats[1..][from..to];
			if (from > 0) {
				beats1 = beats1 - beats[1..][from-1];
			};
		};
		seq = RedSeq((from..to), beats1, mode);
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
				video.seek(beats[seq.currentSection]);
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
		var start = 0;
		isPlaying = true;
		seq.play;
		if (mode == \beats) {
			if (from > 0) {
				start = beats[0..(from-1)].sum * (1.0 / RedMst.clock.tempo);
			};
		} {
			start = beats[from];
		};
		video.play(start);
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
