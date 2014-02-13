package rendering;

import game.ItemData;
import game.LoadedData;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;

import applicationSpecific.ItemType;

class VendorButton extends Button {
	private ItemType item;
	private ItemData itemData;

	VendorButton(GUIContext container, Rectangle iconRect,ItemType item) {
		this(container, iconRect, item, LoadedData.getItemData(item));
	}
	
	private VendorButton(GUIContext container, Rectangle iconRect,ItemType item, ItemData stats) {
		super(container, iconRect, true, stats.icon, ' ', 0,
				stats.tooltip, stats.name, (stats.buyCost + " gold"));
		this.item = item;
		this.itemData = stats;
	}

	ItemType getItemType() {
		return item;
	}
	
	ItemData getItemData(){
		return itemData;
	}

//	@Override
//	boolean isActive() {
//		return Player.affordsItem(item) && GamePlayState.isHeroAliveAndCloseEnoughToMerchant();
//	}

//	@Override
//	int getPercentToShade() {
//		if (!isActive) {
//			return 100;
//		}
//		return 0;
//	}
}
