package game.objects;


import game.Entities;
import game.Map;
import game.RangedAttack;
import game.TechUpgrade;

import java.awt.Point;
import java.util.ArrayList;

import applicationSpecific.SuperTowerType;

public class SuperTower extends Entity {

	private RangedAttack attack;

	public SuperTower(SuperTowerType type, Point location,
			ArrayList<TechUpgrade> upgrades) {
		super(type.maxHealth, type.armor, type.sprite, location, null);
		attack = type.getAttackCopy();
		for (TechUpgrade upgrade : upgrades) {
			notifyNewUpgrade(upgrade);
		}
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
		attack.update(delta);
		if (attack.isReady()) {
			try {
				Entity enemy = Entities.getEntityWithinRange(getLocation(),
						attack.getRange(), Team.EVIL);
				attack.attackTarget(this, enemy, totalDamageMultiplier);
			} catch (Exception e) {
			}
		}
	}

	public Point getPixelLocation() {
		return new Point(x * Map.getTileWidth(), y * Map.getTileHeight());
	}

	@Override
	public Team getTeam() {
		return Team.GOOD;
	}

}
