package game.buffs;

import game.actions.Action;
import game.objects.Entity;
import game.objects.EntityActionListener;

import org.newdawn.slick.Animation;

public class ActionWhenCarrierActs extends Buff implements EntityActionListener{

	private Action triggeredAction;
	private int duration;

	public ActionWhenCarrierActs(String id, Action triggeredAction, Animation animation, int duration) {
		super(id, animation);
		this.triggeredAction = triggeredAction;
		this.duration = duration;
	}

	@Override
	public void applyEffectOn(Entity carrier) {
		carrier.addActionListener(this);
	}

	@Override
	public void revertEffectOn(Entity carrier) {
		carrier.removeActionListener(this);
	}

	@Override
	protected void continuousEffect(Entity carrier, int delta) {
		
	}

	@Override
	protected int getTotalDuration() {
		return duration;
	}

	@Override
	public Buff getCopy() {
		return new ActionWhenCarrierActs(id, triggeredAction, getAnimationCopy(), duration);
	}

	@Override
	public void entityDidAction(Entity actor, Action action) {
		actor.performAction(triggeredAction);
		actor.removeActionListener(this);
		
	}

}
