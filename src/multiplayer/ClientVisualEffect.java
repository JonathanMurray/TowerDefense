package multiplayer;

import java.awt.Point;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;

public class ClientVisualEffect {
	Point pixelLocation;
	int horMoveCooldown;
	int verMoveCooldown;
	int timeSinceHorMove = 0;
	int timeSinceVerMove = 0;
	boolean hasHorMove;
	boolean hasVerMove;
	Animation animation;
	
	public ClientVisualEffect(Point pixelPosition, int horPixelsPerSec, int verPixelsPerSec, Animation animation) {
		if(animation == null || pixelPosition == null){
			throw new IllegalArgumentException(animation + "   " + pixelPosition);
		}
		setLocationAndSpeed(pixelPosition, horPixelsPerSec, verPixelsPerSec);
		this.animation = animation;
	}
	
	public void setLocationAndSpeed(Point pixelLocation, int horPixelsPerSec, int verPixelsPerSec){
		this.pixelLocation = pixelLocation;
		if(horPixelsPerSec > 0){
			this.horMoveCooldown = 1000 / horPixelsPerSec;
			hasHorMove = true;
		}
		if(verPixelsPerSec > 0){
			this.verMoveCooldown = 1000 / verPixelsPerSec;
			hasVerMove = true;
		}
	}
	
	public void render(Graphics g){
		g.drawImage(animation.getCurrentFrame(), pixelLocation.x, pixelLocation.y);
	}
	
	public void update(int delta){
		if(hasHorMove){
			timeSinceHorMove += delta;
			while(timeSinceHorMove > horMoveCooldown){
				pixelLocation.x ++;
				timeSinceHorMove -= horMoveCooldown;
			}
		}
		if(hasVerMove){
			timeSinceVerMove += delta;
			while(timeSinceVerMove > verMoveCooldown){
				pixelLocation.y ++;
				timeSinceVerMove -= verMoveCooldown;
			}
		}
	}
}
