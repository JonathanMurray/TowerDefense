package game.buffs;

import game.EntityHealthListener;
import game.actions.Parameters;
import game.actions.effects.Effect;
import game.objects.Entity;

import org.newdawn.slick.Animation;

public class EffectOnSelfWhenLowLife extends Buff implements EntityHealthListener{
	
	private int healthThresholdPercent;
	private Effect effect;
	private int duration;

	public EffectOnSelfWhenLowLife(String id, int healthThresholdPercent, Effect effect, Animation animation, int duration) {
		super(id, animation);
		this.effect = effect;
		this.healthThresholdPercent = healthThresholdPercent;
		this.duration = duration;
	}

	@Override
	public void applyEffectOn(Entity carrier) {
		carrier.addHealthListener(this);
	}

	@Override
	public void revertEffectOn(Entity carrier) {}

	@Override
	protected void continuousEffect(Entity carrier, int delta) {
	}

	@Override
	protected int getTotalDuration() {
		return duration;
	}

	@Override
	public Buff getCopy() {
		return new EffectOnSelfWhenLowLife(id, healthThresholdPercent, effect, getAnimationCopy(), duration);
	}

	@Override
	public void healthChanged(Entity entity, int oldHealth, int newHealth) {
		if(entity.isBelowPercentHealth(healthThresholdPercent)){
			effect.execute(entity, entity, new Parameters());
			entity.loseBuff(this);
			entity.removeHealthListener(this);
		}
	}

}
