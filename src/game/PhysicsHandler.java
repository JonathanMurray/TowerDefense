package game;

import java.awt.Point;

public class PhysicsHandler {

	public static Point[] getAdjacentLocations(Point centerLocation) {
		Point[] adjacentLocations = new Point[8];
		int i = 0;
		for (int y = centerLocation.y - 1; y <= centerLocation.y + 1; y++) {
			for (int x = centerLocation.x - 1; x <= centerLocation.x + 1; x++) {
				Point p = new Point(x, y);
				if (!p.equals(centerLocation)) {
					adjacentLocations[i] = p;
					i++;
				}
			}
		}
		return adjacentLocations;
	}

	public static Point getRelativeLocation(Point source, Direction direction,
			int distance) {
		Point location = new Point(source);
		location.translate(direction.dx * distance, direction.dy * distance);
		return location;
	}

	public static Point getRelativeLocation(Point source, Direction direction,
			int stepsForward, int stepsRight) {
		Point location = new Point(source);
		Point vector = direction.getVector(stepsForward, stepsRight);
		location.translate(vector.x, vector.y);
		return location;
	}

	public static Point getRelativeLocation(Point source, Point vector) {
		Point location = new Point(source);
		location.translate(vector.x, vector.y);
		return location;
	}

	public static Point getPixelCenterLocation(Point location) {
		int centerX = (int) Math.round(((double) location.x + 0.5)
				* Map.getTileWidth());
		int centerY = (int) Math.round(((double) location.y + 0.5)
				* Map.getTileHeight());
		return new Point(centerX, centerY);
	}

	public static boolean arePointsWithinDistance(Point p1, Point p2, double d) {
		return p1.distanceSq(p2) <= Math.pow(d, 2);
	}

	public static boolean arePointsAdjacent(Point p1, Point p2) {
		return arePointsWithinDistance(p1, p2, getAdjacencyDistance());
	}

	public static double getAdjacencyDistance() {
		return 1.9;
	}

}
