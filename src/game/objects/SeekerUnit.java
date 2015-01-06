package game.objects;

import game.Direction;
import game.DirectionSpriteSet;
import game.Map;
import game.PhysicsHandler;
import game.SoundWrapper;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;


public abstract class SeekerUnit extends Unit {

	private static final int MAX_SEARCH = 100;
	private AStarPathFinder pathFinder; // doesn't work when static for some
										// reason

	private Path path;
	private int currentPathStep;

	public SeekerUnit(int maxHealth, double armor, DirectionSpriteSet spriteSet, int idleFrameIndex, int movementCooldown, 
			Point spawnLocation, SoundWrapper deathSound, double stunDurationMultiplier) {
		super(maxHealth, armor, spriteSet, idleFrameIndex, movementCooldown, spawnLocation, deathSound, stunDurationMultiplier);

		pathFinder = new AStarPathFinder(Map.instance(), MAX_SEARCH, false);
	}

	@Override
	protected boolean waitingForChanceToMove() {
		return isSeeking();
	}

	public boolean isSeeking() {
		return path != null;
	}

	public void stopMoving() {
		path = null;
	}

	@Override
	protected boolean tryToMove() {
		if (currentStepNotBlocked()) {
			takeOneStepOnPath();
			return true;
		} else {
			stayAtSameLocation();
			handlePathIsBlocked();
			return false;
		}
	}

	private boolean currentStepNotBlocked() {
		int stepX = path.getX(currentPathStep);
		int stepY = path.getY(currentPathStep);
		return !Map.instance().blocked(pathFinder, stepX, stepY);
	}

	private void takeOneStepOnPath() {
		setLocation(path.getX(currentPathStep), path.getY(currentPathStep));
		currentPathStep++;
		if (currentPathStep >= path.getLength()) {
			handleEndOfPath();
		}
	}

	public boolean existsPathToSomeAdjacentLocation(Point centerLocation) {
		Point[] adjacentLocations = PhysicsHandler
				.getAdjacentLocations(centerLocation);
		for (Point adjLocation : adjacentLocations) {
			if (getLocation().equals(adjLocation)) {
				return true;
			}
			if (pathFinder.findPath(this, x, y, adjLocation.x, adjLocation.y) != null) {
				return true;
			}
		}
		return false;
	}

	public void seekToRandomAdjacentLocation(Point centerLocation) {
		Point[] adjacentLocations = PhysicsHandler
				.getAdjacentLocations(centerLocation);
		List<Point> asList = Arrays.asList(adjacentLocations);
		Collections.shuffle(asList);
		adjacentLocations = asList.toArray(adjacentLocations);

		for (Point adjLocation : adjacentLocations) {
			seekToLocation(adjLocation);
			if (isSeeking()) {
				return;
			}
		}
	}

	public void seek1StepToOtherAdjacentLocation(Point centerLocation) {
		if (!PhysicsHandler.arePointsAdjacent(centerLocation, getLocation())) {
			new IllegalArgumentException(
					"currently not adjacent to centerLocation!").printStackTrace(); 
			return; //TODO 
			//BUG  Why does this happen sometimes?
		}
		Point[] adjacentLocations = PhysicsHandler
				.getAdjacentLocations(centerLocation);
		for (Point adjLocation : adjacentLocations) {
			if (PhysicsHandler
					.arePointsWithinDistance(getLocation(), adjLocation, 1.1)) {
				seekToLocation(adjLocation);
				if (isSeeking()) {
					return;
				}
			}
		}
	}

	public boolean existsPathToLocation(Point location) {
		return pathFinder.findPath(this, getLocation().x, getLocation().y,
				location.x, location.y) != null;
	}

	public void seekToLocation(Point location) {
		path = pathFinder.findPath(this, getLocation().x, getLocation().y,
				location.x, location.y);
		currentPathStep = 1;
	}

	public void seekAwayFromLocation(Point location) {
		Direction direction = Direction.getMostFittingDirection(getLocation().x
				- location.x, getLocation().y - location.y);
		Point oneStepAway = PhysicsHandler.getRelativeLocation(getLocation(),
				direction.getVector());
		seekToLocation(oneStepAway);
		if (path == null) {
			seekToRandomAdjacentLocation(oneStepAway);
		}
	}

	protected abstract void handlePathIsBlocked();

	protected abstract void handleEndOfPath();

	protected Point getPathDestination() {
		return new Point(path.getX(path.getLength() - 1), path.getY(path
				.getLength() - 1));
	}

}
