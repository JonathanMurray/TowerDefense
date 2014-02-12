package game.objects;

import game.EntityHealthListener;
import game.Game;
import game.GamePlayState;
import game.HeroInfoListener;
import game.ItemData;
import game.ResourceLoader;
import game.Sounds;
import game.UnitMovementListener;
import game.objects.HeroData.AbilityPair;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import org.newdawn.slick.Graphics;

import rendering.HUD;
import rendering.HUD_InputListener;
import applicationSpecific.AbilityType;
import applicationSpecific.HeroType;
import applicationSpecific.ItemType;
import applicationSpecific.TowerType;

public class HeroInfo implements HUD_InputListener, EntityHealthListener, UnitMovementListener{

	public final static int MAX_ABILITIES = 5;
	public final static int MAX_USABLE_ITEMS = 5;

	private Hero hero;
	private int timeUntilResurrection;
	private final static int HERO_RESURRECTION_CD = 20000;

	private ArrayList<AbilityType> abilities = new ArrayList<AbilityType>();
//	private ArrayList<ItemType> equippedItems = new ArrayList<ItemType>();
	private Map<ItemType, Integer> equippedItems = new HashMap<ItemType, Integer>();
	private HashMap<HeroStat, Integer> stats = new HashMap<HeroStat, Integer>();

	private int experienceOnCurrentChunk;
	private int statpointsToSpend;

	private ArrayList<HeroInfoListener> listeners = new ArrayList<HeroInfoListener>();
	private ArrayList<HeroInfoListener> listenersToBeRemoved = new ArrayList<HeroInfoListener>();
	
	private AbilityPair[] abilityRewards;
	private int nextAbilityRewardIndex = 0;
	
	public static HeroInfo INSTANCE = new HeroInfo();

	public void setup(HeroType type, Point heroSpawnLocation, EntityAttributeListener... heroAttributeListeners) {
		HeroData data = Game.getHeroData(type);
		hero = new Hero(type, data, heroSpawnLocation, heroAttributeListeners);
		
		initStatsMap();
		setStat(HeroStat.STRENGTH, data.strength);
		setStat(HeroStat.DEXTERITY, data.dexterity);
		setStat(HeroStat.INTELLIGENCE, data.intelligence);
		System.out.println("setStat in heroinfo setup");
		hero.addHealthListener(this);
		hero.addMovementListener(this);
		hero.gainFullHealth();
		hero.gainFullMana();
		hero.notifyBirth();
		for(ItemType startItem : data.startItems){
			equipItem(startItem);
		}
		addAbility(data.startAbilities.firstAbility);
		addAbility(data.startAbilities.secondAbility);
		abilityRewards = data.subsequentAbilities;
	}

	public void update(int delta) {
		if (isHeroAlive()) {
			hero.update(delta);
		} else {
			timeUntilResurrection -= delta;
			if (timeUntilResurrection < 0) {
				hero.gainFullHealth();
				hero.gainFullMana();
				hero.notifyBirth();
				for(HeroInfoListener listener : listeners){
					listener.heroResurrected();
				}
			}
		}
		handleRemoveListeners();
	}

	public void renderHero(Graphics g) {
		if (isHeroAlive()) {
			hero.render(g);
		}
	}
	
	public void renderHeroExtraVisuals(Graphics g){
		if(isHeroAlive()){
			hero.renderExtraVisuals(g);
		}
	}

	public boolean isHeroAlive() {
		return !hero.shouldBeRemovedFromGame();
	}

	void notifyHeroDeath() {
		timeUntilResurrection = HERO_RESURRECTION_CD;
		for(HeroInfoListener listener : listeners){
			listener.heroDied(timeUntilResurrection);
		}
	}
	
	void notifyHeroStunned(boolean stunned){
		
	}

	public Hero getHero() {
		if (hero.shouldBeRemovedFromGame()) {
			throw new IllegalStateException("hero is dead");
		}
		return hero;
	}

	private void initStatsMap() {
		stats.put(HeroStat.STRENGTH, 0);
		stats.put(HeroStat.DEXTERITY, 0);
		stats.put(HeroStat.INTELLIGENCE, 0);
	}
	
	private void handleRemoveListeners(){
		for(HeroInfoListener listener : listenersToBeRemoved){
			listeners.remove(listener);
		}
		listenersToBeRemoved.clear();
	}


	public void addListener(HeroInfoListener listener) {
		listeners.add(listener);
		listenersToBeRemoved.remove(listener);
	}
	
	public void removeListener(HeroInfoListener listener){
		listenersToBeRemoved.add(listener);
	}

	public void equipItem(ItemType newItem) {
		if(equippedItems.containsKey(newItem)){
			equippedItems.put(newItem, equippedItems.get(newItem) + 1);
		}else{
			equippedItems.put(newItem, 1);
		}
		
		Game.getItemData(newItem).wasEquipped(hero);
		for (HeroInfoListener listener : listeners) {
			listener.itemWasEquipped(newItem);
		}
		if (equippedItems.size() > MAX_USABLE_ITEMS) {
			throw new IllegalStateException("Too many abilities");
		}
	}
	
	public void dropItem(ItemType itemType){
		int itemIndex = HUD.instance().getNumberOfItem(itemType) - 1;
		dropItemWithIndex(itemIndex);
	}

