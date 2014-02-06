package game.actions.targetPickers;

import game.Entities;
import game.Game.Team;
import game.GamePlayState;
import game.Physics;
import game.actions.Parameters;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;
import game.objects.EntityNotFound;
import game.objects.Unit;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Animation;

public class TargetOneInFront implements TargetPicker{
	private Animation animation;
	private Team targetTeam;

	public TargetOneInFront(Animation animation, Team targetTeam){
		this.animation = animation;
		this.targetTeam = targetTeam;
	}

	@Override
	public Entity[] pickTargets(Entity actor, boolean renderAnimations, Parameters parameters) {
		Point[] frontVectors = ((Unit)actor).getDirection().get3FrontVectors();
		List<Entity> targets = new ArrayList<Entity>();
		for (Point vector : frontVectors) {
			try {
				Point targetLocation = Physics.getRelativeLocation(
						actor.getLocation(), vector);
				Entity target = Entities.getEntityOnLocation(targetLocation, targetTeam);
				targets.add(target);
			} catch (EntityNotFound e) {}
		}
		if(targets.size() == 0){
			Point oneAhead = Physics.getRelativeLocation(actor.getLocation(), ((Unit)actor).getDirection(), 1);
			if(animation != null){
				GamePlayState.addSpecialEffect(new AnimationBasedVisualEffect(oneAhead, animation));
			}
			return new Entity[0];
		}else{
			if(animation != null){
				GamePlayState.addSpecialEffect(new AnimationBasedVisualEffect(targets.get(0), animation));
			}
			return targets.subList(0, 1).toArray(new Entity[0]);
		}
		
	}



}
