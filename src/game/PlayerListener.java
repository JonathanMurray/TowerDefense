package game;

import applicationSpecific.ItemType;
import applicationSpecific.TowerType;

public interface PlayerListener {
	
	void towerWasAdded(TowerType towerType);

	void towerWasUnlocked(TowerType towerType);

	void itemWasAdded(ItemType itemType);

	void itemWasRemoved(ItemType itemType);
	
	void moneyWasUpdated(int newAmount);
	
	void playerLifeWasUpdated(int newAmount);
	
	
}
