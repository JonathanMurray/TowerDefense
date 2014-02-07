package game.objects;

import game.AbilityData;
import game.Game;
import game.HeroInfoListenerExtender;
import game.Sounds;
import game.Game.Team;
import game.HeroInfoListener;
import game.Map;

import java.awt.Dimension;
import java.awt.ItemSelectable;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;

import javax.naming.OperationNotSupportedException;

import org.newdawn.slick.Graphics;

import rendering.RenderUtil;

import applicationSpecific.AbilityType;
import applicationSpecific.HeroType;
import applicationSpecific.ItemType;

public class Hero extends StepperUnit {
	int mana;
	int maxMana;
	int baseMaxMana;
	int baseManaRegenCooldown;
	public int manaRegenCooldown;
	private int timeSinceManaRegen;

	private int baseHealthRegenCooldown;
	public int healthRegenCooldown;
	private int timeSinceHealthRegen;

	private final int ITEM_COOLDOWN = 12000;
	private int timeUntilItemReady = 0;

	private HashMap<AbilityType, Integer> abilitiesWithTimeSinceUse = new HashMap<>();
	
	
	

	HeroType type;

	public Hero(HeroType type, HeroData stats, Point location, EntityAttributeListener... attributeListeners) {
		super(stats.baseHealth, stats.baseArmor, stats.spriteSet,
				stats.idleFrameIndex, stats.baseMovementCooldown, location,
				stats.birthSound, stats.deathSound, attributeListeners);
		baseMaxMana = stats.baseMana;
		maxMana = baseMaxMana;
		notifyListenersAttributeChanged(EntityAttribute.MAX_MANA, maxMana);
		mana = (int) (maxMana * 0.75);
		manaRegenCooldown = stats.manaRegenCooldown;
		baseManaRegenCooldown = manaRegenCooldown;
		notifyListenersAttributeChanged(EntityAttribute.MANA_REGEN, manaRegenCooldown);
		healthRegenCooldown = stats.healthRegenCooldown;
		baseHealthRegenCooldown = healthRegenCooldown;
		notifyListenersAttributeChanged(EntityAttribute.HEALTH_REGEN, healthRegenCooldown);
		
		this.type = type;

		HeroInfo.INSTANCE.addListener(new HeroInfoListenerExtender(){
			@Override
			public void abilityWasReplacedByNew(AbilityType oldAbility,	AbilityType newAbility) {
				abilitiesWithTimeSinceUse.remove(oldAbility);
				AbilityData stats = Game.getAbilityData(newAbility);
				abilitiesWithTimeSinceUse.put(newAbility, stats.cooldown); 
			}

			@Override
			public void abilityWasAdded(AbilityType newAbility) {
				AbilityData stats = Game.getAbilityData(newAbility);
				abilitiesWithTimeSinceUse.put(newAbility, stats.cooldown);
			}
		});
		
	}

	@Override
	public void die(boolean wasKilled) {
		super.die(wasKilled);
		HeroInfo.INSTANCE.notifyHeroDeath();
	}



	public boolean tryToUseItem(ItemType item) {
		if (timeUntilItemReady <= 0) {
			Game.getItemData(item).wasUsed(this);
			timeUntilItemReady = ITEM_COOLDOWN;
			HeroInfo.INSTANCE.notifyHeroUsedItem(item, timeUntilItemReady);
			return true;
		}
		return false;
	}
	
	@Override
	public void stun(int duration){
		super.stun(duration);
		HeroInfo.INSTANCE.notifyHeroStunned(true);
	}
	
	@Override
	void notifyStunFaded(){
		HeroInfo.INSTANCE.notifyHeroStunned(false);
	}

	private boolean canUseAbility(AbilityType ability) {
		return hasEnoughManaForAbility(ability) && isAbilityReady(ability)&& !isStunned();
	}

	private boolean hasEnoughManaForAbility(AbilityType abilityType) {
		AbilityData ability = Game.getAbilityData(abilityType);
		return mana >= ability.manaCost;
	}

	private boolean isAbilityReady(AbilityType abilityType) {
		int timeSinceUse =  getAbilityTimeSinceUse(abilityType);
		return timeSinceUse > Game.getAbilityData(abilityType).cooldown;
	}

//	public int getAbilityPercentRemainingCooldown(AbilityType abilityType) {
//		AbilityData stats = Game.getAbilityData(abilityType);
//		int timeSinceUse =  getAbilityTimeSinceUse(abilityType);
//		return (int) (100 * ((float) (stats.cooldown - timeSinceUse) / (float) stats.cooldown));
//	}

	public void tryToUseAbility(AbilityType abilityType) {
		if (canUseAbility(abilityType)) {
			AbilityData stats = Game.getAbilityData(abilityType);
			performAction(stats.action);
			if(stats.sound != null){
				Sounds.play(stats.sound);
			}
			
			addToMana( - stats.manaCost);
			abilitiesWithTimeSinceUse.put(abilityType, 0);
			HeroInfo.INSTANCE.notifyHeroUsedAbility(abilityType);
		}
	}
	
