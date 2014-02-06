package game.objects.enemies;

abstract class State {

	static final boolean STATE_DEBUG = false;

	String stateName;
	protected StateMachine stateMachine;
	protected Enemy enemy;

	protected State(String stateName, StateMachine stateMachine, Enemy enemy) {
		this.stateName = stateName;
		this.stateMachine = stateMachine;
		this.enemy = enemy;
	}

	void enter() {
		debugPrint("enemy " + enemy + " [enter] " + stateName);
	}

	void update(int delta) {
		debugPrint("enemy " + enemy + " [update] " + stateName);
	}

	abstract void handleEndOfPath();

	abstract void pathIsBlocked();

	void exit() {
		debugPrint("enemy " + enemy + "[exit] " + stateName);
	}

	private static void debugPrint(String string) {
		if (STATE_DEBUG) {
			System.out.println(string);
		}
	}

}
