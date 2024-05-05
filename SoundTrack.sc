SoundTrack {
	var <video;
	var <score;
	var <seq;
	var <from, <to, <loop;
	var <isPlaying = false;
	var <isPaused = false;

	*new { |video, score|
		var inst = super.newCopyArgs(video, score);
		inst.initSeq;
		CmdPeriod.add({
			if (inst.isPlaying, {
				inst.stop();
			});
		});
		^inst;
	}

	initSeq { |argFrom, argTo, argLoop|
		argFrom = argFrom ? 0;
		argTo = argTo ? (score.size - 1);
		argLoop = argLoop ? true;
		if (seq != nil) {
			seq.stop;
		};
		if (from != argFrom or: { to != argTo or: { loop != argLoop } }) {
			from = argFrom;
			to = argTo;
			loop = argLoop;
			seq = score.createSeq(from, to, loop);
			seq.onStop = {
				this.prStop;
			};
			seq.onGoto = { |scheduled|
				this.prPlay;
				if (scheduled != true) {
					video.seek(score.beats[seq.currentSection]);
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
		};
		^seq;
	}

	play {
		seq.play;
	}

	prPlay {
		if (isPlaying.not) {
			var start = 0;
			isPlaying = true;
			if (seq.mode == \beats) {
				if (from > 0) {
					start = score.beats[0..(from-1)].sum * (1.0 / RedMst.clock.tempo);
				};
			} {
				start = score.beats[from];
			};
			video.play(start);
		};
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

	prev {
		seq.prev;
	}

	next {
		seq.next;
	}
}
