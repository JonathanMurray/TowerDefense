package game.actions.targetPickers;

import game.actions.Parameters;
import game.objects.Entity;

public interface TargetPicker {
	Entity[] pickTargets(Entity actor, boolean renderAnimations, Parameters parameters);
}
