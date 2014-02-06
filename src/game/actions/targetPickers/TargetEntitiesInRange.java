package game.actions.targetPickers;

import game.Game.Team;
import game.actions.Action;
import game.actions.ParameterName;
import game.actions.Parameters;
import game.actions.effects.Effect;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;
import game.objects.enemies.Enemy;
import game.Entities;
import game.GamePlayState;

import java.util.ArrayList;

import javax.naming.OperationNotSupportedException;


import org.newdawn.slick.Animation;

public class TargetEntitiesInRange implements TargetPicker {
	
	private double range;
	private Animation animation;
	private Team targetTeam;
	private boolean includeActor;
	
	public TargetEntitiesInRange(double range, Animation animation, Team targetTeam, boolean includeActor) {
		this.range = range;
		this.animation = animation;
		this.targetTeam = targetTeam;
		this.includeActor = includeActor;
	}

	@Override
	public Entity[] pickTargets(Entity actor, boolean renderAnimations, Parameters parameters) {
		double range = parameters.getDouble(ParameterName.RANGE, this.range);
		ArrayList<Entity> closeTargets = Entities.getEntitiesWithinRange(actor.getLocation(), range, targetTeam);
		if(!includeActor){
			closeTargets.remove(actor);
		}
		if(renderAnimations && animation != null){
			GamePlayState.addSpecialEffect(new AnimationBasedVisualEffect(actor, animation));
		}
		return closeTargets.toArray(new Entity[0]);
	}


}
