package game.buffs;

import game.actions.Parameters;
import game.actions.effects.Effect;
import game.objects.Entity;

import org.newdawn.slick.Animation;

public class EffectAfterBuff extends Buff {

	private Effect effect;
	private int duration;

	public EffectAfterBuff(int duration, Effect effect, String id, Animation animation) {
		super(id, animation);
		this.duration = duration;
		this.effect = effect;
	}

	@Override
	public void applyEffectOn(Entity carrier) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void revertEffectOn(Entity carrier) {
		effect.execute(carrier, carrier, new Parameters());
	}

	@Override
	protected void continuousEffect(Entity carrier, int delta) {}

	@Override
	protected int getTotalDuration() {
		return duration;
	}

	@Override
	public Buff getCopy() {
		return new EffectAfterBuff(duration, effect, id, getAnimationCopy());
	}

}
