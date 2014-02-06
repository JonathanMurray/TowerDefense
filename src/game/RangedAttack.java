package game;

import game.objects.Entity;
import game.objects.Projectile;

import org.newdawn.slick.Color;

public class RangedAttack extends Attack {

	protected double range;
	protected int projectileSize;
	protected Color projectileColor;
	protected double projectileSpeed;

	/**
	 * Do not call directly
	 */
	protected RangedAttack createCopy() {
		return new RangedAttack(baseDamage, randomDamage, cooldown, range,
				projectileSize, projectileColor, projectileSpeed, sound);
	}

	public RangedAttack(int baseDamage, int randomDamage, int cooldown,
			double range, int projectileSize, Color projectileColor,
			double projectileSpeed, SoundWrapper sound) {
		super(baseDamage, randomDamage, cooldown, sound);
		this.range = range;
		this.projectileSize = projectileSize;
		this.projectileColor = projectileColor;
		this.projectileSpeed = projectileSpeed;
	}

	public RangedAttack(int baseDamage, int randomDamage, int cooldown,
			int range, int projectileSize, Color projectileColor,
			int projectileSpeed) {
		this(baseDamage, randomDamage, cooldown, range, projectileSize,
				projectileColor, projectileSpeed, null);
	}

	@Override
	public double getRange() {
		return range;
	}

	public void changeRange(int amount) {
		range += amount;
		if (range <= 0) {
			throw new IllegalStateException("negative range");
		}
	}

	@Override
	public void attackTarget(Entity attacker, Entity target,
			double damageMultiplier) {
		super.attackTarget(attacker, target, damageMultiplier);
		Projectile p = new Projectile(attacker.getPixelCenterLocation(),
				target.getPixelCenterLocation(), projectileSpeed,
				projectileSize, projectileColor);
		GamePlayState.addProjectile(p);
	}

}
