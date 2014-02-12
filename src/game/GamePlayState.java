package game;

import game.buffs.Buff;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;
import game.objects.Hero;
import game.objects.HeroInfo;
import game.objects.HeroStat;
import game.objects.ItemOnGround;
import game.objects.NeutralUnit;
import game.objects.Projectile;
import game.objects.SuperTower;
import game.objects.Tower;
import game.objects.VisibleObject;
import game.objects.VisualEffect;
import game.objects.HeroData.AbilityPair;
import game.objects.enemies.Enemy;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import rendering.HUD;
import rendering.HUD_keyChars;

import applicationSpecific.AbilityType;
import applicationSpecific.HeroType;
import applicationSpecific.ItemType;
import applicationSpecific.TowerType;

public class GamePlayState extends BasicGameState {

	private static double TIME_MULTIPLIER = 1;
	// private static float[] graphicsScales = new float[] {0.125f, 0.25f,
	// 0.375f, 0.4375f,
	// 0.5f, 0.5625f, 0.625f, 0.6875f, 0.75f, 0.8125f, 0.875f, 0.9375f, 1f };
	// private static int scaleIndex = 6;
	// private static final int GRAPHICS_X_TRANSLATE = (int) (((float) 1280 -
	// (MapEditorData.PIXEL_WIDTH * graphicsScales[scaleIndex])) / 2);
	// private static final int GRAPHICS_Y_TRANSLATE = 60;
	private static final Point HERO_SPAWN = new Point(25, 17);// new
																// Point(20,20);

	private static Waves waves;

	private static ArrayList<VisibleObject> visibleObjects;
	private static ArrayList<VisibleObject> objectsToBeAdded;

	private static NeutralUnit merchant;

	private static HUD hud;

	private boolean hasSetup = false;
	private boolean render;

	public GamePlayState(boolean render) {
		this.render = render;
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		if (!hasSetup) {
			setup(container);
			hasSetup = true;
		}
	}

