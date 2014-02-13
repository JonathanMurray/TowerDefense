package game.objects;

import game.Entities;
import game.LoadedData;
import game.Map;
import game.RangedAttack;
import game.TechUpgrade;

import java.awt.Point;
import java.util.ArrayList;

import applicationSpecific.TowerType;

public class Tower extends Entity {

	private RangedAttack attack;
	private int buildCost;
	
	private int specialPixelRange;
	private int attackPixelRange;
	
	public static Tower createTower(TowerType type, Point location, ArrayList<TechUpgrade> upgrades){
		TowerData stats = LoadedData.getTowerData(type);
		return new Tower(type, stats, location, upgrades);
	}

	private Tower(TowerType type, TowerData stats, Point location, ArrayList<TechUpgrade> upgrades) {
		super(stats.maxHealth, 0, stats.sprite, location, null);
		attack = stats.getAttack();
		buildCost = stats.buildCost;
		for (TechUpgrade upgrade : upgrades) {
			notifyNewUpgrade(upgrade);
		}
		if(stats.behaviourBuff != null){
			receiveBuff(stats.behaviourBuff.getCopy());
		}
		specialPixelRange = (int)(Map.getTileWidth()*stats.specialRange);
		if(attack != null){
			attackPixelRange = (int)(attack.getRange()*Map.getTileWidth()); 
		}
	}
	
	public int getMaxHealth(){
		return super.maxHealth;
	}

	public void notifyNewUpgrade(TechUpgrade upgrade) {
		switch (upgrade) {
		case towerHealth:
			maxHealth += upgrade.getAmount();
			break;
		case towerRange:
			attack.changeRange(upgrade.getAmount());
			break;
		}
	}

	public void update(int delta) {
		super.update(delta);
		if (attack != null) {
			handleAttack(delta);
		}
	}

	private void handleAttack(int delta) {
		attack.update(delta);
		if (attack.isReady()) {
			try {
				Entity enemy = Entities.getEntityWithinRange(getLocation(),
						attack.getRange(), Team.EVIL);
				attack.attackTarget(this, enemy, totalDamageMultiplier);
			} catch (EntityNotFound e) {
			}
		}
	}

	public int getBuildCost() {
		return buildCost;
	}
	
	public int getAttackPixelRange(){
		if(Map.getTileWidth() != Map.getTileHeight()){
			throw new IllegalStateException("Range will look strange since only width is used here.");
		}
		return attackPixelRange;
	}
	
	public int getSpecialPixelRange(){
		if(Map.getTileWidth() != Map.getTileHeight()){
			throw new IllegalStateException("Range will look strange since only width is used here.");
		}
		return specialPixelRange;
	}

	public Point getPixelLocation() {
		return new Point(x * Map.getTileWidth(), y * Map.getTileHeight());
	}

	@Override
	public Team getTeam() {
		return Team.GOOD;
	}
}
