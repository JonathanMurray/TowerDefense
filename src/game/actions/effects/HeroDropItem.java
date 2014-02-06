package game.actions.effects;

import game.actions.ParameterName;
import game.actions.Parameters;
import game.objects.Entity;
import game.objects.HeroInfo;
import applicationSpecific.ItemType;

public class HeroDropItem implements Effect{
	
	private ItemType itemType;

	public HeroDropItem(ItemType itemType){
		this.itemType = itemType;
	}

	@Override
	public boolean execute(Entity actor, Entity target, Parameters context) {
		String contextItemType = context.getString(ParameterName.ITEM_TYPE, "");
		ItemType itemType = contextItemType.length() == 0 ? this.itemType : ItemType.valueOf(contextItemType);
		HeroInfo.INSTANCE.dropItem(itemType);
		return true;
	}
}
