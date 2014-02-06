package game.objects;

import java.awt.Point;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Vector2f;

public class Projectile implements VisibleObject {

	private double pixelX;
	private double pixelY;

	private double destinationPixelX;
	private double destinationPixelY;

	private double movementPerDelta;

	private boolean hasReachedDestination = false;
	private double previousDistanceSq = Double.MAX_VALUE;

	private int size;
	private Color color;

	public Projectile(int pixelX, int pixelY, int pixelDestinationX,
			int pixelDestinationY, double pixelMovementPerDelta, int size,
			Color color) {

		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.destinationPixelX = pixelDestinationX;
		this.destinationPixelY = pixelDestinationY;
		this.movementPerDelta = pixelMovementPerDelta;
		this.size = size;
		this.color = color;
	}

	public Projectile(Point pixelLocation, Point pixelDestination,
			double pixelMovementPerDelta, int size, Color color) {
		this(pixelLocation.x, pixelLocation.y, pixelDestination.x,
				pixelDestination.y, pixelMovementPerDelta, size, color);
	}

	@Override
	public void update(int delta) {

		if (!hasReachedDestination) {
			double movementDistance = (double) delta
					* (double) movementPerDelta;
			Vector2f distanceVector = new Vector2f(
					(float) (destinationPixelX - pixelX),
					(float) (destinationPixelY - pixelY));

			distanceVector.scale((float) movementDistance
					/ distanceVector.length());

			pixelX += distanceVector.x;
			pixelY += distanceVector.y;

		}

		if (hasReachedDestination(delta)) {
			hasReachedDestination = true;
		}
	}

	@Override
	public void render(Graphics g) {
		g.setColor(color);
		g.fillRect(Math.round(pixelX), Math.round(pixelY), size, size);
	}

	@Override
	public void renderExtraVisuals(Graphics g) {
		/* NO EXTRA VISUALS FOR ITEMS, SO FAR! */
	}

	@Override
	public boolean shouldBeRemovedFromGame() {
		return hasReachedDestination;
	}

	private boolean hasReachedDestination(int delta) {
		double distanceSquared = Math.pow(Math.abs(pixelX - destinationPixelX),
				2) + Math.pow(Math.abs(pixelY - destinationPixelY), 2);
		boolean hasReached = distanceSquared < Math.pow(delta
				* movementPerDelta, 2)
				|| distanceSquared > previousDistanceSq;
		previousDistanceSq = distanceSquared;
		return hasReached;
	}

	public String toString() {
		return "Projectile (" + pixelX + "," + pixelY + ")  -->  ("
				+ destinationPixelX + "," + destinationPixelY + ")";
	}

	@Override
	public Point getPixelCenterLocation() {
		return new Point((int) pixelX, (int) pixelY);
	}


}
