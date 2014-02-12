package game;

import game.objects.HeroInfo;
import game.objects.HeroStat;
import java.awt.Point;
import java.util.HashMap;
import multiplayer.ClientVisualEffect;

import org.newdawn.slick.Animation;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import rendering.HUD;
import rendering.HUD_keyChars;

public class ClientGame extends BasicGame {

	java.util.Map<Integer, ClientVisualEffect> visualEffects = new HashMap<>();
	HashMap<Integer, Animation> animations = new HashMap<>();

	boolean isInitialized = false;

	public ClientGame() {
		super("client game");
	}

	public void addVisualEffect(int id, Point pixelPosition, int animationId, int speedX, int speedY) {
		visualEffects.put(id, new ClientVisualEffect(pixelPosition, speedX, speedY, animations.get(1)));
		System.out.println("Added visual effect...");
	}

	public void setVisualEffectPhysics(int id, Point pixelLocation, int speedX, int speedY) {
		visualEffects.get(id).setLocationAndSpeed(pixelLocation, speedX, speedY);
	}

	public boolean isFinishedInit() {
		return isInitialized;
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		animations.put(1, ResourceLoader.createScaledAnimation(true, 200, 200, "stun.png"));
		
		System.out.println("GamePLayState.setup()");
		HUD_keyChars keyChars = new HUD_keyChars(PlayerInputHandler.getStatChar(HeroStat.STRENGTH),
				PlayerInputHandler.getStatChar(HeroStat.DEXTERITY), PlayerInputHandler.getStatChar(HeroStat.INTELLIGENCE),
				PlayerInputHandler.getAbilityChars(), PlayerInputHandler.getItemChars());
		hud = new HUD(container, keyChars, Player.INSTANCE.getMaxLife(), HeroInfo.MAX_ABILITIES, HeroInfo.MAX_USABLE_ITEMS);

		//
		//
		// HeroInfo.INSTANCE.addListener(hud);
		// HeroInfo.INSTANCE.setup(heroType, HERO_SPAWN, hud);

		// Player.INSTANCE.addListener(hud);
		// Player.INSTANCE.setup();

		hud.addInputListener(HeroInfo.INSTANCE);
		hud.addInputListener(Player.INSTANCE);
		hud.addInputListener(PlayerInputHandler.INSTANCE);

		Sounds.mute(true); // TODO
		// Sounds.musicVolume = 0;
		Sounds.playNextMusic();

		// visibleObjects = new ArrayList<VisibleObject>();
		// objectsToBeAdded = new ArrayList<VisibleObject>();
		// setupMerchant();
		// merchant.addMovementListener(HeroInfo.INSTANCE);

		System.out.println("\n\n");
		System.out.println("clientGame enter done at " + System.currentTimeMillis());

		isInitialized = true;
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		for (ClientVisualEffect visualEffect : visualEffects.values()) {
			visualEffect.update(delta);
		}
		
		if (container.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
			container.exit();
		}

		// delta = Math.max(1, (int) ((double) delta * TIME_MULTIPLIER)); //
		// TODO

		// container.getInput().setScale(1 / graphicsScales[scaleIndex],
		// 1 / graphicsScales[scaleIndex]);
		// container.getInput().setOffset(-GRAPHICS_X_TRANSLATE,
		// -GRAPHICS_Y_TRANSLATE);

		Sounds.update(delta);
		hud.update(container.getInput(), delta);
		// waves.handleWaves(delta);
		// waves.handleInput(container.getInput());
		// updateAndRemoveObjects(delta);
		HeroInfo.INSTANCE.update(delta);
		// addVisibleObjects();

		PlayerInputHandler.handleKeyboardInput(container.getInput(), delta, hud);
		if (!hud.isMouseOverHUDElements(container.getInput())) {
			PlayerInputHandler.handleMouseInput(container.getInput(), delta, hud);
		}
		hud.handleInput(container.getInput());

		Player.INSTANCE.handleMouseOverTowerInput(container.getInput(), delta, hud);
		Entities.handleMouseOverEnemyInput(container.getInput(), delta, hud);
	}

	private static HUD hud;

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {

		// TODO
		for (ClientVisualEffect visualEffect : visualEffects.values()) {
			visualEffect.render(g);
		}
		// TODO

		Map.instance().render(g);
		//
		// HeroInfo.INSTANCE.renderHero(g);
		// renderVisibleObjects(g);
		//
		// HeroInfo.INSTANCE.renderHeroExtraVisuals(g);
		//
		// if (waves == null) {
		// throw new IllegalArgumentException("WAVES IS NULL");// TOD
		// } else if (hud == null) {
		// throw new IllegalArgumentException("HUD IS NULL");// TOD
		// }
		//
		// hud.receiveWavesData(waves.getWaveIndex(),
		// waves.getSecondsUntilNextWave(), waves.hasMoreSpawns(),
		// waves.currentWaveHasBeenCleared(),
		// waves.wavesHaveBegun(), waves.isFinalWave());
		hud.render(container, g);
		// Map.instance().debugRenderEntitiesCollision(g);//TOD
		// DEBUGrenderCurrentHeroBuffs(g);
		// DEBUGrenderAllEntitiesBuffs(g);
	}

	// private void DEBUGrenderCurrentHeroBuffs(Graphics g) {
	// int i = 0;
	// if (HeroInfo.INSTANCE.isHeroAlive()) {
	// for (Buff b : HeroInfo.INSTANCE.getHero().buffs) {
	// g.setColor(Color.magenta);
	// g.drawString(b.toString(), 300, 100 + i * 50);
	// i++;
	// }
	// }
	// }

	//
	// private void DEBUGrenderAllEntitiesBuffs(Graphics g){
	// for(VisibleObject obj : visibleObjects){
	// if(obj instanceof Entity){
	// Entity e = (Entity)obj;
	// int i = 0;
	// for(Buff b : e.buffs){
	// g.drawString(b.toString(), e.getPixelLocation().x ,
	// e.getPixelLocation().y - 40 + i);
	// i += 15;
	// }
	// }
	// }
	// }

	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

		
	}

	public static boolean isHeroAliveAndCloseEnoughToMerchant() {
		// return HeroInfo.INSTANCE.isHeroAlive() &&
		// merchant.isLocationWithinDistance(HeroInfo.INSTANCE.getHero().getLocation(),
		// 3);
		return false;
	}

	
	// private void renderVisibleObjects(Graphics g) {
	// for (VisibleObject object : visibleObjects) {
	// if (!(object instanceof AnimationBasedVisualEffect)) {
	// object.render(g);
	//
	// }
	// }
	// for (VisibleObject object : visibleObjects) {
	// if (object instanceof AnimationBasedVisualEffect) {
	// object.render(g);
	// }
	// }
	// for (VisibleObject object : visibleObjects) {
	// object.renderExtraVisuals(g);
	// }
	// }

}
