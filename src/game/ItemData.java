package game;

import game.actions.Action;
import game.buffs.Buff;
import game.objects.Hero;

import org.newdawn.slick.Image;



public class ItemData{
	final public String name;
	final public String tooltip;
	final private Action useAction;
	final private Buff itemBuff;
	final public boolean isDroppedOnUse;
	
	final public int buyCost;
	final public Image icon;
	final public boolean isUnique;

	public ItemData(String name, String tooltip,
			Action useAction, Buff itemBuff, boolean isDroppedOnUse, int buyCost,
			Image icon, boolean isUnique) {
		this.name = name;
		this.tooltip = tooltip;
		this.buyCost = buyCost;
		this.useAction = useAction;
		this.itemBuff = itemBuff;
		this.isDroppedOnUse = isDroppedOnUse;
		this.icon = icon;
		this.isUnique = isUnique;
	}

	public void wasEquipped(Hero hero) {
		if(itemBuff != null){
			hero.receiveBuff(itemBuff);
		}	
	}

	public void wasUsed(Hero hero) {
		if (!canBeUsed()) {
			throw new IllegalStateException();
		}
		hero.performAction(useAction);
	}

	public void wasDropped(Hero hero) {
		if(itemBuff != null){
			hero.loseBuff(itemBuff);
		}
	}

	public boolean canBeUsed() {
		return useAction != null;
	}
}
