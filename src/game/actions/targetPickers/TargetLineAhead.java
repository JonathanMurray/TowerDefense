package game.actions.targetPickers;

import game.EntityHandler;
import game.GamePlayStateInstance;
import game.PhysicsHandler;
import game.actions.ParameterName;
import game.actions.Parameters;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;
import game.objects.EntityNotFound;
import game.objects.Team;
import game.objects.Unit;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Animation;

public class TargetLineAhead implements TargetPicker {

	private int skipNFirstSquares; //TODO
	private int distance;
	private Animation animation;
	private Team targetTeam;

	public TargetLineAhead(int distance, int skipNFirstSquares, Animation animation, Team targetTeam) {
		this.distance = distance;
		this.skipNFirstSquares = skipNFirstSquares;
		this.animation = animation;
		this.skipNFirstSquares = skipNFirstSquares;
		this.targetTeam = targetTeam;
	}

	@Override
	public Entity[] pickTargets(Entity actor, boolean renderAnimations, Parameters parameters) {
		int distance = parameters.get(ParameterName.DISTANCE, this.distance);
		Unit actorUnit = (Unit)actor;
		Point source = getSource(actorUnit, distance);
		List<Entity> targets = new ArrayList<Entity>();
		Entity hitTarget = getTargetOnLocation(PhysicsHandler.getRelativeLocation(
				actorUnit.getLocation(),
				actorUnit.getDirection().getVector(1, 0)), renderAnimations);
		if(hitTarget != null){
			targets.add(hitTarget);
		}
		for (int i = 1; i < distance; i++) {
			Point targetLocation = PhysicsHandler.getRelativeLocation(
					source, actorUnit.getDirection(), i);
			hitTarget = getTargetOnLocation(targetLocation, renderAnimations);
			if(hitTarget != null){
				targets.add(hitTarget);
			}
		}
		return targets.toArray(new Entity[0]);
	}
	
	

	private Point getSource(Unit actor, int distance) {
		Point left = PhysicsHandler.getRelativeLocation(
				actor.getLocation(),
				actor.getDirection().getVector(1, -1));
		Point mid = PhysicsHandler.getRelativeLocation(actor.getLocation(),
				actor.getDirection().getVector(1, 0));
		Point right = PhysicsHandler.getRelativeLocation(
				actor.getLocation(),
				actor.getDirection().getVector(1, 1));
		if (EntityHandler.getEntitiesOnLine(mid, actor.getDirection(),
				distance, targetTeam).size() == 0) {
			if (EntityHandler.getEntitiesOnLine(left,
					actor.getDirection(), distance, targetTeam).size() > 0) {
				return left;
			} else if (EntityHandler.getEntitiesOnLine(right,
					actor.getDirection(), distance, targetTeam).size() > 0) {
				return right;
			}
		}
		
		return mid;
	}

	private Entity getTargetOnLocation(Point location, boolean renderAnimations) {
		try {
			if(renderAnimations && animation != null){
				GamePlayStateInstance.INSTANCE.addSpecialEffect(new AnimationBasedVisualEffect(PhysicsHandler.getPixelCenterLocation(location),animation));
			}
			return EntityHandler.getEntityOnLocation(location, targetTeam);
		} catch (EntityNotFound e) {
			return null;
		}
	}
	
	
	
	

}
