package game;

import game.objects.HeroInfo;
import game.objects.HeroStat;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import messages.ClientEntityMessage;
import messages.ClientReady;
import messages.HUDMessage;
import messages.IntArrayMessageData;
import messages.IntArraysMessageData;
import messages.IntMessageData;
import messages.Message;
import messages.MessageListener;
import messages.UserInputMessage;
import multiplayer.AddEntityMessage;
import multiplayer.Client;
import multiplayer.ModifyEntityMessage;
import multiplayer.SetAutoUpdateSpriteMessage;
import multiplayer.SetIntPropertyMessage;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import rendering.ClientRenderableEntity;
import rendering.HUD;
import rendering.HUD_keyChars;
import rendering.RenderableEntity;

public class ClientGame extends BasicGame implements  MessageListener {

	java.util.Map<Integer, RenderableEntity> renderableEntities = new HashMap<>();
	HashMap<Integer, DirectionSpriteSet> spriteSets = new HashMap<>();
	
	private boolean heroAndPlayerSetupDone = false;

	boolean isInitialized = false;
	boolean heroIsAlive = true; //TODO assumes hero is alive from beginning, bad?
	
	private Client client;
	private boolean isHeroInRangeOfVendor;
	
	private static HUD hud;

	public ClientGame(Client client) {
		
		super("client game");
		this.client = client;
	}

	public void addRenderableEntity(int id, double pixelX, double pixelY, int spriteSetId, Direction direction, int percentHealth, int percentMana, String name) {
		DirectionSpriteSet spriteSet = spriteSets.get(spriteSetId).getCopy();
		renderableEntities.put(id, new ClientRenderableEntity(pixelX, pixelY, spriteSet, direction, percentHealth, percentMana, name));
		System.out.println("Added entity...");
	}
	
	public void removeEntity(int id){
		renderableEntities.remove(id);
	}

	public void setRenderableEntityPhysics(int id, Point pixelLocation, Point movementSpeed) {
		renderableEntities.get(id).setLocationAndSpeed(pixelLocation, movementSpeed);
	}


	@Override
	public void init(GameContainer container) throws SlickException {
//		spriteSets.put(1, ResourceLoader.createScaledAnimation(true, 200, 200, "stun.png"));
		
		System.out.println("ClientGame.init()");
		HUD_keyChars keyChars = new HUD_keyChars(PlayerInputHandler.getStatChar(HeroStat.STRENGTH),
				PlayerInputHandler.getStatChar(HeroStat.DEXTERITY), PlayerInputHandler.getStatChar(HeroStat.INTELLIGENCE),
				PlayerInputHandler.getAbilityChars(), PlayerInputHandler.getItemChars());
		hud = new HUD(container, keyChars, Player.MAX_LIFE, HeroInfo.MAX_ABILITIES, HeroInfo.MAX_USABLE_ITEMS);
		
		SoundHandler.loadMusic();//This is done in splash screen state for offlinemode.
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
		for (RenderableEntity visualEffect : renderableEntities.values()) {
			visualEffect.update(delta);
		}
		
		if (container.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
			container.exit();
		}
		
		boolean finishedInitNow = false;
		if(heroAndPlayerSetupDone && !isInitialized){
			hud.addInputListener(this);
			SoundHandler.mute(true); // TODO
			SoundHandler.playNextMusic();

			finishedInitNow = true;
		}

		SoundHandler.update(delta);
		hud.update(container.getInput(), delta);

		handleKeyboardInput(container.getInput(), delta, hud);
		if (!hud.isMouseOverHUDElements(container.getInput())) {
			handleMouseInput(container.getInput(), delta, hud);
		}
		hud.handleInput(container.getInput());

		Player.INSTANCE.handleMouseOverTowerInput(container.getInput(), delta, hud);
		EntityHandler.handleMouseOverEnemyInput(container.getInput(), delta, hud);
		
		if(finishedInitNow){
			System.out.println("SEND MESSAGE TO SERVER CLIENT READY");
			client.sendMessageToServer(new Message(ClientReady.CLIENT_READY, null));
			isInitialized = true;
			System.out.println("clientGame now isinitalized = true");
		}
	}
	
