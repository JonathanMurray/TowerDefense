package game.actions.effects;

import game.GamePlayState;
import game.actions.ParameterName;
import game.actions.Parameters;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;
import game.objects.HeroInfo;

import org.newdawn.slick.Animation;

public class DrainLife implements Effect {

	private int damageAmount;
	private int healthAmount;
	private Animation animation;

	public DrainLife(int damageAmount, int healthAmount, Animation animation) {
		this.damageAmount  = damageAmount;
		this.healthAmount = healthAmount;
		this.animation = animation;
	}

	/**
	 * Always returns true
	 */
	@Override
	public boolean execute(Entity actor, Entity target, Parameters context) {
		int damageAmount = (int) context.get(ParameterName.DAMAGE_AMOUNT, this.damageAmount);
		int healthAmount = (int) context.get(ParameterName.HEALING_AMOUNT, this.healthAmount);
		int multipliedDamage = actor.getMultipliedDamage(damageAmount);
		target.loseHealthIgnoringArmor(multipliedDamage);
		actor.gainHealth(healthAmount);
		if(animation != null){
			GamePlayState.addSpecialEffect(new AnimationBasedVisualEffect(actor, animation));
			GamePlayState.addSpecialEffect(new AnimationBasedVisualEffect(target, animation));
		}
		return true;
	}

}
