package game;

import game.objects.Entity;

public interface EntityHealthListener {
	void healthChanged(Entity entity, int oldHealth, int newHealth);
}
