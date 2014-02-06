package game;

import game.objects.Entity;

public interface DeathListener {
	void entityDied(Entity entity, boolean wasKilled);
}
