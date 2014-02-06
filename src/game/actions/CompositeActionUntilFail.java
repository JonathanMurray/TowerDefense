package game.actions;

import game.objects.Entity;

public class CompositeActionUntilFail implements Action{
	
	private Action[] actions;

	public CompositeActionUntilFail(Action[] actions){
		this.actions = actions;
	}

	
	/**
	 * execute actions until one of them fails. If some failed, return false.
	 * If all succeeded, return true.
	 */
	@Override
	public boolean execute(Entity actor, Parameters context) {
		for(Action action : actions){
			boolean success = actor.performActionWithoutNotifyingListeners(action, context);
			if(!success){
				return false;
			}
		}
		return true;
	}

}