	private int getAbilityTimeSinceUse(AbilityType abilityType){
		if(abilitiesWithTimeSinceUse.containsKey(abilityType)){
			return abilitiesWithTimeSinceUse.get(abilityType);
		}
		throw new IllegalArgumentException("Hero doesn't have ability: " + abilityType);
	}

	

	public void addToMana(int amount) {
		if (mana + amount > maxMana) {
			setMana(maxMana);
		}else if (mana + amount < 0) {
			setMana(0);
		}else{
			setMana(mana + amount);
		}
	}

	public void gainFullMana() {
		setMana(maxMana);
	}
	
	public void setMana(int mana){
		int oldMana = this.mana;
		if(mana > maxMana || mana < 0){
			throw new IllegalArgumentException();
		}
		this.mana = mana;
		HeroInfo.INSTANCE.notifyHeroManaChanged(oldMana, mana, false, maxMana);
	}

	@Override
	protected void multiplyAttribute(EntityAttribute attribute, double totalMultiplier) {
		switch (attribute) {
		case HEALTH_REGEN:
			healthRegenCooldown = (int) ((double) baseHealthRegenCooldown / totalMultiplier);
			notifyListenersAttributeChanged(attribute, healthRegenCooldown);
			break;
		case MANA_REGEN:
			manaRegenCooldown = (int) ((double) baseManaRegenCooldown / totalMultiplier);
			notifyListenersAttributeChanged(attribute, manaRegenCooldown);
			break;
		case MAX_MANA:
			maxMana = (int) ((double) baseMaxMana * totalMultiplier);
			notifyListenersAttributeChanged(attribute, maxMana);
			break;
		default:
			try {
				super.multiplyAttribute(attribute, totalMultiplier);
			} catch (OperationNotSupportedException e) {
				e.printStackTrace();
				System.exit(0);
			}
			return;
		}
	}
	
//	private void setAttribute(EntityAttribute attribute, double newValue){
//
//		switch (attribute) {
//		case HEALTH_REGEN:
//			healthRegenCooldown = (int) Math.round(newValue);
//			break;
//		case MANA_REGEN:
//			manaRegenCooldown = (int) Math.round(newValue);
//			break;
//		case MAX_MANA:
//			maxMana = (int) Math.round(newValue);
//			break;
//		default:
//			throw new IllegalArgumentException
//		}
//		for(EntityAttributeListener listener : attributeListeners){
//			listener.entityAttributeChanged(this, attribute, newValue);
//		}
//	}

	@Override
	public void update(int delta) {
		super.update(delta);
		handleManaRegen(delta);
		handleHealthRegen(delta);
		handleItemCooldown(delta);
		Iterator<AbilityType> abilityIterator = abilitiesWithTimeSinceUse.keySet().iterator();
		while(abilityIterator.hasNext()){
			AbilityType a = abilityIterator.next();
			abilitiesWithTimeSinceUse.put(a, abilitiesWithTimeSinceUse.get(a) + delta);
		}
	}
	
	
	
	
//	//A ilttle test to ensure looping functionality in update works
//	Can be removed"!
//	public static void main(String[] args) {
//		HashMap<String, Integer> map = new HashMap<>();
//		map.put("A", 0);
//		map.put("B", 10);
//		Iterator<String> it = map.keySet().iterator();
//		while(it.hasNext()){
//			String k = it.next();
//			map.put(k, map.get(k) + 1);
//		}
//		
//		System.out.println(map.get("A"));
//		System.out.println(map.get("B"));
//	}

	private void handleManaRegen(int delta) {
		timeSinceManaRegen += delta;
		if (timeSinceManaRegen > manaRegenCooldown) {
			timeSinceManaRegen -= manaRegenCooldown;
			addToMana(1);
		}
	}

	private void handleHealthRegen(int delta) {
		timeSinceHealthRegen += delta;
		if (timeSinceHealthRegen >= healthRegenCooldown) {
			timeSinceHealthRegen -= healthRegenCooldown;
			gainHealth(1);
		}
	}

	private void handleItemCooldown(int delta) {
		if (timeUntilItemReady > 0) {
			timeUntilItemReady -= delta;
		}
	}

	@Override
	protected void renderStatBars(Graphics g) {
		Point uiTopLeft = new Point(getPixelLocation());
		uiTopLeft.translate(0, -16);
		RenderUtil.renderHealthBar(g, uiTopLeft,new Dimension(Map.getTileWidth(), 6), (double) health / (double) maxHealth, 2);
		uiTopLeft.translate(0, 7);
		RenderUtil.renderManaBar(g, uiTopLeft, new Dimension(Map.getTileWidth(), 6), (double) mana / (double) maxMana, 2);
	}

	@Override
	public Team getTeam() {
		return Team.GOOD;
	}
}
