package game;

import game.objects.HeroInfo;
import game.objects.HeroStat;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import messages.IntArraysMessageData;
import messages.Message;
import messages.MessageType;
import multiplayer.AddVisualEffectData;
import multiplayer.Client;
import multiplayer.ClientVisualEffect;
import multiplayer.UpdatePhysicsData;

import org.newdawn.slick.Animation;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import applicationSpecific.AbilityType;
import applicationSpecific.ItemType;
import applicationSpecific.TowerType;

import rendering.HUD;
import rendering.HUD_InputListener;
import rendering.HUD_keyChars;

public class ClientGame extends BasicGame implements  MessageListener {

	java.util.Map<Integer, ClientVisualEffect> visualEffects = new HashMap<>();
	HashMap<Integer, Animation> animations = new HashMap<>();
	
	private boolean heroAndPlayerSetupDone = false;

	boolean isInitialized = false;
	boolean heroIsAlive = true; //TODO assumes hero is alive from beginning, bad?
	
	private Client client;
	private boolean isHeroInRangeOfVendor;

	public ClientGame(Client client) {
		
		super("client game");
		this.client = client;
	}

	public void addVisualEffect(int id, Point pixelPosition, int animationId, int speedX, int speedY) {
		visualEffects.put(id, new ClientVisualEffect(pixelPosition, speedX, speedY, animations.get(1)));
		System.out.println("Added visual effect...");
	}
	
	public void removeEntity(int id){
		visualEffects.remove(id);
	}

	public void setVisualEffectPhysics(int id, Point pixelLocation, int speedX, int speedY) {
		visualEffects.get(id).setLocationAndSpeed(pixelLocation, speedX, speedY);
	}


	@Override
	public void init(GameContainer container) throws SlickException {
		animations.put(1, ResourceLoader.createScaledAnimation(true, 200, 200, "stun.png"));
		
		System.out.println("ClientGame.init()");
		HUD_keyChars keyChars = new HUD_keyChars(OfflinePlayerInputHandler.getStatChar(HeroStat.STRENGTH),
				OfflinePlayerInputHandler.getStatChar(HeroStat.DEXTERITY), OfflinePlayerInputHandler.getStatChar(HeroStat.INTELLIGENCE),
				OfflinePlayerInputHandler.getAbilityChars(), OfflinePlayerInputHandler.getItemChars());
		hud = new HUD(container, keyChars, Player.MAX_LIFE, HeroInfo.MAX_ABILITIES, HeroInfo.MAX_USABLE_ITEMS);
		
		Sounds.loadMusic();//This is done in splash screen state for offlinemode.
		new Map(); //This is done in splash screen state for offlinemode.
		System.out.println("loading abilities");
		LoadedData.loadAbilityData();
		System.out.println("loading enemies");
		LoadedData.loadEnemyData();
		System.out.println("loading heroes");
		LoadedData.loadHeroData();
		System.out.println("loading items");
		LoadedData.loadItemData();
		System.out.println("loading towers");
		LoadedData.loadTowerData();
		System.out.println("clientgame init done");
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
//		System.out.println("clientgame update");
		for (ClientVisualEffect visualEffect : visualEffects.values()) {
			visualEffect.update(delta);
		}
		
		if (container.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
			container.exit();
		}
		
		boolean finishedInitNow = false;
		if(heroAndPlayerSetupDone && !isInitialized){
			hud.addInputListener(this);
//			hud.addInputListener(Player.INSTANCE);
//			hud.addInputListener(OfflinePlayerInputHandler.INSTANCE);

			Sounds.mute(true); // TODO
			// Sounds.musicVolume = 0;
			Sounds.playNextMusic();

//			System.out.println("\n\n");
//			System.out.println("clientGame enter done at " + System.currentTimeMillis());

			finishedInitNow = true;
		}

		Sounds.update(delta);
		hud.update(container.getInput(), delta);

		handleKeyboardInput(container.getInput(), delta, hud);
		if (!hud.isMouseOverHUDElements(container.getInput())) {
			handleMouseInput(container.getInput(), delta, hud);
		}
		hud.handleInput(container.getInput());

		Player.INSTANCE.handleMouseOverTowerInput(container.getInput(), delta, hud);
		Entities.handleMouseOverEnemyInput(container.getInput(), delta, hud);
		
		if(finishedInitNow){
			System.out.println("SEND MESSAGE TO SERVER CLIENT READY");
			client.sendMessageToServer(new Message(MessageType.CLIENT_READY, null));
			isInitialized = true;
			System.out.println("clientGame now isinitalized = true");
		}
	}
	
