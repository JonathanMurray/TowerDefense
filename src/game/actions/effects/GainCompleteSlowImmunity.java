package game.actions.effects;

import game.actions.Parameters;
import game.objects.Entity;
import game.objects.enemies.Enemy;

public class GainCompleteSlowImmunity implements Effect{

	@Override
	public boolean execute(Entity actor, Entity target, Parameters context) {
		((Enemy)target).setCompletelyImmuneToSlow();
		return true;
	}

}
