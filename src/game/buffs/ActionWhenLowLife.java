package game.buffs;

import game.EntityHealthListener;
import game.actions.Action;
import game.actions.Parameters;
import game.objects.Entity;

import org.newdawn.slick.Animation;

public class ActionWhenLowLife extends Buff implements EntityHealthListener {

	private int healthThresholdPercent;
	private Action action;
	private int duration;

	public ActionWhenLowLife(int healthThresholdPercent, Action action, String id, Animation animation, int duration) {
		super(id, animation);
		this.healthThresholdPercent = healthThresholdPercent;
		this.action = action;
		this.duration = duration;
	}

	@Override
	public void applyEffectOn(Entity carrier) {
		carrier.addHealthListener(this);
	}

	@Override
	public void revertEffectOn(Entity carrier) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void continuousEffect(Entity carrier, int delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int getTotalDuration() {
		return duration;
	}

	@Override
	public Buff getCopy() {
		return new ActionWhenLowLife(healthThresholdPercent, action, id, getAnimationCopy(), duration);
	}

	@Override
	public void healthChanged(Entity entity, int oldHealth, int newHealth) {
		if(entity.isBelowPercentHealth(healthThresholdPercent)){
			entity.performAction(action);
//			action.execute(entity);
			entity.loseBuff(this);
		}
	}

}
