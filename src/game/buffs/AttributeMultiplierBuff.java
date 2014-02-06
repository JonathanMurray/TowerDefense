package game.buffs;

import game.objects.Entity;
import game.objects.EntityAttribute;
import game.objects.Unit;

import javax.naming.OperationNotSupportedException;


import org.newdawn.slick.Animation;

public class AttributeMultiplierBuff extends Buff{
	private EntityAttribute attribute;
	private double multiplier;
	private int duration;

	public AttributeMultiplierBuff(EntityAttribute attribute, double multiplier, int duration, String id,
			Animation animation) {
		super(id, animation);
		this.attribute = attribute;
		this.multiplier = multiplier;
		this.duration = duration;
	}

	@Override
	public void applyEffectOn(Entity target) {
		assertUnitArgument(target);
		Unit targetUnit = (Unit) target;
		try {
			targetUnit.addAttributeMultiplier(attribute, id
					+ "SPEED_MULTIPLY", multiplier); //TODO
		} catch (OperationNotSupportedException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	@Override
	public void revertEffectOn(Entity target) {
		assertUnitArgument(target);
		Unit targetUnit = (Unit) target;
		try {
			targetUnit.removeAttributeMultiplier(attribute, id
					+ "SPEED_MULTIPLY");//TODO
		} catch (OperationNotSupportedException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private void assertUnitArgument(Entity target) {
		if (!(target instanceof Unit)) {
			throw new IllegalArgumentException("Only possible for units");
		}
	}

	@Override
	protected void continuousEffect(Entity target, int delta) {
		/* NOTHING */
	}

	@Override
	protected int getTotalDuration() {
		return duration;
	}

	@Override
	public Buff getCopy() {
		return new AttributeMultiplierBuff(attribute, multiplier, duration, id, getAnimationCopy());
	}
}