	private void setup(GameContainer container) {
		System.out.println("GamePLayState.setup()");
		HeroType heroType = HeroType.HERO;

		HUD_keyChars keyChars = new HUD_keyChars(PlayerInputHandler.getStatChar(HeroStat.STRENGTH), PlayerInputHandler.getStatChar(HeroStat.DEXTERITY),
				PlayerInputHandler.getStatChar(HeroStat.INTELLIGENCE), PlayerInputHandler.getAbilityChars(), PlayerInputHandler.getItemChars());
		hud = new HUD(container, keyChars, Player.INSTANCE.getMaxLife(), HeroInfo.MAX_ABILITIES, HeroInfo.MAX_USABLE_ITEMS);

		HeroInfo.INSTANCE.addListener(hud);
		HeroInfo.INSTANCE.setup(heroType, HERO_SPAWN, hud);

		Player.INSTANCE.addListener(hud);
		Player.INSTANCE.setup();

		hud.addInputListener(HeroInfo.INSTANCE);
		hud.addInputListener(Player.INSTANCE);
		hud.addInputListener(PlayerInputHandler.INSTANCE);

		Sounds.mute(true); // TODO
		// Sounds.musicVolume = 0;
		Sounds.playNextMusic();

		visibleObjects = new ArrayList<VisibleObject>();
		objectsToBeAdded = new ArrayList<VisibleObject>();
		setupMerchant();
		merchant.addMovementListener(HeroInfo.INSTANCE);

		System.out.println("\n\n");
		System.out.println("gamneplay enter done at " + System.currentTimeMillis());

		// TODO

		// TODO

		// TODO

		// TODO

		// TODO

		// TODO

		// TODO

		// TODO

		// TODO

		// TODO

		// TODO

		// HeroInfo.INSTANCE.addAbility(AbilityType.DEBUG_GOD);;
		// HeroInfo.INSTANCE.addAbility(AbilityType.BREATH);
		// Player.INSTANCE.gainMoney(200);
		// HeroInfo.INSTANCE.replaceAbilityWithNew(AbilityType.PUNCH,
		// AbilityType.LIGHTNING);
		// HeroInfo.INSTANCE.replaceAbilityWithNew(AbilityType.RUN,
		// AbilityType.SPRINT);
		// // HeroInfo.INSTANCE.addAbility(AbilityType.SPRINT);
		// HeroInfo.INSTANCE.addAbility(AbilityType.SHOCK);
		// HeroInfo.INSTANCE.addAbility(AbilityType.IRON);
		//
		// HeroInfo.INSTANCE.setStat(HeroStat.STRENGTH, 24);
		// HeroInfo.INSTANCE.setStat(HeroStat.DEXTERITY, 14);
		// HeroInfo.INSTANCE.setStat(HeroStat.INTELLIGENCE, 14);

		// DEBUGSkipNWaves(5);
		// Player.INSTANCE.gainMoney(100);
		// HeroInfo.INSTANCE.setStat(HeroStat.STRENGTH, 15);
		// HeroInfo.INSTANCE.setStat(HeroStat.INTELLIGENCE, 15);
		// HeroInfo.INSTANCE.setStat(HeroStat.DEXTERITY, 15);
		// HeroInfo.INSTANCE.addAbility(AbilityType.FOCUS);
		// HeroInfo.INSTANCE.replaceAbilityWithNew(AbilityType.RUN,
		// AbilityType.BREATH);

		//
		// DEBUGSkipNWaves(4);
		// // HeroInfo.gainExperience(9);
		// //
		// PlayerInfo.addAvailableItem(ItemType.FROST_BITE);
		// PlayerInfo.addAvailableItem(ItemType.BLOOD_SUCKER);
		// PlayerInfo.addAvailableItem(ItemType.DEMON_HEART);
		// PlayerInfo.addAvailableItem(ItemType.MANA_STONE);
		// //
		// // DEBUGsetHeroStats(22);
		// //
		// // HeroInfo.replaceAbilityWithNew(AbilityType.PUNCH,
		// AbilityType.FURY);
		// HeroInfo.replaceAbilityWithNew(AbilityType.RUN, AbilityType.GENIUS);
		// offerHeroNewAbility(AbilityType.ARMOR);
		// offerHeroNewAbility(AbilityType.VAMPIRE);
		// // offerHeroNewAbility(AbilityType.GENIUS);
		// // offerHeroNewAbility(AbilityType.FURY);
		// PlayerInfo.unlockAllAvailableTowers();
		// Player.INSTANCE.gainMoney(800);

	}

