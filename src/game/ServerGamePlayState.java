package game;

import game.objects.Hero;
import game.objects.HeroData.AbilityPair;
import game.objects.HeroInfo;
import game.objects.HeroStat;
import game.objects.NeutralUnit;
import game.objects.Projectile;
import game.objects.SuperTower;
import game.objects.Tower;
import game.objects.VisibleObject;
import game.objects.VisualEffect;
import game.objects.enemies.Enemy;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import multiplayer.IdMessageData;
import multiplayer.Message;
import multiplayer.MessageType;
import multiplayer.Server;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import rendering.HUD;
import rendering.HUD_InputListener;
import rendering.HUD_keyChars;
import applicationSpecific.AbilityType;
import applicationSpecific.HeroType;
import applicationSpecific.ItemType;
import applicationSpecific.TowerType;

public class ServerGamePlayState extends GamePlayState implements PlayerListener, HeroInfoListener, HUD_InputListener{
	
	private static HUD hud;
	
	private static final Point HERO_SPAWN = new Point(25, 17);// new
	// Point(20,20);

	private static Waves waves;
	
	private static ArrayList<VisibleObject> visibleObjects;
	private static ArrayList<VisibleObject> objectsToBeAdded;
	
	private static NeutralUnit merchant;
	
	private Server server;
	
	public ServerGamePlayState(Server server){
		this.server = server;
	}


	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		System.out.println("Entering servergamestate...  (time = " + (System.currentTimeMillis() % 1000000) + ")");
		System.out.println("ServerGamePlatyState.init");
		HeroType heroType = HeroType.HERO;

		HUD_keyChars keyChars = new HUD_keyChars(PlayerInputHandler.getStatChar(HeroStat.STRENGTH), PlayerInputHandler.getStatChar(HeroStat.DEXTERITY),
				PlayerInputHandler.getStatChar(HeroStat.INTELLIGENCE), PlayerInputHandler.getAbilityChars(), PlayerInputHandler.getItemChars());
		hud = new HUD(container, keyChars, Player.INSTANCE.getMaxLife(), HeroInfo.MAX_ABILITIES, HeroInfo.MAX_USABLE_ITEMS);

		
		// This class acts as an additional listener that forwards important messages to the clients!
		HeroInfo.INSTANCE.addListener(this);
		HeroInfo.INSTANCE.addListener(hud);
		HeroInfo.INSTANCE.setup(this, heroType, HERO_SPAWN, hud);

		Player.INSTANCE.addListener(this);
		Player.INSTANCE.addListener(hud);
		Player.INSTANCE.setup(this);

		hud.addInputListener(this);
		hud.addInputListener(HeroInfo.INSTANCE);
		hud.addInputListener(Player.INSTANCE);
		hud.addInputListener(PlayerInputHandler.INSTANCE);
		//--------------------------------------------------------------------------------------------
		

		Sounds.mute(true); // TODO
		// Sounds.musicVolume = 0;
		Sounds.playNextMusic();

		visibleObjects = new ArrayList<VisibleObject>();
		objectsToBeAdded = new ArrayList<VisibleObject>();
		setupMerchant();
		merchant.addMovementListener(HeroInfo.INSTANCE);

		System.out.println("\n\n");
		System.out.println("gameplaystate init done at " + System.currentTimeMillis());
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		Sounds.update(delta);
		hud.update(container.getInput(), delta);
		waves.handleWaves(delta);
		waves.handleInput(container.getInput());
		updateAndRemoveObjects(delta);
		HeroInfo.INSTANCE.update(delta);
		addVisibleObjects();

		PlayerInputHandler.handleKeyboardInput(container.getInput(), delta, hud);
		if (!hud.isMouseOverHUDElements(container.getInput())) {
			PlayerInputHandler.handleMouseInput(container.getInput(), delta, hud);
		}
		hud.handleInput(container.getInput());

