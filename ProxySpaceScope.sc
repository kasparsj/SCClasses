ProxySpaceScope {
	var <proxySpace;
	var <scopes;
	var <window;
	var <routine;

	*new { |proxySpace|
		^super.newCopyArgs(proxySpace).init;
	}

	init {
		scopes = ();
		this.createWindow;
		this.onResize;
		this.forkUpdate;
	}

	createWindow {
		window = Window.new("ProxySpaceScope", Rect(20, 20, 400, 500), scroll: true);
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
					var view = View(window.view, 200@200);
					view.layout = VLayout(
						StaticText(view, 200@20).string_(proxy.asCompileString),
					);
					scopes[proxy.bus.index] = Stethoscope(proxySpace.server, proxy.numChannels, proxy.bus.index, rate: proxy.rate, view: view);
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
		}
	}
}
