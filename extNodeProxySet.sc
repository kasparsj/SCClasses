+ NodeProxy {
	setIfNil { | ...args |
		args.pairsDo { |key, value|
			if (nodeMap[key] == nil) {
				this.set(key, value);
			};
		}
	}

	pollMap { |trig = 1, label = ""|
		nodeMap.keys.do { |key|
			if (nodeMap[key].respondsTo(\kr)) {
				nodeMap[key].kr.poll(trig, if (label.size > 0, { label ++ ": "}) ++ key.asString);
			};
		}
	}
}