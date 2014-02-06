package game.objects.enemies;

import game.Physics;
import game.objects.HeroInfo;

import java.awt.Point;
import java.util.Random;


class StateToHero extends State {

	private static final int changePositionCooldown = 8000;
	private static Random random = new Random();

	StateToHero(StateMachine stateMachine, Enemy enemy) {
		super("towards hero", stateMachine, enemy);
	}

	void enter() {
		super.enter();
		seekToHero();
	}

	@Override
	void update(int delta) {
		super.update(delta);

		if (!HeroInfo.INSTANCE.isHeroAlive()) {
			stateMachine.changeState(StateName.toCastle);
			return;
		}

		if (!stateMachine.isHeroWithinAggroRangeAndReachable()) {
			stateMachine.changeState(StateName.toCastle);
			return;
		}
		if (!enemy.isRangedAttacker()) {
			handlePositionChange();
		}

		if (!enemy.isMidMovement()) {
			if (!isHeroWithinAttackRange()) {
				seekToHero();
			}

			if (enemy.isRangedAttacker()) {
				rangedKeepCorrectDistance();
			}

			enemy.faceTarget(HeroInfo.INSTANCE.getHero());
		}
		enemy.tryToAttackTarget(HeroInfo.INSTANCE.getHero());
	}

	private void rangedKeepCorrectDistance() {
		double margin = 1.5;
		if (enemy.isLocationWithinDistance(HeroInfo.INSTANCE.getHero().getLocation(),
				enemy.getAttackRange() - margin)) {
			enemy.seekAwayFromLocation(HeroInfo.INSTANCE.getHero().getLocation());
		} else if (isHeroWithinAttackRange()) {
			enemy.stopMoving();
		}
	}

	private void handlePositionChange() {
		Point heroLocation = HeroInfo.INSTANCE.getHero().getLocation();
		if (enemy.getTimeSinceLastMove() > changePositionCooldown
				&& Physics.arePointsAdjacent(heroLocation, enemy.getLocation())) {
			if (random.nextInt(2) == 1) {
				enemy.seek1StepToOtherAdjacentLocation(heroLocation);
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

	private void seekToHero() {
		enemy.seekToRandomAdjacentLocation(HeroInfo.INSTANCE.getHero().getLocation());
	}

	private boolean isHeroWithinAttackRange() {
		if (!HeroInfo.INSTANCE.isHeroAlive()) {
			return false;
		}
		return enemy.isLocationWithinDistance(HeroInfo.INSTANCE.getHero().getLocation(),
				enemy.getAttackRange());
	}

}
