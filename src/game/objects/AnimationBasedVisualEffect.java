package game.objects;

import game.Map;
import game.ResourceLoader;

import java.awt.Point;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;

public class AnimationBasedVisualEffect implements VisualEffect {

	private Animation animation;
	private int x;
	private int y;

	public AnimationBasedVisualEffect(Entity targetEntity, String... imgRefs) {
		this(targetEntity.getPixelCenterLocation(), imgRefs);
	}
	
	public AnimationBasedVisualEffect(Entity targetEntity, Animation animation){
		this(targetEntity.getPixelCenterLocation(), animation);
	}


	public AnimationBasedVisualEffect(Point pixelCenterLocation, String... imgRefs) {
		this(pixelCenterLocation, ResourceLoader.createAnimation(false, imgRefs));
	}

	public AnimationBasedVisualEffect(Point pixelCenterLocation,Animation animation) {
		this.animation = animation.copy();
		this.animation.restart();
		this.animation.setLooping(false);
		this.x = (int) (pixelCenterLocation.x - animation.getWidth()/ 2);
		this.y = (int) (pixelCenterLocation.y - animation.getHeight()/ 2);
	}

	@Override
	public void render(Graphics g) {
		animation.draw(x, y);
	}

	@Override
	public void renderExtraVisuals(Graphics g) {
	}

	@Override
	public void update(int delta) {
	}

	@Override
	public boolean shouldBeRemovedFromGame() {
		return animation.isStopped();
	}

	@Override
	public Point getPixelCenterLocation() {
		int centerX = (int) Math.round(((double) x + 0.5) * Map.getTileWidth());
		int centerY = (int) Math
				.round(((double) y + 0.5) * Map.getTileHeight());
		return new Point(centerX, centerY);
	}

	public String toString() {
		return animation.toString();
	}



}
