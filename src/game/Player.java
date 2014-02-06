package game;

import game.objects.HeroInfo;
import game.objects.SuperTower;
import game.objects.Tower;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import org.newdawn.slick.Input;

import rendering.HUD;
import rendering.HUD_InputListener;

import applicationSpecific.AbilityType;
import applicationSpecific.ItemType;
import applicationSpecific.SuperTowerType;
import applicationSpecific.TowerType;

public class Player implements HUD_InputListener{
	private int money;

	private ArrayList<Tower> towers = new ArrayList<Tower>();
	private SuperTower superTower;

	private ArrayList<TechUpgrade> upgrades = new ArrayList<TechUpgrade>();

	private final int MAX_ITEMS = 6;

	private final TowerType[] INIT_AVAILABLE_TOWERS = new TowerType[] { TowerType.DROWSER};
	private final TowerType[] INIT_UNLOCKED_TOWERS = new TowerType[] { TowerType.DROWSER };

	public final int SUPER_TOWER_BUILD_COST = 250;

	private SoundWrapper LOSE_LIFE_SOUND;

	private int MAX_LIFE = 10;
	private int life;

	private ArrayList<TowerType> availableTowers = new ArrayList<TowerType>();
	private ArrayList<TowerType> unlockedTowers = new ArrayList<TowerType>();

	private ArrayList<ItemType> availableItems = new ArrayList<ItemType>();

	private ArrayList<PlayerListener> listeners = new ArrayList<PlayerListener>();

	public static Player INSTANCE = new Player();
	
	public int getMoney() {
		return money;
	}

	public void gainMoney(int amount) {
		setMoney(money + amount);
	}
	
	private void loseMoney(int amount){
		setMoney(money - amount);
	}
	
	private void setMoney(int amount){
		money = amount;
		for(PlayerListener listener : listeners){
			listener.moneyWasUpdated(money);
		}
	}

	public void handleMouseOverTowerInput(Input input, int delta, HUD hud) {
		for (Tower tower : towers) {
			Point mousePixelLocation = new Point(input.getMouseX(), input.getMouseY());
			if (tower.containsPixelLocation(mousePixelLocation)) {
				hud.towerMouseOver(tower.getPixelCenterLocation(), tower.getAttackPixelRange(), tower.getSpecialPixelRange());
				return;
			}
		}
	}

	public ArrayList<Tower> getTowersWithinRangeOf(int distance, Point location) {
		ArrayList<Tower> towersWithinRange = new ArrayList<Tower>();
		for (Tower tower : towers) {
			if (tower.isLocationWithinDistance(location, distance)) {
				towersWithinRange.add(tower);
			}
		}
		return towersWithinRange;
	}

	/*
	 * public boolean affordsTower(TowerType towerType) { return money >=
	 * towerType.buildCost; }
	 */
	public boolean affordsItem(ItemType potionType) {
		return money >= Game.getItemData(potionType).buyCost;
	}

	void tryToBuildTowerAtLocation(TowerType tower, Point location) {
		System.out.println("Player try to build towr at loc");//TODO
		int buildCost = Game.getTowerData(tower).buildCost;
		if (money >= buildCost && !Map.blockedForTowers(location.x, location.y)) {
			buildTower(location, tower);
			loseMoney(buildCost);
		}
	}

	public boolean canBuildSuperTower() {
		return money >= SUPER_TOWER_BUILD_COST;
	}

	public void tryToBuildSuperTowerAtLocation(SuperTowerType type, Point location) {
		if (!hasSuperTower()) {
			boolean validSuperTowerLocation = !Map.blockedForHero(location.x, location.y) && !Map.blockedForTowers(location.x, location.y);

			if (money >= SUPER_TOWER_BUILD_COST && validSuperTowerLocation) {
				buildSuperTower(location, type);
				loseMoney(SUPER_TOWER_BUILD_COST);
			}
		}
	}

	private void buildSuperTower(Point location, SuperTowerType type) {
		SuperTower superTower = new SuperTower(type, location, upgrades);
		GamePlayState.addSuperTower(superTower);
	}

	boolean hasSuperTower() {
		return superTower != null;
	}

	void addUpgrade(TechUpgrade upgrade) {
		upgrades.add(upgrade);
		for (Tower tower : towers) {
			tower.notifyNewUpgrade(upgrade);
		}
	}

	private void buildTower(Point location, TowerType type) {
		Tower tower = Tower.createTower(type, location, upgrades);
		GamePlayState.addTower(tower);
	}

