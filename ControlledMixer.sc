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
		var master, channels, chMutes;
		server = (server ? Server.default);
		master = NodeProxy.audio(server, numChannels);
		channels = [];
		size.do { |i|
			channels = channels.add(Bus.audio(server, numChannels));
			master.add(channels[i]);
		};
		chMutes = Array.fill(size, 0);
		^super.newCopyArgs(
			size,
			master,
			channels,
			(),
			Array.fill(size, { () }),
			0,
			Bus.control(server, 1).set(0),
			chMutes,
			Bus.control(server, size).setn(chMutes),
		);
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
