package game.objects;

import game.EntityHealthListener;
import game.GamePlayState;
import game.LoadedData;
import game.ResourceLoader;
import game.SoundHandler;
import game.UnitMovementListener;
import game.objects.HeroData.AbilityPair;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import messages.BoolMessageData;
import messages.HUDMessage;
import messages.IntArrayMessageData;
import messages.IntMessageData;
import messages.Message;
import messages.MessageListener;
import messages.MessageType;
import messages.UserInputMessage;

import org.newdawn.slick.Graphics;

import rendering.HUD;
import applicationSpecific.AbilityType;
import applicationSpecific.HeroType;
import applicationSpecific.ItemType;

public class HeroInfo implements EntityHealthListener, UnitMovementListener {

	public final static int MAX_ABILITIES = 5;
	public final static int MAX_USABLE_ITEMS = 5;

	private Hero hero;
	private int timeUntilResurrection;
	private final static int HERO_RESURRECTION_CD = 20000;

	private ArrayList<AbilityType> abilities = new ArrayList<AbilityType>();
	// private ArrayList<ItemType> equippedItems = new ArrayList<ItemType>();
	private Map<ItemType, Integer> equippedItems = new HashMap<ItemType, Integer>();
	private HashMap<HeroStat, Integer> stats = new HashMap<HeroStat, Integer>();

	private int experienceOnCurrentChunk;
	private int statpointsToSpend;

	private ArrayList<MessageListener> listeners = new ArrayList<MessageListener>();
	private ArrayList<MessageListener> listenersToBeRemoved = new ArrayList<MessageListener>();

	private AbilityPair[] abilityRewards;
	private int nextAbilityRewardIndex = 0;
	private GamePlayState gameplayState;

	public static HeroInfo INSTANCE = new HeroInfo();

