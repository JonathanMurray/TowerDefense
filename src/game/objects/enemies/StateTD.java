package game.objects.enemies;

import game.Map;

import java.awt.Point;

class StateTD extends State {

	private Point[] castleWaypoints;
	private int currentWaypointIndex = 0;

	protected StateTD(StateMachine stateMachine, Enemy enemy) {
		super("Tower lane", stateMachine, enemy);
		castleWaypoints = Map.instance().getRandomTDCheckpoints();
	}

	@Override
	void enter() {
		seekToCastleWaypoint(currentWaypointIndex);
	}

	@Override
	void handleEndOfPath() {
		if (hasNextCastleWaypoint()) {
			currentWaypointIndex++;
			seekToCastleWaypoint(currentWaypointIndex);
		} else {
			stateMachine.changeState(StateName.toCastle);
		}
	}

	@Override
	void pathIsBlocked() {
		throw new IllegalStateException("Never blocked for this state?");
	}

	private boolean hasNextCastleWaypoint() {
		return currentWaypointIndex + 1 < castleWaypoints.length;
	}

	private void seekToCastleWaypoint(int index) {
		enemy.seekToLocation(castleWaypoints[index]);
	}

}
