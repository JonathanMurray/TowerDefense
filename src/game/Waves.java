package game;

import game.objects.enemies.Enemy;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;


import org.newdawn.slick.Input;

import applicationSpecific.EnemyType;

public class Waves {

	private ArrayList<Wave> waves;
	private int msWaitedBeforeFirstWave = 0;
	private int secondsToWaitUntilFirstWave;
	private boolean wavesHaveBegun;
	private int waveIndex = 0;
	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private boolean hasGivenRewardForCurrentWave = false;

	public Waves(WavesData wavesData){
		this.waves = wavesData.waves;
		this.secondsToWaitUntilFirstWave = wavesData.secondsToWaitUntilFirstWave;
	}
	
	public static class WavesData{
		public ArrayList<Wave> waves;
		public int secondsToWaitUntilFirstWave;
	}
	
	public boolean wavesHaveBegun() {
		return wavesHaveBegun;
	}

	public boolean isFinalWave() {
		return waveIndex == waves.size() - 1;
	}

	public int getWaveIndex() {
		return waveIndex;
	}

	public int getSecondsUntilNextWave() {
		if (wavesHaveBegun) {
			return waves.get(waveIndex).getSecondsRemaining();
		}
		return (int) Math.round((secondsToWaitUntilFirstWave * 1000 - msWaitedBeforeFirstWave) / 1000.0);
	}

	public boolean hasMoreSpawns() {
		return waves.get(waveIndex).hasMoreSpawns();
	}

	public void handleWaves(int delta) {
		if (!wavesHaveBegun) {
			handleWaitForFirstWave(delta);
			return;
		}

		Wave currentWave = waves.get(waveIndex);
		currentWave.update(delta);
		if (currentWave.hasNextSpawn()) {
			spawnEnemy(currentWave.nextSpawn());
		}

		if (currentWave.isFinished() && !isFinalWave()) {
			startNextWave();
		}
		removeDeadEnemies();
		if (currentWaveHasBeenCleared() && !hasGivenRewardForCurrentWave) {
			GamePlayState.giveRewardForWave(waveIndex + 1);
			hasGivenRewardForCurrentWave = true;
			if (isFinalWave()) {
				Game.winGame();
			}
		}
	}

	private void handleWaitForFirstWave(int delta) {
		msWaitedBeforeFirstWave += delta;
		if (msWaitedBeforeFirstWave >= secondsToWaitUntilFirstWave * 1000) {
			startNextWave();
		}
	}

	public void handleInput(Input input) {
		if (input.isKeyPressed(Input.KEY_S)) {
			if (currentWaveHasBeenCleared() || !wavesHaveBegun()) {
				if (!isFinalWave()) {
					startNextWave();
				}

			}
		}
	}

	public boolean currentWaveHasBeenCleared() {
		return !hasMoreSpawns() && enemies.isEmpty();
	}

	public void startNextWave() {
		if (!wavesHaveBegun) {
			wavesHaveBegun = true;
			return;
		}
		if (!hasGivenRewardForCurrentWave) {
			GamePlayState.giveRewardForWave(waveIndex + 1);
		}
		waveIndex++;
		enemies.clear();
		hasGivenRewardForCurrentWave = false;
	}

	private void removeDeadEnemies() {
		Iterator<Enemy> it = enemies.iterator();
		while (it.hasNext()) {
			if (it.next().shouldBeRemovedFromGame()) {
				it.remove();
			}
		}
	}

	private void spawnEnemy(EnemyType enemyData) {
		Enemy heroEnemy = Enemy.constructEnemy(enemyData, LaneType.heroLane);
		GamePlayState.addEnemy(heroEnemy);
		Enemy tdEnemy = Enemy.constructEnemy(enemyData, LaneType.towerLane);
		GamePlayState.addEnemy(tdEnemy);
		enemies.add(heroEnemy);
		enemies.add(tdEnemy);
	}

	public enum LaneType {
		heroLane, towerLane;
	}

}