	@Override
	public int getID() {
		return Game.STATE_GAMEPLAY;
	}

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {

	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {

	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {

		if(!render){
			return;
		}
		
		Map.instance().render(g);

		HeroInfo.INSTANCE.renderHero(g);
		renderVisibleObjects(g);
		HeroInfo.INSTANCE.renderHeroExtraVisuals(g);

		if (waves == null) {
			throw new IllegalArgumentException("WAVES IS NULL");// TODO
		} else if (hud == null) {
			throw new IllegalArgumentException("HUD IS NULL");// TODO
		}

		hud.receiveWavesData(waves.getWaveIndex(), waves.getSecondsUntilNextWave(), waves.hasMoreSpawns(), waves.currentWaveHasBeenCleared(),
				waves.wavesHaveBegun(), waves.isFinalWave());
		hud.render(container, g);
		// Map.instance().debugRenderEntitiesCollision(g);//TODO
		DEBUGrenderCurrentHeroBuffs(g);
		// DEBUGrenderAllEntitiesBuffs(g);
	}

	private void DEBUGrenderCurrentHeroBuffs(Graphics g) {
		int i = 0;
		if (HeroInfo.INSTANCE.isHeroAlive()) {
			for (Buff b : HeroInfo.INSTANCE.getHero().buffs) {
				g.setColor(Color.magenta);
				g.drawString(b.toString(), 300, 100 + i * 50);
				i++;
			}
		}
	}

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

		handleSettingsInput(game, container);

		delta = Math.max(1, (int) ((double) delta * TIME_MULTIPLIER)); // TODO

		// container.getInput().setScale(1 / graphicsScales[scaleIndex],
		// 1 / graphicsScales[scaleIndex]);
		// container.getInput().setOffset(-GRAPHICS_X_TRANSLATE,
		// -GRAPHICS_Y_TRANSLATE);

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

	public static void notifyHeroRespawned(Hero hero) {
		visibleObjects.add(hero);
	}

	// public static void addItem(ItemOnGround item) {
	// visibleObjects.add(item);
	// }

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

		WaveReward reward = Game.getWaveReward(waveNumber);
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

	public static boolean isHeroAliveAndCloseEnoughToMerchant() {
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

	static void setWaves(Waves waves) {
		System.out.println("set waves to " + waves);
		GamePlayState.waves = waves;
	}

	public static void offerHeroOneOfAbilities(AbilityPair newAbilities) {
		hud.addAbilityChoiceDialog(new AbilityType[] { newAbilities.firstAbility, newAbilities.secondAbility });
	}

	// public static void offerHeroNewItem(ItemType newItem) { //F�RL�GG
	// TILL HUD
	// if (HeroInfo.hasFreeItemSlots()) {
	// HeroInfo.equipItem(newItem);
	// } else {
	// //TODO G�ra vad?
	// }
	// }

	private void handleSettingsInput(StateBasedGame game, GameContainer container) {
		// handleGraphicsScale(container); //TODO
		handleSpeedModifying(container);

		if (container.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
			container.exit();
		}

		if (container.getInput().isKeyPressed(Input.KEY_M)) {
			Sounds.toggleMute();
		}

		if (container.getInput().isKeyPressed(Input.KEY_U)) {
			DEBUGSkipNWaves(1);
		}

		if (container.getInput().isKeyPressed(Input.KEY_P)) {
			enterPausedState(game, container);
		}
	}

	private void enterPausedState(StateBasedGame game, GameContainer container) {
		Graphics g = container.getGraphics();

		int w = container.getWidth();
		int h = container.getHeight();
		try {
			Image img = new Image(w, h);
			g.copyArea(img, 0, 0);
			PausedState.setImage(img);
			game.enterState(Game.STATE_PAUSED);
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}

	// private void handleGraphicsScale(GameContainer container) {
	// if (container.getInput().isKeyPressed(Input.KEY_ADD)) {
	// scaleIndex = Math.min(scaleIndex + 1, graphicsScales.length - 1);
	// } else if (container.getInput().isKeyPressed(Input.KEY_SUBTRACT)) {
	// scaleIndex = Math.max(scaleIndex - 1, 0);
	// }
	// }

	private void handleSpeedModifying(GameContainer container) {
		if (container.getInput().isKeyPressed(Input.KEY_I)) {
			TIME_MULTIPLIER += 0.5;
			// System.out.println(TIME_MULTIPLIER);
		} else if (container.getInput().isKeyPressed(Input.KEY_O)) {
			if (TIME_MULTIPLIER <= 1.1) {
				TIME_MULTIPLIER = Math.max(TIME_MULTIPLIER - 0.15, 0.15);
			} else {
				TIME_MULTIPLIER -= 0.5;
			}
			// System.out.println(TIME_MULTIPLIER);
		}
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

	private void renderVisibleObjects(Graphics g) {
		for (VisibleObject object : visibleObjects) {
			if (!(object instanceof AnimationBasedVisualEffect)) {
				object.render(g);

			}
		}
		for (VisibleObject object : visibleObjects) {
			if (object instanceof AnimationBasedVisualEffect) {
				object.render(g);
			}
		}
		for (VisibleObject object : visibleObjects) {
			object.renderExtraVisuals(g);
		}
	}

}