	public void setup(GamePlayState gameplayState, HeroType type, Point heroSpawnLocation, EntityAttributeListener... heroAttributeListeners) {
		this.gameplayState = gameplayState;
		HeroData data = LoadedData.getHeroData(type);
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
		for (ItemType startItem : data.startItems) {
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
				for (MessageListener listener : listeners) {
					listener.messageReceived(new Message(HUDMessage.HERO_REVIVED));
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

	public void renderHeroExtraVisuals(Graphics g) {
		if (isHeroAlive()) {
			hero.renderExtraVisuals(g);
		}
	}

	public boolean isHeroAlive() {
		return !hero.shouldBeRemovedFromGame();
	}

	void notifyHeroDeath() {
		timeUntilResurrection = HERO_RESURRECTION_CD;
		for (MessageListener listener : listeners) {
			listener.messageReceived(new Message(HUDMessage.HERO_DIED, new IntMessageData(timeUntilResurrection)));
		}
	}

	void notifyHeroStunned(boolean stunned) {

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

	private void handleRemoveListeners() {
		for (MessageListener listener : listenersToBeRemoved) {
			listeners.remove(listener);
		}
		listenersToBeRemoved.clear();
	}

	public void addListener(MessageListener listener) {
		listeners.add(listener);
		listenersToBeRemoved.remove(listener);
	}

	public void removeListener(MessageListener listener) {
		listenersToBeRemoved.add(listener);
	}

	public void equipItem(ItemType newItem) {
		if (equippedItems.containsKey(newItem)) {
			equippedItems.put(newItem, equippedItems.get(newItem) + 1);
		} else {
			equippedItems.put(newItem, 1);
		}

		LoadedData.getItemData(newItem).wasEquipped(hero);
		for (MessageListener listener : listeners) {
			listener.messageReceived(new Message(HUDMessage.ITEM_WAS_EQUIPPED, new IntMessageData(newItem.ordinal())));
		}
		if (equippedItems.size() > MAX_USABLE_ITEMS) {
			throw new IllegalStateException("Too many abilities");
		}
	}

	public void dropItem(ItemType itemType) {
		int itemIndex = HUD.instance().getNumberOfItem(itemType) - 1;
		dropItemWithIndex(itemIndex);
	}

	public void dropItemWithIndex(int itemIndex) {
		ItemType itemType = HUD.instance().getItemWithNumber(itemIndex + 1);
		equippedItems.put(itemType, equippedItems.get(itemType) - 1);
		if (equippedItems.get(itemType) == 0) {
			equippedItems.remove(itemType);
		}
		LoadedData.getItemData(itemType).wasDropped(hero);
		for (MessageListener listener : listeners) {
			// listener.itemWasDropped(itemIndex);
			listener.messageReceived(new Message(HUDMessage.ITEM_WAS_DROPPED, new IntMessageData(itemIndex)));
		}
	}

	/*
	 * public void replaceItemWithNew(int oldItemIndex, EquippableItemType
	 * newItem){ equippedItems.remove(oldItemIndex); equippedItems.add(newItem);
	 * for(HeroItemListener listener : itemListeners){
	 * listener.itemWasReplacedByNew(oldItemIndex, newItem); } }
	 */

	public boolean hasSpaceForItem(ItemType item) {
		return equippedItems.size() < MAX_USABLE_ITEMS || equippedItems.containsKey(item);
	}

	// boolean hasMaxNumberOfAbilities() {
	// return abilities.size() == MAX_ABILITIES;
	// }

	public void giveAbilityReward() {
		if (nextAbilityRewardIndex < abilityRewards.length) {
			gameplayState.offerHeroOneOfAbilities(abilityRewards[nextAbilityRewardIndex]);
			nextAbilityRewardIndex++;
		}
	}

	public void addAbility(AbilityType newAbility) {
		if (abilities.contains(newAbility)) {
			throw new IllegalArgumentException("Already has that ability");
		}
		abilities.add(newAbility);
		for (MessageListener listener : listeners) {
			listener.messageReceived(new Message(HUDMessage.ABILITY_WAS_ADDED, new IntMessageData(newAbility.ordinal())));
		}
		if (abilities.size() > MAX_ABILITIES) {
			throw new IllegalStateException("Too many abilities");
		}
	}

	public void replaceAbilityWithNew(AbilityType oldAbility, AbilityType newAbility) {
		if (abilities.contains(newAbility)) {
			throw new IllegalArgumentException("Already has that ability");
		}
		abilities.remove(oldAbility);
		abilities.add(newAbility);
		for (MessageListener listener : listeners) {
			listener.messageReceived(new Message(HUDMessage.ABILITY_WAS_REPLACED, new IntArrayMessageData(oldAbility.ordinal(), newAbility.ordinal())));
		}
	}

	public void gainExperience(int amount) {
		experienceOnCurrentChunk += amount;
		int chunksOf10 = experienceOnCurrentChunk / 10;
		experienceOnCurrentChunk = experienceOnCurrentChunk % 10;
		if (chunksOf10 > 0) {
			SoundHandler.play(ResourceLoader.createSound("powerup.wav", 2));
		}
		statpointsToSpend += chunksOf10;
		for (MessageListener listener : listeners) {
			listener.messageReceived(new Message(HUDMessage.NUM_STATPOINTS_CHANGED, new IntMessageData(statpointsToSpend)));
		}
	}

	public void trySpendStatpointOn(HeroStat stat) {
		System.out.println("heroinfo.tryspendStatpointon"); //TODO
		if (statpointsToSpend > 0) {
			System.out.println("heroinfo. success tryspend statpointon"); //TODO
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
		for (MessageListener listener : listeners) {
			listener.messageReceived(new Message(HUDMessage.HERO_STAT_CHANGED, new IntArrayMessageData(stat.ordinal(), newValue)));
		}
	}

	public void spendPointOnStat(HeroStat stat) {
		if (statpointsToSpend <= 0) {
			throw new IllegalStateException();
		}
		statpointsToSpend--;
		addToStat(stat, 1);
		for (MessageListener listener : listeners) {
			listener.messageReceived(new Message(HUDMessage.NUM_STATPOINTS_CHANGED, new IntMessageData(statpointsToSpend)));
		}
	}
	
	


	void notifyHeroUsedItem(ItemType item, int timeUntilCanUseAgain) {

		for (MessageListener listener : listeners) {
			listener.messageReceived(new Message(HUDMessage.ITEM_WAS_USED, new IntArrayMessageData(item.ordinal(), timeUntilCanUseAgain)));
		}
		dropItem(item);
	}

	@Override
	public void healthChanged(Entity entity, int oldHealth, int newHealth) {
		if (entity != hero) {
			throw new IllegalStateException("Why listen to other entities?");
		}
		for (MessageListener listener : listeners) {
			listener.messageReceived(new Message(HUDMessage.HERO_HEALTH_CHANGED, new IntArrayMessageData(oldHealth, newHealth)));
		}
	}

	void notifyHeroUsedAbility(AbilityType abilityType) {
		for (MessageListener listener : listeners) {
			listener.messageReceived(new Message(HUDMessage.HERO_USED_ABILITY, new IntArrayMessageData(abilityType.ordinal(), LoadedData
					.getAbilityData(abilityType).cooldown)));
		}

	}

	void notifyHeroManaChanged(int oldMana, int mana, boolean dueToUsedAbility, int maxMana) {
		for (MessageListener listener : listeners) {
			listener.messageReceived(new Message(HUDMessage.HERO_MANA_CHANGED, new IntArrayMessageData(oldMana, mana, maxMana)));
		}
	}

	@Override
	public void newLocation(int x, int y) {
		boolean inRangeOfVendor = gameplayState.isHeroAliveAndCloseEnoughToMerchant();
		for (MessageListener listener : listeners) {
			listener.messageReceived(new Message(HUDMessage.HERO_IN_RANGE_OF_VENDOR, new BoolMessageData(inRangeOfVendor)));
		}
	}

}
