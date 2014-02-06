package game.actions;


import game.objects.Entity;

import java.util.List;



public class CompositeAction implements Action{
	
	private Action[] actions;

	public CompositeAction(Action[] actions){
		this.actions = actions;
	}

	/**
	 * Execute all actions. Return true if some of them succeeded.
	 */
	@Override
	public boolean execute(Entity actor, Parameters context) {
		boolean someSucceeded = false;
		for(Action action : actions){
			
			boolean success = actor.performActionWithoutNotifyingListeners(action, context);
			if(success){
				someSucceeded = true;
			}
		}
		return someSucceeded;
	}

}
