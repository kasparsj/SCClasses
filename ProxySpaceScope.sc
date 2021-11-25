ProxySpaceScope {
	var <proxySpace;
	var <scopes;
	var <names;
	var <window;
	var <routine;

	*new { |proxySpace|
		^super.newCopyArgs(proxySpace).init;
	}

	init {
		scopes = ();
		names = ();
		this.createWindow;
		this.onResize;
		this.forkUpdate;
	}

	createWindow {
		window = Window.new("ProxySpaceScope", Rect(20, 20, 400, 500));
		window.view.decorator = FlowLayout(window.view.bounds, 10@5, 20@5);
		window.view.onResize = { |view|
			this.onResize;
		};
		window.onClose = {
			this.onClose;
		};
		window.front;
	}

	onResize {
		window.view.decorator.bounds = window.view.bounds;
		window.view.decorator.reFlow(window.view);
	}

	onClose {
		routine.stop;
		this.removeScopes(scopes.keys);
	}

	forkUpdate {
		var self = this;
		routine = Routine({
			inf.do {
				self.update;
				0.5.yield;
				0.5.wait;
			};
		});
		AppClock.play(routine);
	}

	update {
		this.updateScopes;
		this.renderScopes;
	}

	renderScopes {
		var cols = 4, rows = 4;
		/*cols.do { |col|
			rows.do { |row|

			}
		}*/
	}

	updateScopes {
		var stale = scopes.keys;
		proxySpace.envir.do { |proxy|
			if (proxy.rate == \audio, {
				if (scopes[proxy.bus.index] == nil, {
					scopes[proxy.bus.index] = Stethoscope.new(proxySpace.server, proxy.numChannels, proxy.bus.index, rate: proxy.rate, view:window.view);
					names[proxy.bus.index] = proxy.asCompileString;
				}, {
					stale.remove(proxy.bus.index);
				});
			});
		};
		this.removeScopes(stale);
	}

	removeScopes { |indexes|
		indexes.do { |index|
			scopes[index].free;
			scopes.removeAt(index);
			names.removeAt(index);
		}
	}
}
