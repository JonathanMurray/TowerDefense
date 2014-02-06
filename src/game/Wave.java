package game;

import java.util.SortedMap;
import java.util.TreeMap;

import applicationSpecific.EnemyType;

class Wave {

	private final int durationAfterLastSpawn;
	private int timeSinceLastSpawn;
	private int timeSinceWaveStart;
	private SortedMap<Integer, EnemyType> spawnTimes;

	Wave(int durationAfterLastSpawn) {
		this.durationAfterLastSpawn = durationAfterLastSpawn;
		timeSinceLastSpawn = 0;// will start incrementing after last spawn
		timeSinceWaveStart = 0;
		this.spawnTimes = new TreeMap<Integer, EnemyType>();
	}

	void addSpawnTime(int time, EnemyType enemy) {
		spawnTimes.put(time, enemy);
	}

	void update(int delta) {
		timeSinceWaveStart += delta;
		if (spawnTimes.isEmpty()) {
			timeSinceLastSpawn += delta;
		}
	}

	public String toString() {
		return "Wave [" + durationAfterLastSpawn + "s]";
	}

	int getSecondsRemaining() {
		return (int) ((float) (durationAfterLastSpawn - timeSinceLastSpawn) / (float) 1000);
	}

	boolean hasNextSpawn() {
		if (spawnTimes.isEmpty()) {
			return false;
		}
		return spawnTimes.firstKey() < timeSinceWaveStart;
	}

	EnemyType nextSpawn() {
		EnemyType enemyData = (spawnTimes.get(spawnTimes.firstKey()));
		spawnTimes.remove(spawnTimes.firstKey());
		return enemyData;
	}

	boolean hasMoreSpawns() {
		return !spawnTimes.isEmpty();
	}

	boolean isFinished() {
		return timeSinceLastSpawn >= durationAfterLastSpawn;
	}
}
