package game.actions.effects;

import game.GamePlayStateInstance;
import game.Map;
import game.PhysicsHandler;
import game.actions.Parameters;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;
import game.objects.Hero;
import game.objects.Unit;
import game.objects.enemies.Enemy;

import java.awt.Point;

import org.newdawn.slick.Animation;

public class TeleportToTarget implements Effect{
	
	private Animation animation;

	public TeleportToTarget(Animation animation){
		this.animation = animation;
	}

	@Override
	/**
	 * Returns true if managed to find a free square adjacent to target, and teleport there. 
	 * False if none found.
	 */
	public boolean execute(Entity actor, Entity target, Parameters context) {
		if(!(actor instanceof game.objects.Unit)){
			throw new IllegalStateException("TeleportToTarget effect is only for units");
		}
		boolean isHero = actor instanceof Hero;
		for(Point adjLocation : PhysicsHandler.getAdjacentLocations(target.getLocation())){
			if(isHero || ! ((Enemy)actor).isInTDMode()){
				if(Map.blockedForHero(adjLocation.x, adjLocation.y)){
					continue;
				}
			}else{
				if(Map.blockedForTowers(adjLocation.x, adjLocation.y)){
					continue;
				}
			}
			GamePlayStateInstance.INSTANCE.addSpecialEffect(new AnimationBasedVisualEffect(actor, animation));
			((Unit)actor).teleportToLocation(adjLocation.x, adjLocation.y);
			GamePlayStateInstance.INSTANCE.addSpecialEffect(new AnimationBasedVisualEffect(actor, animation));
			return true;
		}
		return false;
	}
}
