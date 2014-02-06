package game.objects;

import game.Direction;
import game.DirectionSpriteSet;
import game.Map;
import game.SoundWrapper;

import java.awt.Point;


public abstract class StepperUnit extends Unit {

	private boolean waitingForChanceToMove;
	private int dx;
	private int dy;
	private static final double STUN_DURATION_MULTIPLIER = 1;

	public StepperUnit(int maxHealth, double armor,
			DirectionSpriteSet spriteSet, int idleFrameIndex,
			int movementCooldown, Point spawnLocation,
			SoundWrapper birthSound, SoundWrapper deathSound, EntityAttributeListener... attributeListeners) {
		super(maxHealth, armor, spriteSet, idleFrameIndex, movementCooldown,
				spawnLocation, deathSound, STUN_DURATION_MULTIPLIER, attributeListeners);

	}

	protected boolean waitingForChanceToMove() {
		return waitingForChanceToMove;
	}

	public void orderMovement(Direction direction) {
		if (!isMidMovement()) {
			setDirection(direction);
			dx = direction.dx;
			dy = direction.dy;
			waitingForChanceToMove = true;
		}
	}

	protected boolean tryToMove() {
		waitingForChanceToMove = false;
		boolean movementAllowed = !Map.blockedForHero(x + dx, y + dy);
		if (movementAllowed) {
			setLocation(x + dx, y + dy);
		} else {
			stayAtSameLocation();
		}
		return movementAllowed;
	}

}
