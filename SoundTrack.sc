SoundTrack {
	var <video;
	var <beats;
	var <mode;
	var <seq;
	var <from, <to, <loop;
	var <isPlaying = false;
	var <isPaused = false;

	*new { |video, beats, mode|
		var inst = super.newCopyArgs(video, beats, mode ? \beats);
		inst.initSeq;
		CmdPeriod.add({
			if (inst.isPlaying, {
				inst.stop();
			});
		});
		^inst;
	}

	initSeq { |from_, to_, loop_|
		from_ = from_ ? 0;
		to_ = to_ ? (beats.size - 1);
		loop_ = loop_ ? true;
		if (seq != nil) {
			seq.stop;
		};
		if (from != from_ or: { to != to_ or: { loop != loop_ } }) {
			var beats1;
			from = from_;
			to = to_;
			loop = loop_;
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
		};
	}

	play { |from, to, loop|
		this.initSeq(from, to, loop);
		this.prPlay(this.from);
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
