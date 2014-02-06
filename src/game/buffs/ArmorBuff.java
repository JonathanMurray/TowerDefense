package game.buffs;

import game.objects.Entity;

import org.newdawn.slick.Animation;

public class ArmorBuff extends Buff {

	private int amount;
	private int duration;

	public ArmorBuff(int amount, int duration, String id, Animation animation) {
		super(id, animation);
		this.amount = amount;
		this.duration = duration;
	}
//
//	public ArmorBuff(int amount, int duration, String id) {
//		super(id);
//		this.amount = amount;
//		this.duration = duration;
//	}

	@Override
	public void applyEffectOn(Entity target) {
		target.changeArmor(amount);
	}

	@Override
	public void revertEffectOn(Entity target) {
		target.changeArmor(-amount);
	}

	@Override
	protected void continuousEffect(Entity target, int delta) {
	}

	@Override
	protected int getTotalDuration() {
		return duration;
	}

	@Override
	public Buff getCopy() {
		
		return new ArmorBuff(amount, duration, id, getAnimationCopy());
	}

}
