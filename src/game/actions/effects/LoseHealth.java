package game.actions.effects;

import game.GamePlayStateInstance;
import game.actions.ParameterName;
import game.actions.Parameters;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;

import org.newdawn.slick.Animation;


public class LoseHealth implements Effect {
	
	private int amount;
	private Animation animation;
	private boolean ignoreArmor;

	public LoseHealth(int amount, Animation animation, boolean ignoreArmor) {
		
		this.amount = amount;
		this.animation = animation;
		this.ignoreArmor = ignoreArmor;
		
		
	}
	

	@Override
	public boolean execute(Entity actor, Entity target, Parameters context) {
		int amount = context.get(ParameterName.AMOUNT, this.amount);
		boolean ignoreArmor = context.getBoolean(ParameterName.IGNORE_ARMOR, this.ignoreArmor);
		int multipliedDamage = actor.getMultipliedDamage(amount);
		if(ignoreArmor){
			target.loseHealthIgnoringArmor(multipliedDamage);
		}else{
			target.loseHealth(multipliedDamage);
		}
		if(animation != null){
			GamePlayStateInstance.INSTANCE.addSpecialEffect(new AnimationBasedVisualEffect(target, animation));
		}
		return true;
	}
	
	public String toString(){
		return "[LoseHealth. amount:" + amount + ", ignoreArmor:" + ignoreArmor + "]";
	}

}
