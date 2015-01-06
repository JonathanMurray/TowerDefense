package game;

import game.objects.Entity;

import java.util.Random;


/**
 * Be careful not to use the same copy of an attack for different attackers.
 * Cooldowns will be messed up!
 */
public class Attack {

	private static Random random;

	protected int baseDamage;
	protected int randomDamage;
	protected int cooldown;
	protected int timeSinceLastAttack;
	protected SoundWrapper sound;

	/**
	 * Use to copy attacks, to ensure that each attacker has his own copy
	 * 
	 * @param source
	 */
	public static Attack getCopy(Attack source) {
		return source.createCopy();
	}

	/**
	 * Do not call directly
	 */
	protected Attack createCopy() {
		return new Attack(baseDamage, randomDamage, cooldown, sound);
	}

	public Attack(int baseDamage, int randomDamage, int cooldown,
			SoundWrapper sound) {
		this.baseDamage = baseDamage;
		this.randomDamage = randomDamage;
		this.cooldown = cooldown;
		timeSinceLastAttack = cooldown;
		this.sound = sound;
	}
	
	public String getMinToMaxDamageString(){
		return "" + baseDamage + "-" + (baseDamage + randomDamage);
	}
	
	public double getAttackCooldownInSeconds(){
		return cooldown/1000.0;
	}
	
	
	
	public int getAverageDPS(){
		return Math.round(((float)baseDamage + (float)randomDamage/2) * 1000 / cooldown);
	}
	

	public Attack(int baseDamage, int randomDamage, int cooldown) {
		this(baseDamage, randomDamage, cooldown, null);
	}

	public void update(int delta) {
		timeSinceLastAttack += delta;
	}

	public double getRange() {
		return PhysicsHandler.getAdjacencyDistance();
	}

	public boolean isReady() {
		return timeSinceLastAttack >= cooldown;
	}

	public boolean attackedRecently() {
		return timeSinceLastAttack < cooldown / 3;
	}

	public void attackTarget(Entity attacker, Entity target,
			double damageMultiplier) {
		if (sound != null) {
			SoundHandler.play(sound);
		}

		random = new Random();
		int damage = baseDamage + random.nextInt(randomDamage);
		damage = (int) ((double) damage * damageMultiplier);
		target.loseHealth(damage);
		timeSinceLastAttack = 0;
	}

}
