package game.actions.effects;

import game.actions.Parameters;
import game.objects.Entity;
import game.objects.Unit;

public class FaceTarget implements Effect{

	@Override
	public boolean execute(Entity actor, Entity target, Parameters context) {
		if(!(actor instanceof Unit)){
			throw new IllegalStateException("Only units can face");
		}
		((Unit)actor).faceTarget(target);
		return true;
	}

}
