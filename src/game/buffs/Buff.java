package game.buffs;

import game.objects.Entity;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;

public abstract class Buff {

	private int timeSinceAttachment = 0;
	private boolean hasRequestedRemoval = false;
	protected String id;
	protected Animation animation;

	public Buff(String id) {
		if(id == null || id.equals("")){
			throw new IllegalArgumentException("Invalid buffID = " + id);
		}
		this.id = id;
	}

	public Buff(String id, Animation animation) {
		this.id = id;
		this.animation = animation;
	}
	
	public final void resetDuration(){
		timeSinceAttachment = 0;
		hasRequestedRemoval = false;
	}

	public abstract void applyEffectOn(Entity carrier);

	public abstract void revertEffectOn(Entity carrier);

	public final void update(Entity carrier, int delta) {
		timeSinceAttachment += delta;
		if (!isPermanent() && timeSinceAttachment >= getTotalDuration() && !hasRequestedRemoval) {
			carrier.loseBuff(this);
			hasRequestedRemoval = true;
		}
		if (hasAnimation()) {
			animation.update(delta);
		}
		continuousEffect(carrier, delta);
	}

	public Image getCurrentFrame() {
		return animation.getCurrentFrame();
	}

	public boolean hasAnimation() {
		return animation != null;
	}

	public final String getID() {
		return id;
	}
	
	protected Animation getAnimationCopy(){
		if(animation == null){
			return null;
		}
		return animation.copy();
	}

	protected abstract void continuousEffect(Entity carrier, int delta);

	protected abstract int getTotalDuration();
	
	//Can be overridden!
	protected boolean isPermanent(){
		return getTotalDuration() <= 0;
	}

	public abstract Buff getCopy();

	public static class EmptyBuff extends Buff {
		private int duration;

		public EmptyBuff(String id, int duration, Animation animation) {
			super(id, animation);
			this.duration = duration;
		}

		public void applyEffectOn(Entity carrier) {
		}

		public void revertEffectOn(Entity carrier) {
		}

		protected void continuousEffect(Entity carrier, int delta) {
		}

		protected int getTotalDuration() {
			return duration;
		}

		public boolean isMovementImpairing() {
			return false;
		}

		public Buff getCopy() {
			return new EmptyBuff(id, duration, animation.copy());
		}

	}
	
	public String toString(){
		return "" + id;
	}
	
	@Override
	public boolean equals(Object other){
		if(other == null || !(other instanceof Buff)){
			return false;
		}
		return getID().equals(((Buff)other).getID());
	}

}
