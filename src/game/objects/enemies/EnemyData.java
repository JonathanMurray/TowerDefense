package game.objects.enemies;

import game.Attack;
import game.DirectionSpriteSet;
import game.SoundWrapper;
import game.buffs.Buff;

public class EnemyData {
	
	String name;
	int maxHealth;
	double armor;
	private Attack attack;
	int aggroRange;
	int movementCooldown;
	DirectionSpriteSet spriteSet;
	int idleFrameIndex;
	SoundWrapper deathSound;
	int moneyRewarded;
	int experienceRewarded;
	Buff behaviourBuff;
	String tinyDescription;
	int maxNumMinions;
	boolean isImmuneToStun;
	boolean isImmuneToSlow;
	double stunDurationMultiplier;

	public EnemyData(String name, int maxHealth, double armor, Attack attack, int aggroRange,
			int movementCooldown, DirectionSpriteSet spriteSet,
			int idleFrameIndex, SoundWrapper deathSound, int moneyRewarded,
			int experienceRewarded, Buff behaviourBuff, 
			String tinyDescription, int maxNumMinions, boolean isImmuneToSlow, boolean isImmuneToStun, double stunDurationMultiplier) {
		this.name = name;
		this.maxHealth = maxHealth;
		this.armor = armor;
		this.spriteSet = spriteSet;
		this.idleFrameIndex = idleFrameIndex;
		this.deathSound = deathSound;
		this.movementCooldown = movementCooldown;
		this.moneyRewarded = moneyRewarded;
		this.attack = attack;
		this.aggroRange = aggroRange;
		this.experienceRewarded = experienceRewarded;
		this.behaviourBuff = behaviourBuff;
		this.tinyDescription = tinyDescription;
		this.maxNumMinions = maxNumMinions;
		this.isImmuneToSlow = isImmuneToSlow;
		this.isImmuneToStun = isImmuneToStun;
		this.stunDurationMultiplier = stunDurationMultiplier;
	}
	
	Attack getAttackCopy() {
		return Attack.getCopy(attack);
	}
}
