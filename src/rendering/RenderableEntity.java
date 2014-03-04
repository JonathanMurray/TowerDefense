package rendering;

import game.Direction;

import java.awt.Point;

import org.newdawn.slick.Graphics;

public interface RenderableEntity {
	public void render(Graphics g);
	public void renderExtraVisuals(Graphics g);
	//Speeds are given in "pixels per second"
	public void setLocationAndSpeed(Point pixelLocation, Point movementSpeed);
	public void setDirection(Direction direction);
	public void setAutoUpdateSprite(boolean autoUpdate);
	public void setPercentHealth(int percentHealth);
	public void setPercentMana(int percentMana);


	/**
	 * Update location if movement is activated and speed != 0
	 * 
	 * @param delta
	 */
	public void update(int delta);
}
