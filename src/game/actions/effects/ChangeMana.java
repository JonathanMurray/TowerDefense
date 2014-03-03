package game.actions.effects;

import game.GamePlayStateInstance;
import game.actions.ParameterName;
import game.actions.Parameters;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;
import game.objects.Hero;

import java.util.Map;

import org.newdawn.slick.Animation;

public class ChangeMana implements Effect{

	private int amount;
	private Animation animation;
	

	public ChangeMana(int amount, Animation animation) {
		this.animation = animation;
		this.amount = amount;
	}

	/**
	 * Always returns true
	 */
	@Override
	public boolean execute(Entity actor, Entity target, Parameters context) {
		System.out.println("\n---------------------------------\nchangeMana.execute. amount = " + amount); //TODO
		int amount = (int) context.get(ParameterName.AMOUNT, this.amount);
		System.out.println("context sensitivie amount = " + amount + "\n");
		((Hero)actor).addToMana(amount);
		if(animation != null){
			GamePlayStateInstance.INSTANCE.addSpecialEffect(new AnimationBasedVisualEffect(target, animation));
		}
		return true;
	}
}
