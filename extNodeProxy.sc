+ NodeProxy {

	// set after quant
	qset { |...args|
		if (quant.isNil) { this.set(*args); }
		{
			clock = clock ? TempoClock.default;
			clock.schedAbs(quant.nextTimeOnGrid(clock),  {
				this.set(*args);
			});
		};
	}

	pollMap { |trig = 1, label = ""|
		nodeMap.keys.do { |key|
			if (nodeMap[key].respondsTo(\kr)) {
				nodeMap[key].kr.poll(trig, if (label.size > 0, { label ++ ": "}) ++ key.asString);
			} {
				nodeMap[key].poll(trig, if (label.size > 0, { label ++ ": "}) ++ key.asString);
			};
		}
	}
}