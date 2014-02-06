package game.actions.targetPickers;

import game.actions.ParameterName;
import game.actions.Parameters;
import game.objects.Entity;

import java.util.ArrayList;
import java.util.List;


public class FilterBelowHealthThreshold implements TargetPicker{
	
	TargetPicker targetPicker;
	int healthThresholdPercent;
	
	public FilterBelowHealthThreshold(TargetPicker targetPicker, int healthThresholdPercent){
		this.targetPicker = targetPicker;
		this.healthThresholdPercent = healthThresholdPercent;
	}

	@Override
	public Entity[] pickTargets(Entity actor, boolean renderAnimations, Parameters parameters) {
		int healthThreshold = parameters.get(ParameterName.HEALTH_THRESHOLD, this.healthThresholdPercent);
		List<Entity> filtered = new ArrayList<Entity>();
		for(Entity e : targetPicker.pickTargets(actor, renderAnimations, parameters)){
			if(e.isBelowPercentHealth(healthThresholdPercent)){
				filtered.add(e);
			}
		}
		return filtered.toArray(new Entity[0]);
	}

}
