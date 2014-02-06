package game.actions.effects;

import game.actions.ParameterName;
import game.actions.Parameters;
import game.objects.Entity;

import java.util.Map;
import java.util.Random;


public class ChanceEffect implements Effect{
	
	private double chance;
	private Effect effect;
	
	public ChanceEffect(double chance, Effect effect){
		this.chance = chance;
		this.effect = effect;
	}

	@Override
	/**
	 * Returns true if effect was executed and it succeded.
	 */
	public boolean execute(Entity actor, Entity target, Parameters context) {
		double chance = context.getDouble(ParameterName.CHANCE, this.chance);
		if(new Random().nextDouble() < chance){
			if(effect.execute(actor, target, context)){
				return true;
			}
		}
		return false;
	}

}
