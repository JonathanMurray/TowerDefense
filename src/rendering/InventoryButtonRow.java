package rendering;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;

import applicationSpecific.ItemType;

 class InventoryButtonRow extends ButtonRow {
	 
	 private int timeLeftToWaitForReady;
	 private int totalTimeToWaitForReady;

	InventoryButtonRow(GUIContext container, Rectangle firstButtonRect,
		int buttonGap, int numberOfButtons) {
		super(container, firstButtonRect, buttonGap, numberOfButtons);
	}
	
	ItemType getItemWithNumber(int itemNumber){
		Button b =buttons.get(itemNumber - 1);
		if(b instanceof InventoryButton){
			return ((InventoryButton)b).getItem();
		}
		throw new IllegalArgumentException("itemNumber: " + itemNumber);
	}
	
	int getNumberOfItem(ItemType item){
		int number = 1;
		for(Button b : buttons){
			if(b instanceof InventoryButton && ((InventoryButton)b).getItem() == item){
				return number;
			}
			number ++;
		}
		throw new IllegalArgumentException("Dont have item");
	}
	
	void heroDied() {
		setAllActive(false);
	}
	
	void heroResurrected(){
		setAllActive(true);
	}
	
	private void setAllActive(boolean active){
		for (Button b : buttons) {
			if (!b.isBlank()) {
				b.setActive(active);
			}
		}
	}
	 
	void itemWasUsed(int timeUntilCanUseAgain){
		totalTimeToWaitForReady = timeUntilCanUseAgain;
		timeLeftToWaitForReady = timeUntilCanUseAgain;
	}
	
	void update(Input input, int delta){
		super.update(input, delta);
		if(timeLeftToWaitForReady > 0){
			timeLeftToWaitForReady -= delta;
			setPercentToShadeForAll((int)(100 * timeLeftToWaitForReady / (double)totalTimeToWaitForReady));
		}
	}
	
	void itemWasAdded(ItemType item, char[] itemChars){
		for(Button b : buttons){
			if(b instanceof InventoryButton && ((InventoryButton)b).getItem() == item){
				((InventoryButton)b).addToStackSize(1);
				return;
			}
		}
		addInventoryButton(item, itemChars, 1);
	}

	private void addInventoryButton(ItemType item, char[] itemChars, int stackSize) {
		
		int firstEmptyIndex = getFirstEmptyButtonIndex();
		InventoryButton button = new InventoryButton(container,
				getNthButtonRectangle(firstEmptyIndex + 1), item,
				itemChars[firstEmptyIndex], stackSize);
		addButton(button);
	}

	 void replaceItemWithNew(int oldItemIndex, ItemType newItem) {
		((InventoryButton) buttons.get(oldItemIndex)).setItem(newItem);
	}

	 void itemWasDropped(int itemIndex) {
		 if(((InventoryButton)buttons.get(itemIndex)).getStackSize() == 1){
			 buttons.set(itemIndex, new Button.BlankButton(container,
						getNthButtonRectangle(itemIndex + 1)));
		 }else{
			 ((InventoryButton)buttons.get(itemIndex)).addToStackSize( - 1);
		 }
	}

}