		Player.INSTANCE.handleMouseOverTowerInput(container.getInput(), delta, hud);
		Entities.handleMouseOverEnemyInput(container.getInput(), delta, hud);
	}

	@Override
	public int getID() {
		return OfflineGame.STATE_GAMEPLAY;
	}

	
	public static void notifyHeroRespawned(Hero hero) {
		visibleObjects.add(hero);
	}

	public static void addTower(Tower tower) {
		visibleObjects.add(tower);
		Player.INSTANCE.notifyTowerWasAdded(tower);
		tower.notifyBirth();
	}

	public static void addSuperTower(SuperTower superTower) {
		visibleObjects.add(superTower);
		Player.INSTANCE.notifySuperTowerWasAdded(superTower);
		superTower.notifyBirth();
	}

	public static void addEnemy(Enemy enemy) {
		objectsToBeAdded.add(enemy);
	}

	public static void addProjectile(Projectile projectile) {
		objectsToBeAdded.add(projectile);
	}

	public static void addSpecialEffect(VisualEffect specialEffect) {
		objectsToBeAdded.add(specialEffect);
	}

	static void giveRewardForWave(int waveNumber) {

		WaveReward reward = LoadedData.getWaveReward(waveNumber);
		Player.INSTANCE.gainMoney(reward.money);
		HeroInfo.INSTANCE.giveAbilityReward();
		for (TowerType tower : reward.towers) {
			Player.INSTANCE.addAvailableTower(tower);
		}
		for (ItemType item : reward.getAllRandomItems()) {
			if (!Player.INSTANCE.hasAvailableItem(item)) {
				Player.INSTANCE.addAvailableItem(item);
			}
		}
	}

	private void setupMerchant() {
		Point[] merchantLocations = new Point[] { new Point(27, 17), new Point(28, 17), new Point(29, 17), new Point(27, 18), new Point(28, 18),
				new Point(29, 18), new Point(27, 19), new Point(28, 19), new Point(29, 19) };
		merchant = new NeutralUnit("Merchant", 3000, merchantLocations, ResourceLoader.createNumberEncodedDirectionSpriteSet(true,
				"characters/blacksmith/blacksmith", 3, "png"), 1, 1050, merchantLocations[0]);
		visibleObjects.add(merchant);
	}

	public boolean isHeroAliveAndCloseEnoughToMerchant() {
		return HeroInfo.INSTANCE.isHeroAlive() && merchant.isLocationWithinDistance(HeroInfo.INSTANCE.getHero().getLocation(), 3);
	}

	private void DEBUGSkipNWaves(int waveNumber) {
		for (int i = 0; i < waveNumber; i++) {
			waves.startNextWave();
		}
	}

	// private void DEBUGsetHeroStats(int value) {
	// for (HeroStat stat : HeroStat.values()) {
	// HeroInfo.setStat(stat, value);
	// }
	// }

	public void setWaves(Waves waves) {
		ServerGamePlayState.waves = waves;
	}

	public void offerHeroOneOfAbilities(AbilityPair newAbilities) {
		hud.addAbilityChoiceDialog(new AbilityType[] { newAbilities.firstAbility, newAbilities.secondAbility });
	}
	
	private void updateAndRemoveObjects(int delta) {
		Iterator<VisibleObject> it = visibleObjects.iterator();
		while (it.hasNext()) {
			VisibleObject object = it.next();
			if (object.shouldBeRemovedFromGame()) {
				it.remove();
				continue;
			}
			object.update(delta);
			if (object.shouldBeRemovedFromGame()) {
				it.remove();
			}
		}
		Entities.removeDeadEnemies();
		Player.INSTANCE.removeDeadTowers();
	}

	private void addVisibleObjects() {
		for (VisibleObject object : objectsToBeAdded) {
			visibleObjects.add(object);
			if (object instanceof Enemy) {
				Entities.notifyEnemyWasAdded((Enemy) object);
				((Enemy) object).notifyBirth();
			}
		}
		objectsToBeAdded.clear();
	}


	



	@Override
	public void towerWasAdded(TowerType towerType) {
		server.sendMessageToClients(new Message(MessageType.TOWER_WAS_ADDED, new IdMessageData(towerType.ordinal())));
		hud.towerWasAdded(towerType);
		System.out.println("tower was added: " + towerType);
	}
	



	@Override
	public void towerWasUnlocked(TowerType towerType) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void itemWasAdded(ItemType itemType) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void itemWasRemoved(ItemType itemType) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void moneyWasUpdated(int newAmount) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void playerLifeWasUpdated(int newAmount) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}


	@Override
	public void pressedAddAbility(AbilityType newAbility) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void abilityWasReplacedByNew(AbilityType oldAbility, AbilityType newAbility) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void abilityWasAdded(AbilityType newAbility) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void heroUsedAbility(AbilityType ability, int timeUntilCanUseAgain) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void itemWasReplacedByNew(int oldItemIndex, ItemType newItem) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void itemWasEquipped(ItemType newItem) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void itemWasDropped(int itemIndex) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void heroStatChanged(HeroStat stat, int newValue) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void heroDied(int timeUntilResurrection) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void heroResurrected() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void numStatpointsChanged(int numAvailableStatpoints) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void heroHealthChanged(int newHealth, int maxHealth) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void heroUsedItem(int timeUntilCanUseAgain) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void heroManaChanged(int oldMana, int newMana, int maxMana) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void heroIsNowInRangeOfVendor(boolean isInRange) {
		// TODO Auto-generated method stub
		
	}

}
