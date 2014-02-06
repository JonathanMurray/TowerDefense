package game;

import game.Game.Team;
import game.objects.Entity;
import game.objects.EntityNotFound;
import game.objects.HeroInfo;
import game.objects.enemies.Enemy;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import org.newdawn.slick.Input;

import rendering.HUD;

public class Entities {

	private static ArrayList<Enemy> enemies = new ArrayList<Enemy>();

	static void notifyEnemyWasAdded(Enemy enemy) {
		enemies.add(enemy);
	}

	public static void handleMouseOverEnemyInput(Input input, int delta, HUD hud) {
		for (Enemy enemy : enemies) {
			Point mousePixelLocation = new Point(input.getMouseX(), input.getMouseY());
			if (enemy.containsPixelLocation(mousePixelLocation)) {
				hud.enemyMouseOver(enemy.getPixelLocation(), enemy.getMouseOverText());
				return;
			}
		}
	}

	public static Entity getEntityWithinRange(Point location, double maxRange, Team entityTeam) throws EntityNotFound {
		if (entityTeam == Team.EVIL) {
			for (Enemy enemy : enemies) {
				if (enemy.isLocationWithinDistance(location, maxRange)) {
					return enemy;
				}
			}
		} else {
			if (HeroInfo.INSTANCE.isHeroAlive() && HeroInfo.INSTANCE.getHero().isLocationWithinDistance(location, maxRange)) {
				return HeroInfo.INSTANCE.getHero();
			}
		}

		throw new EntityNotFound("No close entity");
	}

	public static ArrayList<Entity> getEntitiesWithinRange(Point location, double maxRange, Team entityTeam) {
		ArrayList<Entity> entitiesWithinRange = new ArrayList<Entity>();
		if (entityTeam == Team.EVIL) {
			for (Enemy enemy : enemies) {
				if (enemy.isLocationWithinDistance(location, maxRange)) {
					entitiesWithinRange.add(enemy);
				}
			}
		} else {
			if (HeroInfo.INSTANCE.isHeroAlive() && HeroInfo.INSTANCE.getHero().isLocationWithinDistance(location, maxRange)) {
				entitiesWithinRange.add(HeroInfo.INSTANCE.getHero());
			}
		}

		return entitiesWithinRange;
	}

	public static ArrayList<Entity> getEntitiesAdjacentTo(Point location, Team entityTeam) {
		ArrayList<Entity> adjacentEntities = new ArrayList<Entity>();
		if (entityTeam == Team.EVIL) {
			for (Entity enemy : enemies) {
				if (enemy.isLocationWithinDistance(location, Physics.getAdjacencyDistance())) {
					adjacentEntities.add(enemy);
				}
			}
		} else {
			if (HeroInfo.INSTANCE.isHeroAlive() && HeroInfo.INSTANCE.getHero().isLocationAdjacent(location)) {
				adjacentEntities.add(HeroInfo.INSTANCE.getHero());
			}
		}

		return adjacentEntities;
	}

	public static Entity getEntityOnLocation(Point location, Team entityTeam) throws EntityNotFound {
		if (entityTeam == Team.EVIL) {
			for (Entity enemy : enemies) {
				if (enemy.getLocation().equals(location)) {
					return enemy;
				}
			}
		} else {
			if (HeroInfo.INSTANCE.isHeroAlive() && HeroInfo.INSTANCE.getHero().getLocation() == location) {
				return HeroInfo.INSTANCE.getHero();
			}
		}
		throw new EntityNotFound("No enemy on that location");
	}

	public static Entity getOneFrontEntity(Point location, Direction direction, Team entityTeam) throws EntityNotFound {
		for (Point vector : direction.get3FrontVectors()) {
			Point targetLocation = Physics.getRelativeLocation(location, vector);
			try {
				return Entities.getEntityOnLocation(targetLocation, entityTeam);
			} catch (EntityNotFound e) {
			}
		}
		throw new EntityNotFound("No front entity!");
	}

	public static Entity getEntityOnRelativeLocation(Point location, Direction direction, int distance, Team entityTeam)
			throws EntityNotFound {
		Point adjacentLocation = Physics.getRelativeLocation(location, direction, distance);
		return getEntityOnLocation(adjacentLocation, entityTeam);
	}

	public static ArrayList<Entity> getEntitiesOnLine(Point source, Direction direction, int distance, Team entityTeam) {
		ArrayList<Entity> entitiesOnLine = new ArrayList<Entity>();
		for (int i = 1; i <= distance; i++) {
			try {
				Entity entity = getEntityOnRelativeLocation(source, direction, i, entityTeam);
				entitiesOnLine.add(entity);
			} catch (EntityNotFound e) {
			}
		}
		return entitiesOnLine;
	}

	static void removeDeadEnemies() {
		Iterator<Enemy> enemiesIt = enemies.iterator();
		while (enemiesIt.hasNext()) {
			Enemy enemy = enemiesIt.next();
			if (enemy.shouldBeRemovedFromGame()) {
				enemiesIt.remove();
			}
		}
	}
}
