package game;

import game.objects.Hero;
import game.objects.HeroData.AbilityPair;
import game.objects.HeroInfo;
import game.objects.HeroStat;
import game.objects.NeutralUnit;
import game.objects.Projectile;
import game.objects.Tower;
import game.objects.VisibleObject;
import game.objects.VisualEffect;
import game.objects.enemies.Enemy;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import messages.HUDMessage;
import messages.IntArrayMessageData;
import messages.IntArraysMessageData;
import messages.IntMessageData;
import messages.Message;
import messages.MessageListener;
import messages.MessageType;
import messages.UserInputMessage;
import multiplayer.Server;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import rendering.HUD;
import rendering.HUD_keyChars;
import applicationSpecific.AbilityType;
import applicationSpecific.HeroType;
import applicationSpecific.ItemType;
import applicationSpecific.TowerType;

public class ServerGamePlayState extends GamePlayState implements MessageListener{
	
	private static HUD hud;
	
	private static final Point HERO_SPAWN = new Point(25, 17);// new
	// Point(20,20);

	private static WaveHandler waves;
	
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
		HeroType heroType = HeroType.HERO;

		HUD_keyChars keyChars = new HUD_keyChars(PlayerInputHandler.getStatChar(HeroStat.STRENGTH), PlayerInputHandler.getStatChar(HeroStat.DEXTERITY),
				PlayerInputHandler.getStatChar(HeroStat.INTELLIGENCE), PlayerInputHandler.getAbilityChars(), PlayerInputHandler.getItemChars());
		hud = new HUD(container, keyChars, Player.MAX_LIFE, HeroInfo.MAX_ABILITIES, HeroInfo.MAX_USABLE_ITEMS);
		
		// This class acts as an additional listener that forwards important messages to the clients!
		HeroInfo.INSTANCE.addListener(this);
		HeroInfo.INSTANCE.addListener(hud);
		HeroInfo.INSTANCE.setup(this, heroType, HERO_SPAWN, hud);

		Player.INSTANCE.addListener(this);
		//Player.INSTANCE.addListener(hud);
		Player.INSTANCE.setup(this);

//		hud.addInputListener(this);
//		hud.addInputListener(HeroInfo.INSTANCE);
//		hud.addInputListener(Player.INSTANCE);
//		hud.addInputListener(OfflinePlayerInputHandler.INSTANCE);
		//--------------------------------------------------------------------------------------------
		

		SoundHandler.mute(true); // TODO
		// Sounds.musicVolume = 0;
		SoundHandler.playNextMusic();

		visibleObjects = new ArrayList<VisibleObject>();
		objectsToBeAdded = new ArrayList<VisibleObject>();
		setupMerchant();
		merchant.addMovementListener(HeroInfo.INSTANCE);
		
		GamePlayStateInstance.INSTANCE = this;

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
			SoundHandler.update(delta);
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

	public void addTower(Tower tower, TowerType towerType) {
		visibleObjects.add(tower);
		Player.INSTANCE.notifyTowerWasAdded(tower);
//		server.sendMessageToClients(new Message(MessageType.TOWER_WAS_ADDED, new IntArrayMessageData(towerType.ordinal(), tower.getLocation().x, tower.getLocation().y)));
		tower.notifyBirth();
	}


	public void addEnemy(Enemy enemy) {
		objectsToBeAdded.add(enemy);
//		AddVisualEffectData messageData = new AddVisualEffectData(id, pixelX, pixelY, horPixelsPerSec, verPixelsPerSec, animationId)
//		server.sendMessageToClients(new Message(MessageType.ADD_VISUAL_EFFECT, new AddVisualEffectData(id, pixelX, pixelY, horPixelsPerSec, verPixelsPerSec, animationId)))
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


	public void setWaves(WaveHandler waves) {
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
		EntityHandler.removeDeadEnemies();
		Player.INSTANCE.removeDeadTowers();
	}

	private void addVisibleObjects() {
		for (VisibleObject object : objectsToBeAdded) {
			visibleObjects.add(object);
			if (object instanceof Enemy) {
				EntityHandler.notifyEnemyWasAdded((Enemy) object);
				((Enemy) object).notifyBirth();
			}
		}
		objectsToBeAdded.clear();
	}


	@Override
	public void messageReceived(Message message) {
		System.out.println("serverGPS received: " + message);
		if(message.type instanceof HUDMessage){
			hud.messageReceived(message);
			server.sendMessageToClients(message);
		}else if(message.type instanceof UserInputMessage){
			
		}
	}
	
	public void messageReceivedFromClient(Message message){
		switch((UserInputMessage)message.type){
		
		case CLIENT_PRESSED_KEYS:
			IntArraysMessageData d1 = (IntArraysMessageData)message.data;
			PlayerInputHandler.handleKeyboardInput(d1.array1, d1.array2, hud);
			break;
		case CLIENT_PRESSED_LEFT_MOUSE:
			IntArrayMessageData d2 = (IntArrayMessageData)message.data;
			PlayerInputHandler.handleLeftMousePressed(d2.array[0], d2.array[1], hud);
			break;
		case CLIENT_PRESSED_RIGHT_MOUSE:
			IntArrayMessageData d3 = (IntArrayMessageData)message.data;
			PlayerInputHandler.handleRightMousePressed(d3.array[0], d3.array[1], hud);
			break;
		case PRESSED_ADD_ABILITY:
			IntMessageData d4 = (IntMessageData)message.data;
			HeroInfo.INSTANCE.addAbility(AbilityType.values()[d4.value]);
			break;
		case PRESSED_BUY_ITEM:
			IntMessageData d5 = (IntMessageData)message.data;
			Player.INSTANCE.pressedBuyItem(ItemType.values()[d5.value]);
			break;
		case PRESSED_REPLACE_ABILITY:
			IntArrayMessageData d6 = (IntArrayMessageData)message.data;
			AbilityType oldAbility = AbilityType.values()[d6.array[0]];
			AbilityType newAbility = AbilityType.values()[d6.array[1]];
			HeroInfo.INSTANCE.replaceAbilityWithNew(oldAbility, newAbility);
			break;
		case PRESSED_UNLOCK_TOWER:
			IntMessageData d7 = (IntMessageData)message.data;
			Player.INSTANCE.pressedUnlockTower(TowerType.values()[d7.value]);
			break;
		case PRESSED_SELECT_TOWER:
			
			break;
		case PRESSED_USE_ABILITY:
//			HeroInfo.INSTANCE.getHero().tryToUseAbility(abilityType);
			break;
		case PRESSED_USE_ITEM:
			break;
		default:
			break;
		};
	}


	@Override
	public void setAllowedToRun(boolean allowedToRun) {
		this.allowedToRun = allowedToRun;
	}



	

}
