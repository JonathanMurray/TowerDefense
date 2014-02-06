package rendering;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;

import applicationSpecific.ItemType;

class VendorButtonRow extends ButtonRow {

	private int playerMoney;
	private boolean heroIsInRangeOfVendor;

	VendorButtonRow(GUIContext container, Rectangle firstButtonRect,
			int buttonGap, int numberOfButtons) {
		super(container, firstButtonRect, buttonGap, numberOfButtons);
	}

	void addVendorButton(ItemType item) {
		int firstEmptyIndex = getFirstEmptyButtonIndex();
		VendorButton button = new VendorButton(container,
				getNthButtonRectangle(firstEmptyIndex + 1), item);
		button.addListener(this);
		addButton(button);
	}

	ItemType getItemOfButtonWithIndex(int index) {
		VendorButton button = (VendorButton) buttons.get(index);
		return button.getItemType();
	}

	void removeVendorButton(ItemType itemType) {
		int buttonIndex = 0;
		for (Button button : buttons) {
			if (button instanceof VendorButton) {
				if (((VendorButton) button).getItemType() == itemType) {
					break;
				}
			}
			buttonIndex++;
		}
		buttons.set(buttonIndex, new Button.BlankButton(container,
				getNthButtonRectangle(buttonIndex + 1)));
	}

	public void heroIsNowInRangeOfVendor(boolean isInRange) {
		heroIsInRangeOfVendor = isInRange;
		updateAllActive();
	}
	
	private void updateAllActive(){
		for (Button b : buttons) {
			if (!b.isBlank()) {
				b.setActive(heroIsInRangeOfVendor && ((VendorButton)b).getItemData().buyCost <= playerMoney);
			}
		}
		
	}


	public void setMoney(int newAmount) {
		this.playerMoney = newAmount;
		updateAllActive();
	}
}