	public void dropItemWithIndex(int itemIndex){
		ItemType itemType = HUD.instance().getItemWithNumber(itemIndex + 1);
		equippedItems.put(itemType, equippedItems.get(itemType) - 1);
		if(equippedItems.get(itemType) == 0){
			equippedItems.remove(itemType);
		}
		Game.getItemData(itemType).wasDropped(hero);
		for (HeroInfoListener listener : listeners) {
			listener.itemWasDropped(itemIndex);
		}
	}

	/*
	 * public void replaceItemWithNew(int oldItemIndex,
	 * EquippableItemType newItem){ equippedItems.remove(oldItemIndex);
	 * equippedItems.add(newItem); for(HeroItemListener listener :
	 * itemListeners){ listener.itemWasReplacedByNew(oldItemIndex, newItem); } }
	 */

	public boolean hasSpaceForItem(ItemType item) {
		return equippedItems.size() < MAX_USABLE_ITEMS || equippedItems.containsKey(item);
	}
	


//	boolean hasMaxNumberOfAbilities() {
//		return abilities.size() == MAX_ABILITIES;
//	}
	
	public void giveAbilityReward(){
		if(nextAbilityRewardIndex < abilityRewards.length){
			GamePlayState.offerHeroOneOfAbilities(abilityRewards[nextAbilityRewardIndex]);
			nextAbilityRewardIndex ++;
		}
	}
	
	
	public void addAbility(AbilityType newAbility) {
		if (abilities.contains(newAbility)) {
			throw new IllegalArgumentException("Already has that ability");
		}
		abilities.add(newAbility);
		for (HeroInfoListener listener : listeners) {
			listener.abilityWasAdded(newAbility);
		}
		if (abilities.size() > MAX_ABILITIES) {
			throw new IllegalStateException("Too many abilities");
		}
	}

	public void replaceAbilityWithNew(AbilityType oldAbility,AbilityType newAbility) {
		if (abilities.contains(newAbility)) {
			throw new IllegalArgumentException("Already has that ability");
		}
		abilities.remove(oldAbility);
		abilities.add(newAbility);
		for (HeroInfoListener listener : listeners) {
			listener.abilityWasReplacedByNew(oldAbility, newAbility);
		}
	}

	public void gainExperience(int amount) {
		experienceOnCurrentChunk += amount;
		int chunksOf10 = experienceOnCurrentChunk / 10;
		experienceOnCurrentChunk = experienceOnCurrentChunk % 10;
		if (chunksOf10 > 0) {
			Sounds.play(ResourceLoader.createSound("powerup.wav", 2));
		}
		statpointsToSpend += chunksOf10;
		for (HeroInfoListener listener : listeners) {
			listener.numStatpointsChanged(statpointsToSpend);
		}
	}
	
	public void trySpendStatpointOn(HeroStat stat) {
		if(statpointsToSpend > 0){
			spendPointOnStat(stat);
		}
	}

	public void addToStat(HeroStat stat, int changeAmount) {
		setStat(stat, stats.get(stat) + changeAmount);
	}

	public void setStat(HeroStat stat, int newValue) {
		if (newValue < 1) {
			throw new IllegalArgumentException("trying to set " + stat + " to " + newValue);
		}
		try {
			stat.notifyChangedStat(hero, stats.get(stat), newValue);
		} catch (OperationNotSupportedException e) {
			e.printStackTrace();
			System.exit(0);
		}
		stats.put(stat, newValue);
		for(HeroInfoListener listener : listeners){
			listener.heroStatChanged(stat, newValue);
		}
	}

	public void spendPointOnStat(HeroStat stat) {
		if (statpointsToSpend <= 0) {
			throw new IllegalStateException();
		}
		statpointsToSpend--;
		addToStat(stat, 1);
		for (HeroInfoListener listener : listeners) {
			listener.numStatpointsChanged(statpointsToSpend);
		}
	}

	@Override
	public void pressedBuyItem(ItemType itemType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void towerSelected(TowerType towerType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pressedUnlockTower(TowerType towerType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pressedReplaceAbility(AbilityType oldAbility, AbilityType newAbility) {
		replaceAbilityWithNew(oldAbility, newAbility);
	}

	void notifyHeroUsedItem(ItemType item, int timeUntilCanUseAgain) {
		
		
		for(HeroInfoListener listener : listeners){
			listener.heroUsedItem(timeUntilCanUseAgain);
		}
		dropItem(item);
	}

	@Override
	public void healthChanged(Entity entity, int oldHealth, int newHealth) {
		if(entity != hero){
			throw new IllegalStateException("Why listen to other entities?");
		}
		for(HeroInfoListener listener : listeners){
			listener.heroHealthChanged(newHealth, hero.maxHealth);
		}
	}

	void notifyHeroUsedAbility(AbilityType abilityType) {
		for(HeroInfoListener listener : listeners){
			listener.heroUsedAbility(abilityType, Game.getAbilityData(abilityType).cooldown);
		}
	}

	void notifyHeroManaChanged(int oldMana, int mana, boolean dueToUsedAbility, int maxMana) {
		for(HeroInfoListener listener : listeners){
			listener.heroManaChanged(oldMana, mana, maxMana);
		}
	}

	@Override
	public void pressedAddAbility(AbilityType newAbility) {
		addAbility(newAbility);
	}

	@Override
	public void newLocation(int x, int y) {
		boolean inRangeOfVendor = GamePlayState.isHeroAliveAndCloseEnoughToMerchant();
		for(HeroInfoListener listener : listeners){
			listener.heroIsNowInRangeOfVendor(inRangeOfVendor);
		}
	}
}
