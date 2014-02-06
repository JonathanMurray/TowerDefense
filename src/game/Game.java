package game;

import game.objects.HeroData;
import game.objects.TowerData;
import game.objects.enemies.EnemyData;

import java.nio.file.Path;
import java.util.HashMap;


import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import xmlLoading.XML_Loader;

import applicationSpecific.AbilityType;
import applicationSpecific.EnemyType;
import applicationSpecific.HeroType;
import applicationSpecific.ItemType;
import applicationSpecific.Paths;
import applicationSpecific.TowerType;

public class Game extends StateBasedGame {

	
	private static final String GAME_TITLE = "Slick TD";
	public static final int STATE_SPLASHSCREEN = 0;
	public static final int STATE_GAMEPLAY = 1;
	public static final int STATE_PAUSED = 2;
	
	public static enum Team {
		GOOD, EVIL, NEUTRAL;
	}
	
	private static HashMap<EnemyType, EnemyData> globalEnemyStats;
	private static HashMap<TowerType, TowerData> globalTowerStats;
	private static HashMap<AbilityType, AbilityData> globalAbilityStats;
	private static HashMap<ItemType, ItemData> globalItemStats;
	private static HashMap<HeroType, HeroData> globalHeroStats;
	private static WaveReward[] globalWaveRewards;
	
	public Game() {
		super(GAME_TITLE);
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		
		addState(new SplashScreenState());
		addState(new GamePlayState());
		addState(new PausedState());
		enterState(STATE_SPLASHSCREEN);
		
	}
	
	public static void loseGame() {
		System.out.println("YOU LOST THE GAME");
	}

	public static void winGame() {
		System.out.println("YOU WON THE GAME");
	}
	
	

	static public void loadEnemyData(){
		globalEnemyStats = XML_Loader.loadEnemyStats("src/" + Paths.RESOURCES_FILE_PATH + "enemyStats.xml");
	}

	static public void loadTowerData(){
		globalTowerStats = XML_Loader.loadTowerStats("src/" + Paths.RESOURCES_FILE_PATH + "towerStats.xml");
	}
	
	//TODO Varf�r beh�vs src/ i pathen?
	
	static public void loadAbilityData(){
		globalAbilityStats = XML_Loader.loadAbilityStats("src/" + Paths.RESOURCES_FILE_PATH + "abilityStats.xml");
	}

	static public void loadItemData(){
		globalItemStats = XML_Loader.loadItemStats("src/" + Paths.RESOURCES_FILE_PATH + "itemStats.xml");
	}
	
	static public void loadHeroData(){
		globalHeroStats = XML_Loader.loadHeroStats("src/" + Paths.RESOURCES_FILE_PATH + "heroStats.xml");
	}
	
	static public void loadWaveRewards(){
		globalWaveRewards = XML_Loader.loadWaveRewards("src/" + Paths.RESOURCES_FILE_PATH + "waveRewards.xml");
	}
	
	
	public static EnemyData getEnemyData(EnemyType type){
		checkStatsExists(globalEnemyStats, type);
		return globalEnemyStats.get(type);
	}
	
	public static TowerData getTowerData(TowerType type){
		checkStatsExists(globalTowerStats, type);
		return globalTowerStats.get(type);
	}
	
	public static AbilityData getAbilityData(AbilityType type) {
		checkStatsExists(globalAbilityStats, type);
		return globalAbilityStats.get(type);
	}
	
	public static ItemData getItemData(ItemType type){
		checkStatsExists(globalItemStats, type);
		return globalItemStats.get(type);
	}
	
	public static HeroData getHeroData(HeroType type){
		checkStatsExists(globalHeroStats, type);
		return globalHeroStats.get(type);
	}
	
	public static WaveReward getWaveReward(int waveNumber){
		return globalWaveRewards[waveNumber - 1];
	}
	
	@SuppressWarnings("rawtypes")
	private static void checkStatsExists(HashMap statsMap, Object type){
		if(!statsMap.containsKey(type)){
			new IllegalArgumentException("No stats for unknown type: " + type).printStackTrace();
			System.exit(0);
		}
	}
}
