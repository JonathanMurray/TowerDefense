package game.buffs;

import game.actions.Action;
import game.objects.Entity;

import org.newdawn.slick.Animation;

public class RepeatActionBuff extends Buff{
	

	private int actionCooldown;
	private int timeSinceAction;
	private int duration;
	private Action action;

	public RepeatActionBuff(Action action, int actionCooldown,
			int duration, String buffId, Animation animation) {
		super(buffId, animation);
		this.action = action;
		this.actionCooldown = actionCooldown;
		timeSinceAction = actionCooldown;
		this.duration = duration;
	}

	@Override
	public void applyEffectOn(Entity carrier) {

	}

	@Override
	public void revertEffectOn(Entity carrier) {

	}

	@Override
	protected void continuousEffect(Entity carrier, int delta) {
		timeSinceAction += delta;
		if (timeSinceAction >= actionCooldown) {
			carrier.performActionWithoutNotifyingListeners(action);
			
			timeSinceAction = 0;
		}
	}


	@Override
	protected int getTotalDuration() {
		return duration;
	}

	@Override
	public Buff getCopy() {
		return new RepeatActionBuff(action, actionCooldown, duration, id, getAnimationCopy());
	}
}
