package game;

import game.WaveHandler.WavesData;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import applicationSpecific.EnemyType;
import applicationSpecific.Paths;

public class SplashScreenState extends BasicGameState {
	
	private static boolean mapCreated = false;
	private static boolean musicLoaded = false;
	private static boolean wavesLoaded = false;
	private static boolean enemyStatsLoaded = false;
	private static boolean towerStatsLoaded = false;
	private static boolean abilityStatsLoaded = false;
	private static boolean itemStatsLoaded = false;
	private static boolean heroStatsLoaded = false;
	private static boolean waveRewardsLoaded = false;
	
	private boolean render;
	
	private GamePlayState gameplayState;
	
	public SplashScreenState(GamePlayState gameplayState, boolean render){
		this.render = render;
		this.gameplayState = gameplayState;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {

	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		System.out.println("Entering splash screen state...  (time = " + (System.currentTimeMillis() % 1000000) + ")");
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		
		if(!render){
			return;
		}
		
		if(!mapCreated){
			g.setColor(Color.gray);
			g.drawString("setup map   [ ]", 600, 300);
		}else{
			g.setColor(Color.white);
			g.drawString("setup map   [x]", 600, 300);
		}	
		if(!musicLoaded){
			g.setColor(Color.gray);
			g.drawString("load music  [ ]", 600, 350);
		}else{
			g.setColor(Color.white);
			g.drawString("load music  [x]", 600, 350);
		}
		if(!wavesLoaded){
			g.setColor(Color.gray);
			g.drawString("setup waves [ ]", 600, 400);
		}else{
			g.setColor(Color.white);
			g.drawString("setup waves [x]", 600, 400);
		}
		if(!enemyStatsLoaded){
			g.setColor(Color.gray);
			g.drawString("load enemies  [ ]", 600, 450);
		}else{
			g.setColor(Color.white);
			g.drawString("load enemies  [x]", 600, 450);
		}
		if(!towerStatsLoaded){
			g.setColor(Color.gray);
			g.drawString("load towers  [ ]", 600, 500);
		}else{
			g.setColor(Color.white);
			g.drawString("load towers  [x]", 600, 500);
		}
		if(!abilityStatsLoaded){
			g.setColor(Color.gray);
			g.drawString("load abilities  [ ]", 600, 550);
		}else{
			g.setColor(Color.white);
			g.drawString("load abilities  [x]", 600, 550);
		}
		if(!itemStatsLoaded){
			g.setColor(Color.gray);
			g.drawString("load items  [ ]", 600, 600);
		}else{
			g.setColor(Color.white);
			g.drawString("load items  [x]", 600, 600);
		}
		
		if(!heroStatsLoaded){
			g.setColor(Color.gray);
			g.drawString("load heroes  [ ]", 600, 650);
		}else{
			g.setColor(Color.white);
			g.drawString("load heroes  [x]", 600, 650);
		}
		
		if(!waveRewardsLoaded){
			g.setColor(Color.gray);
			g.drawString("load wave rewards  [ ]", 600, 700);
		}else{
			g.setColor(Color.white);
			g.drawString("load wave rewards  [x]", 600, 700);
		}
	}
	
	

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		
		
		
		if(!mapCreated){
			System.out.println("\n\n");
			System.out.println("Creating map...");
			new Map();
			System.out.println("Created map.");
			mapCreated = true;
			return;
		}
		
		
		
		if(!musicLoaded){
			SoundHandler.loadMusic();
			musicLoaded = true;
			return;
		}
		
		if(!wavesLoaded){
			try {
				gameplayState.setWaves(new WaveHandler(loadWavesDataFromFile(Paths.WAVES_PATH), gameplayState));
				wavesLoaded = true;
				return;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		
		if(!enemyStatsLoaded){
			LoadedData.loadEnemyData();
			enemyStatsLoaded = true;
			return;
		}
		
		if(!towerStatsLoaded){
			LoadedData.loadTowerData();
			towerStatsLoaded = true;
			return;
		}
		
		
		if(!abilityStatsLoaded){
			LoadedData.loadAbilityData();
			abilityStatsLoaded = true;
			return;
		}
		
		if(!itemStatsLoaded){
			LoadedData.loadItemData();
			itemStatsLoaded = true;
			return;
		}
		
		if(!heroStatsLoaded){
			LoadedData.loadHeroData();
			heroStatsLoaded = true;
			return;
		}
		
		if(!waveRewardsLoaded){
			LoadedData.loadWaveRewards();
			waveRewardsLoaded = true;
			return;
		}
		
		game.enterState(OfflineGame.STATE_GAMEPLAY);
		
	}


	@Override
	public int getID() {
		return OfflineGame.STATE_SPLASHSCREEN;
	}
	
	public WavesData loadWavesDataFromFile(String filePath) throws FileNotFoundException {

		// Scanner file = new Scanner(new FileReader(filePath));
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
		
		Scanner file = new Scanner(inputStream);
		Scanner line;

		Wave wave;

		line = new Scanner(file.nextLine());
		int secondsToWaitUntilFirstWave = line.nextInt();
		ArrayList<Wave> waves = new ArrayList<>();
		line.close();
		line = new Scanner(file.nextLine());
		int waveDurationInSeconds = line.nextInt();
		wave = new Wave(waveDurationInSeconds * 1000);

		while (file.hasNextLine()) {
			line = new Scanner(file.nextLine());

			if (!line.hasNext()) { // /empty line
				waves.add(wave);
				Scanner lineAfterSpace = new Scanner(file.nextLine());
				waveDurationInSeconds = lineAfterSpace.nextInt();
				wave = new Wave(waveDurationInSeconds * 1000);
				lineAfterSpace.close();
			} else {
				if(!line.hasNext("//.*")){
					int spawnTime = line.nextInt();
					EnemyType enemyData = EnemyType.valueOf(line.next());
					wave.addSpawnTime(spawnTime, enemyData);
				}
			}
		}
		waves.add(wave);
		line.close();
		file.close();
		
		WavesData data = new WavesData();
		data.waves = waves;
		data.secondsToWaitUntilFirstWave  = secondsToWaitUntilFirstWave;
		return data;
	}

}
