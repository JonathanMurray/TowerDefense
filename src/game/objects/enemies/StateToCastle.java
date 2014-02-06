package game.objects.enemies;

import game.Map;

import java.awt.Point;

import applicationSpecific.MapEditorData;

class StateToCastle extends State {

	private static Point castleLocation = Map.instance().getCastleLocation();

	StateToCastle(StateMachine stateMachine, Enemy enemy) {
		super("towards castle", stateMachine, enemy);
	}

	void enter() {
		super.enter();
		seekCastle();
	}

	void update(int delta) {
		super.update(delta);
		if (stateMachine.isHeroWithinAggroRangeAndReachable()) {
			stateMachine.changeState(StateName.toHero);
		} else if (stateMachine.isSomeTowerWithinAggroRangeAndReachable()) {
			stateMachine.changeState(StateName.toTower);
		}

		if (!enemy.isSeeking()) {
			if (enemy.existsPathToLocation(castleLocation)) {
				seekCastle();
			} else {
				seekSomeCheckpoint();
			}
		}
	}

	private void seekSomeCheckpoint() {
		for (int i = 0; i < MapEditorData.HERO_ENEMIES_CHECKPOINTS.length; i++) {
			if (enemy.isSeeking()) {
				break;
			}
			enemy.seekToLocation(MapEditorData.HERO_ENEMIES_CHECKPOINTS[i]);
		}
	}

	void pathIsBlocked() {
		seekCastle();
	}

	@Override
	void handleEndOfPath() {
		enemy.enterCastle();
	}

	private void seekCastle() {
		enemy.seekToLocation(castleLocation);
	}

}