	void notifyTowerWasAdded(Tower tower) {
		towers.add(tower);
	}

	void notifySuperTowerWasAdded(SuperTower superTower) {
		superTower = superTower;
	}

	void tryToSellTowerAtLocation(int x, int y) {
		for (Tower tower : towers) {
			if (tower.getLocation().equals(new Point(x, y))) {
				sellTower(tower);
				return;
			}
		}
	}

	private void sellTower(Tower tower) {
		gainMoney(tower.getBuildCost() / 2);
		tower.die(false);
		towers.remove(tower);
	}

	void removeDeadTowers() {
		Iterator<Tower> it = towers.iterator();
		while (it.hasNext()) {
			Tower tower = it.next();
			if (tower.shouldBeRemovedFromGame()) {
				it.remove();
			}
		}
	}

	public void pressedUnlockTower(TowerType towerType) {
		int unlockCost = Game.getTowerData(towerType).unlockCost;
		if (money >= unlockCost) {
			loseMoney(unlockCost);
			unlockTower(towerType);
		}
	}



	public void setup() {
		LOSE_LIFE_SOUND = ResourceLoader.createSound("death/femaleScream.wav", 0.5f);
		for (TowerType tower : INIT_AVAILABLE_TOWERS) {
			addAvailableTower(tower);
		}
		for (TowerType tower : INIT_UNLOCKED_TOWERS) {
			unlockTower(tower);
		}
		try {
			addAvailableItem(ItemType.RESTORE);
		} catch (java.lang.ExceptionInInitializerError e) {
			System.out.println(e.getCause());
		}
		gainMoney(35);
		setLife(MAX_LIFE);
	}

	public int getMaxLife() {
		return MAX_LIFE;
	}

	public int getLife() {
		return life;
	}

	public void loseLife(int amount) {
		Sounds.play(LOSE_LIFE_SOUND);
		setLife(life - amount);
		if (life <= 0) {
			Game.loseGame();
		}
	}
	
	private void setLife(int amount){
		life = amount;
		for(PlayerListener listener : listeners){
			listener.playerLifeWasUpdated(life);
		}
	}

	public void addListener(PlayerListener listener) {
		listeners.add(listener);
	}

	public void addAvailableTower(TowerType towerType) {
		availableTowers.add(towerType);
		for (PlayerListener listener : listeners) {
			listener.towerWasAdded(towerType);
		}
	}

	public void unlockAllAvailableTowers() {
		for (TowerType tower : availableTowers) {
			unlockTower(tower);
		}
	}


	public void unlockTower(TowerType towerType) {

		if (!availableTowers.contains(towerType)) {
			throw new IllegalArgumentException("Tower is not available");
		}
		if (unlockedTowers.contains(towerType)) {
			return;
		}
		unlockedTowers.add(towerType);
		for (PlayerListener listener : listeners) {
			listener.towerWasUnlocked(towerType);
		}
	}
	
	public boolean hasAvailableItem(ItemType itemType){
		return availableItems.contains(itemType);
	}

	public void addAvailableItem(ItemType itemType) {
		availableItems.add(itemType);
		ItemType removedItem = null;
		if (availableItems.size() > MAX_ITEMS) {
			removedItem = availableItems.remove(0);
			System.out.println(removedItem + " was removed");
		}
		for (PlayerListener listener : listeners) {
			if (removedItem != null) {
				listener.itemWasRemoved(removedItem);
			}
			listener.itemWasAdded(itemType);
		}
	}

	public void removeAvailableItem(ItemType itemType) {
		availableItems.remove(itemType);
		for (PlayerListener listener : listeners) {
			listener.itemWasRemoved(itemType);
		}
	}

	@Override
	public void pressedBuyItem(ItemType itemType) {
		ItemData itemStats = Game.getItemData(itemType);
		if (HeroInfo.INSTANCE.isHeroAlive()) {
			if (money >= itemStats.buyCost && HeroInfo.INSTANCE.hasSpaceForItem(itemType) && GamePlayState.isHeroAliveAndCloseEnoughToMerchant()) {
				HeroInfo.INSTANCE.equipItem(itemType);
				loseMoney(itemStats.buyCost);
				if (itemStats.isUnique) {
					removeAvailableItem(itemType);
				}
			}
		}
	}

	@Override
	public void towerSelected(TowerType towerType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pressedReplaceAbility(AbilityType oldAbility, AbilityType newAbility) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pressedAddAbility(AbilityType newAbility) {
		// TODO Auto-generated method stub
		
	}

}
