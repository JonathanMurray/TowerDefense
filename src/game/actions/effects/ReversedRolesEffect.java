package game.actions.effects;

import game.actions.Parameters;
import game.objects.Entity;

public class ReversedRolesEffect implements Effect{
	
	private Effect effect;
	
	public ReversedRolesEffect(Effect effect){
		this.effect = effect;
	}

	@Override
	public boolean execute(Entity actor, Entity target, Parameters context) {
		return effect.execute(target, actor, context);
	}

}
