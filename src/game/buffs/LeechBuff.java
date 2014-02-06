package game.buffs;

import game.objects.Entity;
import game.objects.HeroInfo;

import org.newdawn.slick.Animation;

public class LeechBuff extends Buff {

	private int damageCooldown;
	private int timeSinceDamage;
	private int healingCooldown;
	private int timeSinceHealing;
	private int duration;

	public LeechBuff(int damageCooldown, int healingCooldown, int duration,
			String id, Animation animation) {
		super(id, animation);
		this.damageCooldown = damageCooldown;
		this.healingCooldown = healingCooldown;
		this.duration = duration;
	}

	@Override
	public void applyEffectOn(Entity target) {

	}

	@Override
	public void revertEffectOn(Entity target) {

	}

	@Override
	protected void continuousEffect(Entity target, int delta) {
		handleDamage(target, delta);
		handleHealing(delta);
	}

	private void handleDamage(Entity target, int delta) {
		timeSinceDamage += delta;
		if (timeSinceDamage >= damageCooldown) {
			timeSinceDamage -= damageCooldown;
			target.loseHealthIgnoringArmor(1);

		}
	}

	private void handleHealing(int delta) {
		timeSinceHealing += delta;
		if (timeSinceHealing >= healingCooldown) {
			timeSinceHealing -= healingCooldown;
			if (HeroInfo.INSTANCE.isHeroAlive()) {
				HeroInfo.INSTANCE.getHero().gainHealth(1);
			}
		}
	}

	@Override
	protected int getTotalDuration() {
		return duration;
	}

	@Override
	public Buff getCopy() {
		return new LeechBuff(damageCooldown, healingCooldown, duration, id, animation);
	}
}
