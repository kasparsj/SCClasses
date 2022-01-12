VideoOSC {
	var <net;
	var <path;
	var <>start;

	*new { |net, path, start|
		var inst = super.newCopyArgs(net).start_(start ? 0);
		if (path != nil, {
			inst.load(path);
		});
		^inst;
	}

	load { |value|
		path = value;
		net.sendMsg("/load", path);
	}

	play { |from, to|
		from = from ? 0;
		to = to ? (start * -1);
		net.sendMsg("/play", start + from, start + to);
	}

	loop { |from, to|
		from = from ? 0;
		to = to ? (start * -1);
		net.sendMsg("/loop", start + from, start + to);
	}

	stop {
		net.sendMsg("/stop");
	}

	pause {
		net.sendMsg("/pause");
	}

	resume {
		net.sendMsg("/resume");
	}

	addMarkers { |times, labels|
		times.do { |time, i|
			this.addMarker(time, labels[i]);
		};
	}

	addMarker { |time, label|
		net.sendMsg("/marker", start + time, label);
	}

	select { |from, to|
		from = from ? 0;
		to = to ? (start * -1);
		net.sendMsg("/select", start + from, start + to);
	}

	seek { |pos|
		net.sendMsg("/seek", start + pos);
	}
}
