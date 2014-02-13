package rendering;

import game.ItemData;
import game.LoadedData;
import game.ResourceLoader;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;

import applicationSpecific.ItemType;

 class InventoryButton extends Button {

	private ItemType item;
	private int stackSize;

	 InventoryButton(GUIContext container, Rectangle iconRect, ItemType item, char buttonChar, int stackSize) {
		this(container,iconRect, item, LoadedData.getItemData(item), buttonChar, stackSize);
	}
	
	private InventoryButton(GUIContext container, Rectangle iconRect, ItemType item, ItemData stats, char buttonChar, int stackSize){
		super(container, iconRect, false, stats.icon, buttonChar, stackSize, 
				stats.tooltip, stats.name);
		this.item = item;
		this.stackSize = stackSize;
	}

	 InventoryButton(GUIContext container, Rectangle iconRect) {
		super(container, iconRect, false, null, ' ', 0, "");
	}

	 void setItem(ItemType item) {
		this.item = item;
		ItemData stats = LoadedData.getItemData(item);
		if (item == null) {
			setIcon(ResourceLoader.createBlankImage(10, 10));
			setTexts(new String[] { "" });
			setTooltip("");
		} else {
			setIcon(stats.icon);
			setTexts(new String[] { stats.name });
			setTooltip(stats.tooltip);
		}

	}

	 ItemType getItem() {
		return item;
	}
	 
	 int getStackSize(){
		 return stackSize;
	 }
	 
	 void addToStackSize(int amount){
		 stackSize += amount;
		 super.addToTopCornerNumber(amount);
	 }

	public String toString() {
		return "ItemButton[" + item + " x" + stackSize + "]";
	}

}
