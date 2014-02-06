package game.actions.targetPickers;

import game.actions.Parameters;
import game.objects.Entity;

public class TargetSelf implements TargetPicker{

	@Override
	public Entity[] pickTargets(Entity actor, boolean renderAnimations, Parameters parameters) {
		return new Entity[]{actor};
	}

}
