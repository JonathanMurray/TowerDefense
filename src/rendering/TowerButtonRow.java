package rendering;

import game.LoadedData;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;

import applicationSpecific.TowerType;

class TowerButtonRow extends ButtonRow {

	TowerButtonRow(GUIContext container, Rectangle firstButtonRect, int buttonGap, int numberOfButtons) {
		super(container, firstButtonRect, buttonGap, numberOfButtons);
	}

	void addTowerButton(TowerType tower) {
		int firstEmptyIndex = getFirstEmptyButtonIndex();
		TowerButton button = new TowerButton(container, getNthButtonRectangle(firstEmptyIndex + 1), tower, LoadedData.getTowerData(tower));
		button.addListener(this);
		addButton(button);
		button.setUnlockAreaActive(button.getTowerData().unlockCost <= money);
	}
	
	private int money;

	void setMoney(int money) {
		this.money = money;
		for (Button b : buttons) {
			if (!b.isBlank()) {
				TowerButton tb = (TowerButton)b;
				boolean canAfford = tb.getTowerData().buildCost <= money;
				b.setActive(canAfford && !tb.towerLocked);
				if(tb.towerLocked){
					tb.setUnlockAreaActive(tb.getTowerData().unlockCost <= money);
				}
			}
		}
	}

	void unlockTower(TowerType towerType) {
		for (Button button : buttons) {
			TowerButton towerButton = (TowerButton) button;
			if (towerButton.getTowerType() == towerType) {
				towerButton.unlockTower(money);
				return;
			}
		}
		throw new IllegalStateException("should be some hit in arraylist");
	}

	TowerType getTowerOfButtonWithIndex(int selectedTowerIndex) {
		return ((TowerButton) buttons.get(selectedTowerIndex)).getTowerType();
	}

}
