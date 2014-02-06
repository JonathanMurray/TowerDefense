package game.objects;

import game.DirectionSpriteSet;
import game.SoundWrapper;
import applicationSpecific.AbilityType;
import applicationSpecific.ItemType;

public class HeroData {
	public int baseHealth;
	public int healthRegenCooldown;
	public double baseArmor;
	public int baseMana;
	public int manaRegenCooldown;
	public SoundWrapper birthSound;
	public SoundWrapper deathSound;
	public AbilityPair startAbilities;
	public AbilityPair[] subsequentAbilities;
	public ItemType[] startItems;
	public DirectionSpriteSet spriteSet;
	public int idleFrameIndex;
	public int baseMovementCooldown;
	public int strength;
	public int dexterity;
	public int intelligence;

	public HeroData(int maxHealth, int healthRegenCooldown, double armor, int maxMana,
			int manaRegenCooldown, SoundWrapper birthSound,
			SoundWrapper deathSound, AbilityPair startAbilities, AbilityPair[] subsequentAbilities, ItemType[] startItems,
			DirectionSpriteSet spriteSet, int idleFrameIndex,
			int movementCooldown, int strength, int dexterity, int intelligence) {

		this.baseHealth = maxHealth;
		this.healthRegenCooldown = healthRegenCooldown;
		this.baseArmor = armor;
		this.baseMana = maxMana;
		this.manaRegenCooldown = manaRegenCooldown;
		this.birthSound = birthSound;
		this.deathSound = deathSound;
		this.startAbilities = startAbilities;
		this.subsequentAbilities = subsequentAbilities;
		this.startItems = startItems;
		this.spriteSet = spriteSet;
		this.idleFrameIndex = idleFrameIndex;
		this.baseMovementCooldown = movementCooldown;
		this.strength = strength;
		this.dexterity = dexterity;
		this.intelligence = intelligence;
	}
	
	public static class AbilityPair{
		public AbilityType firstAbility;
		public AbilityType secondAbility;
		public AbilityPair(AbilityType firstAbility, AbilityType secondAbility) {
			this.firstAbility = firstAbility;
			this.secondAbility = secondAbility;
		}
	}
}
