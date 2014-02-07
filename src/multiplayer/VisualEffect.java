package multiplayer;

import java.awt.Point;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;

class VisualEffect {
	Point pixelPosition;
	int speedX;
	int speedY;
	Animation animation;
	
	public VisualEffect(Point pixelPosition, int speedX, int speedY, Animation animation) {
		if(animation == null || pixelPosition == null){
			throw new IllegalArgumentException(animation + "   " + pixelPosition);
		}
		this.pixelPosition = pixelPosition;
		this.speedX = speedX;
		this.speedY = speedY;
		this.animation = animation;
	}
	
	void render(Graphics g){
		g.drawImage(animation.getCurrentFrame(), pixelPosition.x, pixelPosition.y);
	}
}
