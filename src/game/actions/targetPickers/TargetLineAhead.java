package game.actions.targetPickers;

import game.Game.Team;
import game.actions.ParameterName;
import game.actions.Parameters;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;
import game.objects.EntityNotFound;
import game.objects.Unit;
import game.Entities;
import game.GamePlayState;
import game.Physics;

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
		Entity hitTarget = getTargetOnLocation(Physics.getRelativeLocation(
				actorUnit.getLocation(),
				actorUnit.getDirection().getVector(1, 0)), renderAnimations);
		if(hitTarget != null){
			targets.add(hitTarget);
		}
		for (int i = 1; i < distance; i++) {
			Point targetLocation = Physics.getRelativeLocation(
					source, actorUnit.getDirection(), i);
			hitTarget = getTargetOnLocation(targetLocation, renderAnimations);
			if(hitTarget != null){
				targets.add(hitTarget);
			}
		}
		return targets.toArray(new Entity[0]);
	}
	
	

	private Point getSource(Unit actor, int distance) {
		Point left = Physics.getRelativeLocation(
				actor.getLocation(),
				actor.getDirection().getVector(1, -1));
		Point mid = Physics.getRelativeLocation(actor.getLocation(),
				actor.getDirection().getVector(1, 0));
		Point right = Physics.getRelativeLocation(
				actor.getLocation(),
				actor.getDirection().getVector(1, 1));
		if (Entities.getEntitiesOnLine(mid, actor.getDirection(),
				distance, targetTeam).size() == 0) {
			if (Entities.getEntitiesOnLine(left,
					actor.getDirection(), distance, targetTeam).size() > 0) {
				return left;
			} else if (Entities.getEntitiesOnLine(right,
					actor.getDirection(), distance, targetTeam).size() > 0) {
				return right;
			}
		}
		
		return mid;
	}

	private Entity getTargetOnLocation(Point location, boolean renderAnimations) {
		try {
			if(renderAnimations && animation != null){
				GamePlayState.addSpecialEffect(new AnimationBasedVisualEffect(Physics.getPixelCenterLocation(location),animation));
			}
			return Entities.getEntityOnLocation(location, targetTeam);
		} catch (EntityNotFound e) {
			return null;
		}
	}
	
	
	
	

}
