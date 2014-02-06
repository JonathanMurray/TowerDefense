package game.actions.effects;

import game.actions.Parameters;
import game.objects.Entity;

public class CompositeEffectUntilFail implements Effect{
	private Effect[] effects;

	public CompositeEffectUntilFail(Effect... effects){
		this.effects = effects;
	}

	/**
	 * Returns true if all succeded. Keeps applying effects until one of them fails.
	 */
	@Override
	public boolean execute(Entity actor, Entity target, Parameters context) {
		for(Effect effect : effects){
			if(!effect.execute(actor, target, context)){
				return false;
			}
		}
		return true;
	}
}