	private void handleKeyboardInput(Input input, int delta, HUD hud){
		List<Integer> keysDownList = new ArrayList<Integer>();
		List<Integer> pressedKeysList = new ArrayList<Integer>();
		for (int key : PlayerInputHandler.ALL_KEYS) {
			if(input.isKeyDown(key)){
				keysDownList.add(key);
			}
			if(input.isKeyPressed(key)){
				pressedKeysList.add(key);
			}
		}
		int[] keysDown = CollectionsUtil.toArray(keysDownList);
		int[] pressedKeys = CollectionsUtil.toArray(pressedKeysList);
		client.sendMessageToServer(new Message(UserInputMessage.CLIENT_PRESSED_KEYS, new IntArraysMessageData(keysDown, pressedKeys)));
	}

	private void handleMouseInput(Input input, int delta, HUD hud){
		if(input.isMousePressed(0)){
			client.sendMessageToServer(new Message(UserInputMessage.CLIENT_PRESSED_LEFT_MOUSE, new IntArrayMessageData(input.getMouseX(), input.getMouseY())));
		}
		if(input.isMousePressed(1)){
			client.sendMessageToServer(new Message(UserInputMessage.CLIENT_PRESSED_RIGHT_MOUSE, new IntArrayMessageData(input.getMouseX(), input.getMouseY())));
		}
	}

	
	
	public void notifyHeroAndPlaySetupDoneOnServer(){
		System.out.println("Cliengame. notifyheroandplaysetupdoneonserver");
		heroAndPlayerSetupDone = true;
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {

		Map.instance().render(g);
		hud.render(container, g);
		
		for (RenderableEntity entity : renderableEntities.values()) {
			entity.render(g);
		}
		for (RenderableEntity entity : renderableEntities.values()) {
			entity.renderExtraVisuals(g);
		}
	}


	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

		
	}

	public boolean isHeroAliveAndCloseEnoughToMerchant() {
		return heroIsAlive && isHeroInRangeOfVendor;
	}
	
	@Override
	public void messageReceived(Message message){
		System.out.println("ClientGame received:  " + message);
		if(message.type instanceof HUDMessage){
			hud.messageReceived(message);
		}else if(message.type instanceof ClientEntityMessage){
			switch((ClientEntityMessage)message.type){
			case ADD_ENTITY:
				AddEntityMessage d1 = (AddEntityMessage)message.data;
				addRenderableEntity(d1.id, d1.pixelX, d1.pixelY, d1.spriteSetId, Direction.DOWN, d1.percentHealth, d1.percentMana, d1.name);
				break;
			case ENTITY_SET_AUTO_UPDATE_SPRITE:
				SetAutoUpdateSpriteMessage d2 = (SetAutoUpdateSpriteMessage)message.data;
				renderableEntities.get(d2.id).setAutoUpdateSprite(d2.setAuto);
				break;
			case ENTITY_SET_DIRECTION:
				SetIntPropertyMessage d3 = (SetIntPropertyMessage)message.data;
				renderableEntities.get(d3.id).setDirection(Direction.values()[d3.value]);
				break;
			case ENTITY_SET_LOCATION_SPEED:
				ModifyEntityMessage d4 = (ModifyEntityMessage)message.data;
				renderableEntities.get(d4.id).setLocationAndSpeed(new Point(d4.pixelX, d4.pixelY), new Point(d4.horPixelsPerSec, d4.verPixelsPerSec));
				break;
			case ENTITY_SET_PERCENT_HEALTH:
				SetIntPropertyMessage d5 = (SetIntPropertyMessage)message.data;
				renderableEntities.get(d5.id).setPercentHealth(d5.value);
				break;
			case ENTITY_SET_PERCENT_MANA:
				SetIntPropertyMessage d6 = (SetIntPropertyMessage)message.data;
				renderableEntities.get(d6.id).setPercentMana(d6.value);
				break;
			case REMOVE_CLIENT_ENTITY:
				IntMessageData d7 = (IntMessageData)message.data;
				removeEntity(d7.value);
				break;
			}
		}
	}


}
