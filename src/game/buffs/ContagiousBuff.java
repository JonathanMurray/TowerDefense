package game.buffs;

import game.Entities;
import game.objects.Entity;
import game.objects.enemies.Enemy;

import org.newdawn.slick.Animation;

public class ContagiousBuff extends Buff {

	private int duration;
	private Buff actualBuff;
	private int spreadCooldown;
	private int timeSinceSpread;
	private double range;

	public ContagiousBuff(int duration, Buff actualBuff, double range,
			String id, Animation animation) {
		super(id, animation);
		setupVariables(duration, actualBuff, range);
	}

	private void setupVariables(int duration, Buff actualBuff, double range) {
		this.duration = duration;
		this.actualBuff = actualBuff;
		spreadCooldown = 400;
		timeSinceSpread = spreadCooldown;
		this.range = range;
	}

	@Override
	public void applyEffectOn(Entity carrier) {
		carrier.receiveBuff(actualBuff);
	}

	@Override
	public void revertEffectOn(Entity carrier) {
		// carrier.loseBuff(actualBuff);
	}

	@Override
	protected void continuousEffect(Entity carrier, int delta) {
		timeSinceSpread += delta;
		if (timeSinceSpread >= spreadCooldown) {
			timeSinceSpread = 0;
			spread(carrier);
		}
	}

	private void spread(Entity carrier) {
		for (Entity entity : Entities.getEntitiesWithinRange(
				carrier.getLocation(), range, carrier.getTeam())) {
			if (entity != carrier) {
				entity.receiveBuff(actualBuff.getCopy());
			}
		}
	}

	@Override
	protected int getTotalDuration() {
		return duration;
	}

	@Override
	public Buff getCopy() {
		return new ContagiousBuff(duration, actualBuff.getCopy(), range, id, getAnimationCopy());
	}

}
