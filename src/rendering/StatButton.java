package rendering;

import game.objects.HeroStat;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;

class StatButton extends Button {

	StatButton(GUIContext container, Rectangle iconRect, Image icon,
			HeroStat stat, char buttonChar) {
		super(container, iconRect, false, icon, buttonChar, 0, stat.tooltip,stat.abbreviation);
		isActive = false;
	}
}
