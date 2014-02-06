package game.actions.effects;

import game.Entities;
import game.Game.Team;
import game.actions.ParameterName;
import game.actions.Parameters;
import game.objects.Entity;

import java.util.Map;

public class AoE_Effect implements Effect{
	

	private double range;
	private Effect effect;
	private Team targetTeam;

	public AoE_Effect(double range, Effect effect, Team targetTeam){
		this.range = range;
		this.effect = effect;
		this.targetTeam = targetTeam;
	}

	@Override
	public boolean execute(Entity actor, Entity target, Parameters context) {
		boolean someSucceded = false;
		double range = context.getDouble(ParameterName.RANGE, this.range);
		for(Entity targets : Entities.getEntitiesWithinRange(target.getLocation(), range, targetTeam)){
			if(effect.execute(actor, targets, context)){
				someSucceded = true;
			}
		}
		return someSucceded;
	}

	

}
