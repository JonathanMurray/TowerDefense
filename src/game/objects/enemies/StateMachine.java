package game.objects.enemies;

import game.Player;
import game.WaveHandler.LaneType;
import game.objects.HeroInfo;
import game.objects.Tower;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class StateMachine {

	private Enemy enemy;
	private State currentState;
	private Map<StateName, State> states = new HashMap<StateName, State>();
	private Tower memorizedTower;

	StateMachine(Enemy enemy, LaneType laneOrigin) {
		this.enemy = enemy;
		states.put(StateName.toCastle, new StateToCastle(this, enemy));
		states.put(StateName.toHero, new StateToHero(this, enemy));
		states.put(StateName.toTower, new StateToTower(this, enemy));
		states.put(StateName.towerDefense, new StateTD(this, enemy));

		if (laneOrigin == LaneType.heroLane) {
			currentState = states.get(StateName.toCastle);
		} else {
			currentState = states.get(StateName.towerDefense);
		}
		currentState.enter();
	}

	void update(int delta) {
		currentState.update(delta);
		if (memorizedTower != null && !memorizedTower.isAlive()) {
			memorizedTower = null;
		}
	}

	void changeState(StateName newState) {
		currentState.exit();
		currentState = states.get(newState);
		currentState.enter();
	}

	void handleEndOfPath() {
		currentState.handleEndOfPath();
	}

	void handlePathIsBlocked() {
		currentState.pathIsBlocked();
	}

	boolean isInTDMode() {
		return currentState == states.get(StateName.towerDefense);
	}

	boolean isHeroWithinAggroRangeAndReachable() {
		if (!HeroInfo.INSTANCE.isHeroAlive()) {
			return false;
		}
		Point heroLocation = HeroInfo.INSTANCE.getHero().getLocation();
		if (!enemy.isLocationWithinDistance(heroLocation, enemy.getAggroRange())) {
			return false;
		}
		return enemy.existsPathToSomeAdjacentLocation(heroLocation);
	}

	boolean isSomeTowerWithinAggroRangeAndReachable() {
		ArrayList<Tower> towersWithinRange = Player.INSTANCE.getTowersWithinRangeOf(enemy.getAggroRange(), enemy.getLocation());
		if (towersWithinRange.isEmpty()) {
			return false;
		}
		for (Tower tower : towersWithinRange) {
			if (enemy.existsPathToSomeAdjacentLocation(tower.getLocation())) {
				memorizedTower = tower;
				return true;
			}
		}
		return false;
	}

	Tower getMemorizedTower() {
		return memorizedTower;
	}

	String DEBUG_STATE_NAME() {
		return currentState.stateName;
	}

}
