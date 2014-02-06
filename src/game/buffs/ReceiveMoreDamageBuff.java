package game.buffs;

import game.EntityHealthListener;
import game.objects.Entity;

import org.newdawn.slick.Animation;

public class ReceiveMoreDamageBuff extends Buff implements EntityHealthListener {

	private int duration;
	private double damageMultiplier;

	public ReceiveMoreDamageBuff(int duration, double damageMultiplier, String id,
			Animation animation) {
		super(id, animation);
		this.duration = duration;
		this.damageMultiplier = damageMultiplier;
	}

	@Override
	public void applyEffectOn(Entity carrier) {
		carrier.addHealthListener(this);
	}

	@Override
	public void revertEffectOn(Entity carrier) {
		carrier.removeHealthListener(this);
	}

	@Override
	protected void continuousEffect(Entity carrier, int delta) {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getTotalDuration() {
		return duration;
	}

	@Override
	public Buff getCopy() {
		return new ReceiveMoreDamageBuff(duration, damageMultiplier, id, getAnimationCopy());
	}

	@Override
	public void healthChanged(Entity entity, int oldHealth, int newHealth) {
		int damageTaken = oldHealth - newHealth;
		if (damageTaken > 0) {
			if (damageMultiplier > 1) {
				int extraDamage = (int) ((damageMultiplier - (double) 1) * (double) damageTaken);
				entity.loseHealthIgnoringArmorWithoutNotifyingListeners(extraDamage);
			} else {
				int reducedDamage = (int) (((double) 1 - damageMultiplier) * (double) damageTaken);
				entity.gainHealthWithoutNotifyingListeners(reducedDamage);
			}

		}
	}

}
