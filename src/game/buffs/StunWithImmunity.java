package game.buffs;

import game.objects.Entity;
import game.objects.Unit;

import org.newdawn.slick.Animation;

public class StunWithImmunity extends Buff {

	private int timePassed = 0;
	private boolean hasAppliedImmunity = false;
	private int stunDuration;
	private int postImmunityDuration;

	public StunWithImmunity(String id, int stunDuration,int postImmunityDuration) {
		this(id, stunDuration, postImmunityDuration, null);
	}

	public StunWithImmunity(String id, int stunDuration,
			int postImmunityDuration, Animation animation) {
		super(id, animation);
		this.stunDuration = stunDuration;
		this.postImmunityDuration = postImmunityDuration;
	}

	@Override
	public void applyEffectOn(Entity carrier) {
		((Unit) carrier).stun(stunDuration);
	}

	@Override
	public void revertEffectOn(Entity carrier) {
		((Unit) carrier).removeStunImmunity();
	}

	@Override
	protected void continuousEffect(Entity carrier, int delta) {
		timePassed += delta;
		if (timePassed > stunDuration && !hasAppliedImmunity) {
			((Unit) carrier).addStunImmunity();
			hasAppliedImmunity = true;
		}
	}

	@Override
	protected int getTotalDuration() {
		return stunDuration + postImmunityDuration;
	}

	@Override
	public Buff getCopy() {
		return new StunWithImmunity(id, stunDuration, postImmunityDuration, getAnimationCopy());
	}

}
