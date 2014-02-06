package game.buffs;

import game.objects.Entity;
import game.objects.Unit;

import org.newdawn.slick.Animation;

public class StunImmunity extends Buff {

	private int duration;

	public StunImmunity(String id, int duration, Animation animation) {
		super(id, animation);
		this.duration = duration;
	}

	@Override
	public void applyEffectOn(Entity carrier) {
		Unit targetUnit = (Unit) carrier;
		targetUnit.addStunImmunity();
	}

	@Override
	public void revertEffectOn(Entity carrier) {
		Unit targetUnit = (Unit) carrier;
		targetUnit.removeStunImmunity();
	}

	@Override
	protected void continuousEffect(Entity carrier, int delta) {
	}

	@Override
	protected int getTotalDuration() {
		return duration;
	}

	@Override
	public Buff getCopy() {
		return new StunImmunity(id, duration, getAnimationCopy());
	}

}