	private void handleKeyboardInput(Input input, int delta, HUD hud){
		List<Integer> keysDownList = new ArrayList<Integer>();
		List<Integer> pressedKeysList = new ArrayList<Integer>();
		for (int key : OfflinePlayerInputHandler.ALL_KEYS) {
			if(input.isKeyDown(key)){
				keysDownList.add(key);
			}
			if(input.isKeyPressed(key)){
				pressedKeysList.add(key);
			}
		}
		int[] keysDown = CollectionsUtil.toArray(keysDownList);
		int[] pressedKeys = CollectionsUtil.toArray(pressedKeysList);
		client.sendMessageToServer(new Message(MessageType.CLIENT_PRESSED_KEYS, new IntArraysMessageData(keysDown, pressedKeys)));
	}
	

	
	
	
	
	
	
	private void handleMouseInput(Input input, int delta, HUD hud){
		
	}

	private static HUD hud;
	
	public void notifyHeroAndPlaySetupDoneOnServer(){
		System.out.println("Cliengame. notifyheroandplaysetupdoneonserver");
		heroAndPlayerSetupDone = true;
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {

		// TODO
		for (ClientVisualEffect visualEffect : visualEffects.values()) {
			visualEffect.render(g);
		}
		// TODO

		Map.instance().render(g);
		hud.render(container, g);
	}


	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

		
	}

	public boolean isHeroAliveAndCloseEnoughToMerchant() {
		return heroIsAlive && isHeroInRangeOfVendor;
	}
	
	@Override
	public void messageReceived(Message message){
		System.out.println("ClientGame received:  " + message);
		switch(message.type){
		
		case ADD_VISUAL_EFFECT:
			AddVisualEffectData d1 = (AddVisualEffectData)message.data;
			addVisualEffect(d1.id, new Point(d1.pixelX, d1.pixelY), d1.animationId, d1.horPixelsPerSec, d1.verPixelsPerSec);
			break;
		case UPDATE_PHYSICS:
			UpdatePhysicsData d2 = (UpdatePhysicsData)message.data;
			setVisualEffectPhysics(d2.id, new Point(d2.pixelX, d2.pixelY), d2.horPixelsPerSec, d2.verPixelsPerSec);
			break;
		
		case PLAYER_AND_HERO_SETUP_DONE:
			notifyHeroAndPlaySetupDoneOnServer();
			break;
			
		case REMOVE_CLIENT_ENTITY:
			int entityId = message.getIntDataValue();
			removeEntity(entityId);
			break;
			
		case PRESSED_ADD_ABILITY:
		case PRESSED_BUY_ITEM:
		case PRESSED_REPLACE_ABILITY:
		case PRESSED_UNLOCK_TOWER:
			client.sendMessageToServer(message);
			break;
		
		case HERO_IN_RANGE_OF_VENDOR:
			isHeroInRangeOfVendor = message.getBoolDataValue();
			hud.messageReceived(message);
			break;
			
		case HERO_REVIVED:
			heroIsAlive = true;
			hud.messageReceived(message);
			break;
			
		case HERO_DIED:
			heroIsAlive = false;
			hud.messageReceived(message);
			break;
		
		case HERO_MANA_CHANGED:
		case MONEY_WAS_UPDATED:
		case PLAYER_LIFE_WAS_UPDATED:
		case HERO_STAT_CHANGED:
		case NUM_STATPOINTS_CHANGED:
		case ITEM_WAS_USED:
		case HERO_HEALTH_CHANGED:
		case HERO_USED_ABILITY:
		case ABILITY_WAS_REPLACED:
		case ABILITY_WAS_ADDED:
		case TOWER_WAS_ADDED:
		case TOWER_WAS_UNLOCKED:
		case ITEM_WAS_ADDED:
		case ITEM_WAS_REMOVED:
		case ITEM_WAS_EQUIPPED:
		case ITEM_WAS_REPLACED:
		case ITEM_WAS_DROPPED:
		case TOWER_WAS_SELECTED:
			hud.messageReceived(message);
			break;
		
		}
	}


}
