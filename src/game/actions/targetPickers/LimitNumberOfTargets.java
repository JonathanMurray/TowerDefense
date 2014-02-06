package game.actions.targetPickers;

import game.actions.ParameterName;
import game.actions.Parameters;
import game.objects.Entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class LimitNumberOfTargets implements TargetPicker{
	
	private TargetPicker targetPicker;
	private int maxNumberOfTargets;

	public LimitNumberOfTargets(TargetPicker targetPicker, int maxNumberOfTargets){
		this.targetPicker = targetPicker;
		this.maxNumberOfTargets = maxNumberOfTargets;
	}

	@Override
	public Entity[] pickTargets(Entity actor, boolean renderAnimations, Parameters parameters) {
		int maxNumberOfTargets = parameters.get(ParameterName.MAX_NUMBER_OF_TARGETS, this.maxNumberOfTargets);
		Entity[] targets = targetPicker.pickTargets(actor, renderAnimations, parameters);
		if(maxNumberOfTargets >= targets.length){
			return targets;
		}
		List<Entity> targetsList = Arrays.asList(targets);
		Collections.shuffle(targetsList);
		return targetsList.subList(0, maxNumberOfTargets).toArray(new Entity[0]);
	}

}
