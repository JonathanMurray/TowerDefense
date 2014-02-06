package game.objects;

import game.actions.Action;

public interface EntityActionListener {
	void entityDidAction(Entity actor, Action action);
}
