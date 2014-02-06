package game.actions.effects;

import game.actions.Parameters;
import game.objects.Entity;

public class CompositeEffect implements Effect{
	
	private Effect[] effects;

	public CompositeEffect(Effect... effects){
		this.effects = effects;
	}

	/**
	 * Returns true if some succeeded
	 */
	@Override
	public boolean execute(Entity actor, Entity target, Parameters context) {
		boolean someSucceeded = false;
		for(Effect effect : effects){
			if(effect.execute(actor, target, context)){
				someSucceeded = true;
			}
		}
		return someSucceeded;
	}

}
