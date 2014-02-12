package game;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

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

public class ServerGame extends BasicGame{



	private static final Point HERO_SPAWN = new Point(25, 17);// new
																// Point(20,20);

	private static Waves waves;

	private static ArrayList<VisibleObject> visibleObjects;
	private static ArrayList<VisibleObject> objectsToBeAdded;

	private static NeutralUnit merchant;
	private static HUD hud;

	public ServerGame(){
		super("Server game");
	}



	@Override
	public void update(GameContainer container, int delta) throws SlickException {
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
		ServerGame.waves = waves;
	}

	public static void offerHeroOneOfAbilities(AbilityPair newAbilities) {
		hud.addAbilityChoiceDialog(new AbilityType[] { newAbilities.firstAbility, newAbilities.secondAbility });
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
	public void render(GameContainer arg0, Graphics arg1) throws SlickException {
		//NO RENDERING: THIS IS SERVER
	}


	@Override
	public void init(GameContainer container) throws SlickException {
		System.out.println("ServerGame.setup()");
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
	}



}
