package game;

import game.actions.Parameters;
import game.actions.effects.Effect;
import game.objects.Entity;

import org.newdawn.slick.Color;

public class RangedActionAttack extends RangedAttack {

	private Effect effect;

	public RangedActionAttack(int baseDamage, int randomDamage, int cooldown,
			double range, int projectileSize, Color projectileColor,
			double projectileSpeed, Effect effect, SoundWrapper sound) {
		super(baseDamage, randomDamage, cooldown, range, projectileSize,
				projectileColor, projectileSpeed, sound);
		this.effect = effect;
	}

	public RangedActionAttack(int baseDamage, int randomDamage, int cooldown,
			double range, int projectileSize, Color projectileColor,
			int projectileSpeed, Effect effect) {
		this(baseDamage, randomDamage, cooldown, range, projectileSize,projectileColor, projectileSpeed, effect, null);
	}

	@Override
	public void attackTarget(Entity attacker, Entity target,double damageMultiplier) {
		super.attackTarget(attacker, target, damageMultiplier);
		effect.execute(attacker, target, new Parameters());
	}

	/**
	 * Do not call directly
	 */
	protected RangedActionAttack createCopy() {
		return new RangedActionAttack(baseDamage, randomDamage, cooldown,
				range, projectileSize, projectileColor, projectileSpeed,
				effect, sound);
	}

}
