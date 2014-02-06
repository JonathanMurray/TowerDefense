package game.objects.enemies;

import game.objects.Tower;

import java.util.Random;


class StateToTower extends State {

	private Tower targetTower;

	private static final int changePositionCooldown = 8000;
	private static Random random = new Random();

	StateToTower(StateMachine stateMachine, Enemy enemy) {
		super("towards tower", stateMachine, enemy);
	}

	void enter() {
		super.enter();
		if (!stateMachine.isSomeTowerWithinAggroRangeAndReachable()) {
			stateMachine.changeState(StateName.toCastle);
		}
		targetTower = stateMachine.getMemorizedTower();
		seekToTower();
	}

	void update(int delta) {
		super.update(delta);

		if (!targetTower.isAlive()) {
			stateMachine.changeState(StateName.toCastle);
		}

		if (stateMachine.isHeroWithinAggroRangeAndReachable()) {
			stateMachine.changeState(StateName.toHero);
			return;
		} else if (!stateMachine.isSomeTowerWithinAggroRangeAndReachable()) {
			stateMachine.changeState(StateName.toCastle);
			return;
		}

		handlePositionChange();
		if (!enemy.isMidMovement()) {
			if (!isTowerWithinAttackRange()) {
				seekToTower();
			}
			enemy.faceTarget(targetTower);
		}
		enemy.tryToAttackTarget(targetTower);
	}

	private void handlePositionChange() {
		if (enemy.getTimeSinceLastMove() > changePositionCooldown) {
			if (random.nextInt(2) == 1) {
				enemy.seek1StepToOtherAdjacentLocation(targetTower
						.getLocation());
			}
		}
	}

	@Override
	void pathIsBlocked() {
		stateMachine.changeState(StateName.toCastle);
	}

	@Override
	void handleEndOfPath() {
		// TODO Auto-generated method stub
		// inget?
	}

	private void seekToTower() {
		enemy.seekToRandomAdjacentLocation(targetTower.getLocation());
	}

	private boolean isTowerWithinAttackRange() {
		return enemy.isLocationWithinDistance(targetTower.getLocation(),
				enemy.getAttackRange());
	}

}
