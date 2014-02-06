package game;

import java.awt.Point;

public enum Direction {

	UP(0, -1), LEFT(-1, 0), DOWN(0, 1), RIGHT(1, 0);

	public int dx;
	public int dy;

	Direction(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public static Direction getDirection(int dx, int dy) {
		if (dx > 0) {
			return RIGHT;
		} else if (dx < 0) {
			return LEFT;
		} else {
			if (dy > 0) {
				return DOWN;
			} else if (dy < 0) {
				return UP;
			}
		}
		throw new IllegalArgumentException("Invalid arguments: dx=" + dx
				+ ", dy=" + dy);
	}

	public static Direction getMostFittingDirection(int dx, int dy) {
		if (dx > 0) {
			if (dx > Math.abs(dy)) {
				return RIGHT;
			} else {
				return dy > 0 ? DOWN : UP;
			}
		} else {
			if (-dx > Math.abs(dy)) {
				return LEFT;
			} else {
				return dy > 0 ? DOWN : UP;
			}
		}
	}

	public Direction getOppositeDirection() {
		switch (this) {
		case UP:
			return DOWN;
		case DOWN:
			return UP;
		case RIGHT:
			return LEFT;
		case LEFT:
			return RIGHT;
		default:
			throw new IllegalStateException();
		}
	}

	public Point getVector(int stepsForward, int stepsRight) {
		switch (this) {
		case UP:
			return new Point(stepsRight, -stepsForward);
		case LEFT:
			return new Point(-stepsForward, -stepsRight);
		case DOWN:
			return new Point(-stepsRight, stepsForward);
		case RIGHT:
			return new Point(stepsForward, stepsRight);
		default:
			throw new IllegalStateException();
		}
	}

	public Point getVector() {
		return getVector(1, 0);
	}

	/**
	 * The mid vector (point straight forward) is the first element
	 * @return
	 */
	public Point[] get3FrontVectors() {
		switch (this) {
		case UP:
			return new Point[] { new Point(0, -1), new Point(-1, -1),
					new Point(1, -1) };
		case LEFT:
			return new Point[] { new Point(-1, 0), new Point(-1, 1),
					new Point(-1, -1) };
		case DOWN:
			return new Point[] { new Point(0, 1), new Point(1, 1),
					new Point(-1, 1) };
		case RIGHT:
			return new Point[] { new Point(1, 0), new Point(1, -1),
					new Point(1, 1) };
		default:
			throw new IllegalStateException();
		}
	}

}
