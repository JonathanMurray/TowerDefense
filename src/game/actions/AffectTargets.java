package game.actions;

import game.actions.effects.Effect;
import game.actions.targetPickers.TargetPicker;
import game.objects.Entity;

public class AffectTargets implements Action{
	
	private TargetPicker targetPicker;
	private Effect effect;

	public AffectTargets(TargetPicker targetPicker, Effect effect){
		this.targetPicker = targetPicker;
		this.effect = effect;
	}

	/**
	 * DO NOT CALL DIRECTLY. INSTEAD CALL ENTITY.PERFORM_ACTION() !! 
	 * Execute effect for all found targets. If effect was successful on some target, return true.
	 */
	@Override
	public boolean execute(Entity actor, Parameters parameters) {
		Entity[] targets = targetPicker.pickTargets(actor, true, parameters);
		boolean someSucceeded = false;
		for(Entity target : targets){
			if(target == null){
				throw new IllegalStateException();
			}
			boolean success = effect.execute(actor, target, parameters);
			if(success){
				someSucceeded = true;
			}
		}
		return someSucceeded;
	}

}
