ControlledMixer {
	var <size;
	var <master;
	var <channels;
	var <masterControls;
	var <channelControls;
	var <>masterMute;
	var <maMuteControl;
	var <>channelMutes;
	var <chMutesControl;

	*new { |size, server = nil, numChannels = 2|
		var master, channels, channelControls, chMutes, chMutesControl;
		server = (server ? Server.default);
		master = NodeProxy.audio(server, numChannels);
		channels = [];
		channelControls = [];
		chMutes = Array.fill(size, 0);
		chMutesControl = Bus.control(server, size).setn(chMutes);
		^super.newCopyArgs(
			size,
			master,
			channels,
			(),
			channelControls,
			0,
			Bus.control(server, 1).set(0),
			chMutes,
			chMutesControl,
		).init;
	}

	init {
		size.do { |i|
			channels = channels.add(Bus.audio(master.server, master.numChannels));
			channelControls = channelControls.add((
				\vol: Bus.control(master.server).set(1),
			));
			master.add({ channels[i].ar * channelControls[i].vol.kr * (1.0 - chMutesControl.kr(1, i)) });
		};
		// it seems like SC is not freeing buses on CmdPeriod
		CmdPeriod.doOnce {
			master.clear;
			channels.do { |c| c.free; };
			masterControls.do { |c| c.free; };
			channelControls.do { |c| c.values.do { |c1| c1.free; }; };
			maMuteControl.free;
			chMutesControl.free;
		};
	}

	addMasterTarget { |target, slot = nil, controlName, defaultValue = 0|
		if (masterControls[controlName] == nil) {
			masterControls[controlName] = Bus.control(master.server).set(defaultValue);
		};
		if (slot == nil) {
			target.add({ master.ar * masterControls[controlName].kr * (1.0 - maMuteControl.kr) });
		} {
			target.put(slot, { master.ar * masterControls[controlName].kr * (1.0 - maMuteControl.kr) });
		};
	}

	addChannelTarget { |target, slot = nil, name, defaultValue = 0, which = nil|
		var i = 0;
		which = which ? (0..(size-1));
		which.do { |c|
			if (channelControls[c][name] == nil) {
				channelControls[c][name] = Bus.control(master.server).set(defaultValue);
			};
			if (slot == nil) {
				target.add({ channels[c].ar * channelControls[c][name].kr * (1.0 - chMutesControl.kr(1, c)) });
			} {
				target.put(slot + i, { channels[c].ar * channelControls[c][name].kr * (1.0 - chMutesControl.kr(1, c)) });
				i = i + 1;
			};
		};
	}
}
