package game.actions.effects;

import game.actions.Parameters;
import game.objects.Entity;

import java.util.Map;

public interface Effect {
	boolean execute(Entity actor, Entity target, Parameters context);
}
