package game.buffs;

import game.actions.Parameters;
import game.actions.effects.Effect;
import game.objects.Entity;

import org.newdawn.slick.Animation;



public class RepeatEffectOnSelfBuff extends Buff{
	private int actionCooldown;
	private int timeSinceAction;
	private int duration;
	private Effect effect;

	public RepeatEffectOnSelfBuff(Effect effect, int actionCooldown,
			int duration, String buffId, Animation animation) {
		super(buffId, animation);
		this.effect = effect;
		this.actionCooldown = actionCooldown;
		timeSinceAction = 0;
		this.duration = duration;
	}

	@Override
	public void applyEffectOn(Entity carrier) {
		
	}

	@Override
	public void revertEffectOn(Entity carrier) {

	}

	@Override
	protected void continuousEffect(Entity carrier, int delta) {
		timeSinceAction += delta;
		if (timeSinceAction >= actionCooldown) {
			effect.execute(carrier, carrier, new Parameters());
			timeSinceAction = 0;
		}
	}


	@Override
	protected int getTotalDuration() {
		return duration;
	}

	@Override
	public Buff getCopy() {
		return new RepeatEffectOnSelfBuff(effect, actionCooldown, duration, id, getAnimationCopy());
	}
}
