package game.objects;

import javax.naming.OperationNotSupportedException;

public enum HeroStat {
	
	
	
	STRENGTH("Strength", 
			"STR",
			"Increases max health, health regeneration and armor. \n" +
			"Max health = base x " + (1+HEALTH_MODIFIER_STR()) + " ^ STR\n" +
			"Regen = base x (1 + " + HEALTH_REGEN_MODIFIER_STR() + " * STR)\n" + 
			"Armor = base + " + ARMOR_PER_STR() + " * STR" ) {
				@Override
				public void notifyChangedStat(Hero hero, int oldValue, int newValue) throws OperationNotSupportedException {
					int prevMaxHealth = hero.maxHealth;
					hero.removeAttributeMultiplier(EntityAttribute.MAX_HEALTH, "strMaxHealthMultiply");
					hero.addAttributeMultiplier(EntityAttribute.MAX_HEALTH,    "strMaxHealthMultiply", Math.pow(1 + HEALTH_MODIFIER_STR(), newValue));
					hero.health = (int) ((double) hero.health * ((double) hero.maxHealth / (double) prevMaxHealth));
					hero.removeAttributeMultiplier(EntityAttribute.HEALTH_REGEN,"strHealthRegenMultiply");
					hero.addAttributeMultiplier(EntityAttribute.HEALTH_REGEN,   "strHealthRegenMultiply", 1 + newValue	* HEALTH_REGEN_MODIFIER_STR());
					hero.changeArmor((newValue - oldValue) * ARMOR_PER_STR());
				}
			}, 
			
	DEXTERITY(
			"Dexterity", 
			"DEX", 
			"Increases damage and movement speed. \n" +
			"Dmg = base x (1 + " + DAMAGE_MODIFIER_DEX() + " * DEX\n" + 
			"Speed = base x (1 + " + SPEED_MODIFIER_DEX() + " * DEX") {
				@Override
				public void notifyChangedStat(Hero hero, int oldValue, int newValue) throws OperationNotSupportedException {
					double speedMod = 1 + newValue * SPEED_MODIFIER_DEX();
					hero.removeAttributeMultiplier(EntityAttribute.MOVEMENT_SPEED,"dexSpeedMultiply");
					hero.addAttributeMultiplier(EntityAttribute.MOVEMENT_SPEED, "dexSpeedMultiply",speedMod);
					
					double dexDamageMultiplier = 1 + (newValue * DAMAGE_MODIFIER_DEX());
					hero.removeAttributeMultiplier(EntityAttribute.DAMAGE, "dexDamageMultiply");
					hero.addAttributeMultiplier(EntityAttribute.DAMAGE, "dexDamageMultiply", dexDamageMultiplier);
				}
			},
	
	INTELLIGENCE(
			"Intelligence", 
			"INT", 
			"Increases max mana and mana regeneration.\n" +
			"Max mana = base x " + (1 + MANA_MODIFIER_INT()) + " ^ INT\n" +
			"Regen = base x (1 + " + MANA_REGEN_MODIFIER_INT() + " * INT)") {
				@Override
				public void notifyChangedStat(Hero hero, int oldValue, int newValue) throws OperationNotSupportedException {
					int prevMaxMana = hero.maxMana;
					hero.removeAttributeMultiplier(EntityAttribute.MAX_MANA, "intMaxManaMultiply");
					hero.addAttributeMultiplier(EntityAttribute.MAX_MANA,    "intMaxManaMultiply", Math.pow(1 + MANA_MODIFIER_INT(), newValue));
					hero.mana = (int) ((double) hero.mana * ((double) hero.maxMana / (double) prevMaxMana));
					double intManaRegenMultiplier = 1 + (newValue * MANA_REGEN_MODIFIER_INT());
					hero.removeAttributeMultiplier(EntityAttribute.MANA_REGEN,"intManaRegenMultiply");
					hero.addAttributeMultiplier(EntityAttribute.MANA_REGEN, "intManaRegenMultiply", intManaRegenMultiplier);
				}
			};
			
	private final static double HEALTH_MODIFIER_STR(){return 0.18;}
	private final static double HEALTH_REGEN_MODIFIER_STR(){return 0.6;}
	private final static double ARMOR_PER_STR(){return 0.22;}
	private final static double MANA_MODIFIER_INT(){return 0.2;}
	private final static double MANA_REGEN_MODIFIER_INT(){return 0.2;}
	private final static double SPEED_MODIFIER_DEX(){return 0.08;}
	private final static double DAMAGE_MODIFIER_DEX(){return 0.16;}

	public String name;
	public String abbreviation;
	public String tooltip;

	HeroStat(String name, String abbreviation, String tooltip) {
		this.name = name;
		this.abbreviation = abbreviation;
		this.tooltip = tooltip;
	}
	
	abstract public void notifyChangedStat(Hero hero, int oldValue, int newValue) throws OperationNotSupportedException;
}
