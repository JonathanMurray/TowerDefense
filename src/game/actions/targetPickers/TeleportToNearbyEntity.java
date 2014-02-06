package game.actions.targetPickers;

import game.Game.Team;
import game.Entities;
import game.GamePlayState;
import game.Map;
import game.Physics;
import game.actions.Action;
import game.actions.effects.Effect;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;
import game.objects.Unit;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import org.newdawn.slick.Animation;

public class TeleportToNearbyEntity /*implements Action*/{
	/*
	 * 
	 
		private double range;
		private Effect effect;
		private Team targetTeam;
		private Animation animation;

		public TeleportToNearbyEntity(double range, Effect effect, Team targetTeam, Animation animation) {
			this.effect = effect;
			this.range = range;
			this.targetTeam = targetTeam;
			this.animation = animation;
		}
		
		@Override
		public void execute(Entity actor) {
			ArrayList<Entity> targetsWithinRange = Entities.getEntitiesWithinRange(actor.getLocation(), range, targetTeam);
			Collections.shuffle(targetsWithinRange);
			for (Entity target : targetsWithinRange) {
				Point jumpLocation = getFreeLocationAdjacentToEnemy(target);
				if (jumpLocation != null) {
					jumpEffect((Unit)actor, jumpLocation, target);
					return;
				}
			}
			// no jump was made.. tough luck
		}

		private Point getFreeLocationAdjacentToEnemy(Entity enemy) {
			List<Point> adjacentLocations = Arrays.asList(Physics.getAdjacentLocations(enemy.getLocation()));
			Collections.shuffle(adjacentLocations);
			for (Point adjLocation : adjacentLocations) {
				if (!Map.blockedForHero(adjLocation.x, adjLocation.y)) {
					return adjLocation;
				}
			}
			return null;
		}

		private void jumpEffect(Unit actor, Point jumpLocation, Entity target) {
			GamePlayState.addSpecialEffect(new AnimationBasedVisualEffect(actor, animation));
			actor.teleportToLocation(jumpLocation.x, jumpLocation.y);
			GamePlayState.addSpecialEffect(new AnimationBasedVisualEffect(actor, animation));
			actor.faceTarget(target);
			effect.execute(actor, target);
		}

	*/
}
