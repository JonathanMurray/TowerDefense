package game.objects;

import game.Map;
import game.ResourceLoader;

import java.awt.Point;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public abstract class ItemOnGround{ //implements VisibleObject {
//	protected int x;
//	protected int y;
//	private boolean alive = true;
//
//	protected Animation sprite;
//
//	public ItemOnGround(String spriteRef, Point location) {
//		Image img = ResourceLoader.createImage(spriteRef).getScaledCopy(
//				Map.getTileWidth(), Map.getTileHeight());
//		sprite = new Animation(new Image[] { img }, 1000);
//		x = location.x;
//		y = location.y;
//	}
//
//	public void die() {
//		alive = false;
//	}
//
//	@Override
//	public void render(Graphics g) {
//		Point topLeft = new Point(x * Map.getTileWidth(), y
//				* Map.getTileHeight());
//		g.drawImage(sprite.getCurrentFrame(), topLeft.x, topLeft.y);
//	}
//
//	@Override
//	public void renderExtraVisuals(Graphics g) {
//		/* NO EXTRA VISUALS FOR ITEMS, SO FAR! */
//	}
//
//	@Override
//	public boolean shouldBeRemovedFromGame() {
//		return !alive;
//	}
//
//	public Point getLocation() {
//		return new Point(x, y);
//	}
//
//	public void update(int delta) {
//		if (HeroInfo.isHeroAlive()) {
//			Point heroLocation = HeroInfo.getHero().getLocation();
//			if (getLocation().equals(heroLocation)) {
//				wasSteppedOnByHero();
//			}
//		}
//		sprite.update(delta);
//	}
//
//	public abstract void wasSteppedOnByHero();
//
//	@Override
//	public Point getPixelCenterLocation() {
//		int centerX = (int) Math.round(((double) x + 0.5) * Map.getTileWidth());
//		int centerY = (int) Math
//				.round(((double) y + 0.5) * Map.getTileHeight());
//		return new Point(centerX, centerY);
//	}

}
