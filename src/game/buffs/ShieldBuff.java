package game.buffs;

import game.EntityHealthListener;
import game.GamePlayStateInstance;
import game.ResourceLoader;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;

import org.newdawn.slick.Animation;

public class ShieldBuff extends Buff implements EntityHealthListener {

	private int absorbAmount;
	private int remainingShield;
	private int duration;

	public ShieldBuff(int absorbAmount, int duration, String id,Animation animation) {
		super(id, animation);
		this.absorbAmount = absorbAmount;
		this.remainingShield = absorbAmount;
		this.duration = duration;
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
	}

	@Override
	protected int getTotalDuration() {
		return duration;
	}

	@Override
	public Buff getCopy() {
		return new ShieldBuff(absorbAmount, duration, id, getAnimationCopy());
	}

	@Override
	public void healthChanged(Entity entity, int oldHealth, int newHealth) {
		int damageTaken = oldHealth - newHealth;

		if (damageTaken > 0 && remainingShield > 0) {
			entity.gainHealth(Math.min(damageTaken, remainingShield));
			remainingShield -= damageTaken;

			GamePlayStateInstance.INSTANCE.addSpecialEffect(new AnimationBasedVisualEffect(entity
					.getPixelCenterLocation(), ResourceLoader
					.createTileScaledAnimation(false, "abilities/bubble.png")));
			if (remainingShield <= 0) {
				entity.loseBuff(this);
				entity.removeHealthListener(this);
			}
		}
	}
}
