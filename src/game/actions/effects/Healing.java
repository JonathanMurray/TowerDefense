package game.actions.effects;


import game.OfflineGamePlayState;
import game.actions.ParameterName;
import game.actions.Parameters;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;

import org.newdawn.slick.Animation;

public class Healing implements Effect {
	
	private int amount;
	private Animation animation;
	

	public Healing(int amount, Animation animation) {
		this.animation = animation;
		this.amount = amount;
	}

	@Override
	public boolean execute(Entity actor, Entity target, Parameters context) {
		int amount = context.get(ParameterName.AMOUNT, this.amount);
		target.gainHealth(amount);
		if(animation != null){
			OfflineGamePlayState.addSpecialEffect(new AnimationBasedVisualEffect(target, animation));
		}
		return true;
	}

}
