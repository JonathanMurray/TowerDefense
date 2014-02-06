package game.objects;

import java.awt.Point;

import org.newdawn.slick.Graphics;

public interface VisibleObject {
	void render(Graphics g);
	void renderExtraVisuals(Graphics g);
	void update(int delta);
	boolean shouldBeRemovedFromGame();
	Point getPixelCenterLocation();
}
