package game.objects;


import java.awt.Point;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class LineBasedVisualEffect implements VisualEffect{
	
	private int timeSinceCreation = 0;
	private Point startPoint;
	private Point endPoint;
	private Color color;
	private int duration;

	public LineBasedVisualEffect(Point startPoint, Point endPoint, Color color, int duration){
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.color = color;
		this.duration = duration;
	}

	@Override
	public void render(Graphics g) {
		g.setColor(color);
		g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
	}

	@Override
	public void renderExtraVisuals(Graphics g) {}

	@Override
	public void update(int delta) {
		timeSinceCreation += delta;
	}

	@Override
	public boolean shouldBeRemovedFromGame() {
		return timeSinceCreation > duration;
	}

	@Override
	public Point getPixelCenterLocation() {
		return new Point((startPoint.x + endPoint.x)/2, (startPoint.y + endPoint.y)/2);
	}

}
