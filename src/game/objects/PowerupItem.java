package game.objects;

import game.ResourceLoader;

import java.awt.Point;

import applicationSpecific.PowerupItemType;

public class PowerupItem{ //extends ItemOnGround {
//
//	private int duration;
//	private int timeSinceSpawn = 0;
//	private PowerupAction action;
//	private final static int timeLeftStartBlinking = 4000;
//	private boolean hasStartedBlinking = false;
//
//	public PowerupItem(PowerupItemType data, Point location) {
//		super(data.spriteRef, location);
//		this.duration = data.duration;
//		this.action = data.action;
//	}
//
//	@Override
//	public void update(int delta) {
//		super.update(delta);
//		timeSinceSpawn += delta;
//		if (duration - timeSinceSpawn < timeLeftStartBlinking
//				&& !hasStartedBlinking) {
//			super.sprite = ResourceLoader.createBlinkingAnimation(sprite
//					.getCurrentFrame());
//			hasStartedBlinking = true;
//		}
//		if (timeSinceSpawn >= duration) {
//			die();
//		}
//	}
//
//	public void wasSteppedOnByHero() {
//		action.execute(this);
//		die();
//	}
//
//	public static interface PowerupAction {
//		void execute(PowerupItem item);
//	}

}
