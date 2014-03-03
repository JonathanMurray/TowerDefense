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

import messages.Message;
import messages.MessageType;
import multiplayer.Server;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import rendering.HUD;
import rendering.HUD_InputListener;
import rendering.HUD_keyChars;
import applicationSpecific.AbilityType;
import applicationSpecific.HeroType;
import applicationSpecific.ItemType;
import applicationSpecific.TowerType;

public class ServerGamePlayState extends GamePlayState implements MessageListener, HUD_InputListener{
	
	private static HUD hud;
	
	private static final Point HERO_SPAWN = new Point(25, 17);// new
	// Point(20,20);

	private static Waves waves;
	
	private static ArrayList<VisibleObject> visibleObjects;
	private static ArrayList<VisibleObject> objectsToBeAdded;
	
	private static NeutralUnit merchant;
	
	private Server server;

	private boolean allowedToRun;
	
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

		HUD_keyChars keyChars = new HUD_keyChars(OfflinePlayerInputHandler.getStatChar(HeroStat.STRENGTH), OfflinePlayerInputHandler.getStatChar(HeroStat.DEXTERITY),
				OfflinePlayerInputHandler.getStatChar(HeroStat.INTELLIGENCE), OfflinePlayerInputHandler.getAbilityChars(), OfflinePlayerInputHandler.getItemChars());
		hud = new HUD(container, keyChars, Player.MAX_LIFE, HeroInfo.MAX_ABILITIES, HeroInfo.MAX_USABLE_ITEMS);
		server.notifyHUDCreated(hud);
		
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
		hud.addInputListener(OfflinePlayerInputHandler.INSTANCE);
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
		
		server.notifyClientsSetupDone();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if(allowedToRun){
			Sounds.update(delta);
			hud.update(container.getInput(), delta);
			waves.handleWaves(delta);
			waves.handleInput(container.getInput());
			updateAndRemoveObjects(delta);
			HeroInfo.INSTANCE.update(delta);
			addVisibleObjects();
		}
	}

	@Override
	public int getID() {
		return OfflineGame.STATE_GAMEPLAY;
	}
	
	public HUD getHUD(){
		return hud;
	}

	
	public static void notifyHeroRespawned(Hero hero) {
		visibleObjects.add(hero);
	}

	public void addTower(Tower tower) {
		visibleObjects.add(tower);
		Player.INSTANCE.notifyTowerWasAdded(tower);
		tower.notifyBirth();
	}

	public static void addSuperTower(SuperTower superTower) {
		visibleObjects.add(superTower);
		Player.INSTANCE.notifySuperTowerWasAdded(superTower);
		superTower.notifyBirth();
	}

	public void addEnemy(Enemy enemy) {
		objectsToBeAdded.add(enemy);
	}

	public void addProjectile(Projectile projectile) {
		objectsToBeAdded.add(projectile);
	}

	public void addSpecialEffect(VisualEffect specialEffect) {
		objectsToBeAdded.add(specialEffect);
	}

	public void giveRewardForWave(int waveNumber) {

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
	public void messageReceived(Message message) {
		System.out.println("serverGPS received: " + message);
		switch(message.type){
		case TOWER_WAS_ADDED:	
		case TOWER_WAS_UNLOCKED:
		case ITEM_WAS_ADDED:
		case ITEM_WAS_REMOVED:
		case MONEY_WAS_UPDATED:
		case PLAYER_LIFE_WAS_UPDATED:
		case TOWER_WAS_SELECTED:
		case ABILITY_WAS_REPLACED:
		case ABILITY_WAS_ADDED:
		case HERO_USED_ABILITY:
		case HERO_DIED:
		case HERO_IN_RANGE_OF_VENDOR:
		case HERO_REVIVED:
		case HERO_STAT_CHANGED:
		case ITEM_WAS_DROPPED:
		case ITEM_WAS_EQUIPPED:
		case ITEM_WAS_REPLACED:
		case ITEM_WAS_USED:
		case NUM_STATPOINTS_CHANGED:
		case HERO_HEALTH_CHANGED:
		case HERO_MANA_CHANGED:
			hud.messageReceived(message);
			server.sendMessageToClients(message);
			break;
		case ADD_VISUAL_EFFECT:
			break;
		case CLIENT_PRESSED_KEYS:
			break;
		case CLIENT_READY:
			break;

		}
	}


	@Override
	public void setAllowedToRun(boolean allowedToRun) {
		this.allowedToRun = allowedToRun;
	}



	

}
