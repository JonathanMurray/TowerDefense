package rendering;

import game.ResourceLoader;
import game.objects.TowerData;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.gui.MouseOverArea;

import applicationSpecific.TowerType;

class TowerButton extends Button {

	private Rectangle unlockRect;
	private MouseOverArea unlockMouseOverArea;
	private TowerType tower;
	private TowerData towerData;
	boolean towerLocked = true;
	private static final int unlockHeight = 18;
	private boolean unlockAreaActive;

	TowerButton(GUIContext container, Rectangle iconRect, TowerType tower, TowerData stats) {
		super(container, iconRect, true, stats.icon, ' ', 0, stats.tooltip, stats.name, (stats.buildCost + " gold"));

		Image unlockImg = ResourceLoader.createBlankImage((int) iconRect.getWidth(), unlockHeight);
		unlockRect = new Rectangle(iconRect.getX(), iconRect.getY() + iconRect.getHeight() + 5, unlockImg.getWidth(), unlockImg.getHeight());
		this.unlockMouseOverArea = new MouseOverArea(container, unlockImg, unlockRect);

		this.tower = tower;
		this.towerData = stats;
		isActive = false;
	}

	void addListener(ComponentListener listener) {
		super.addListener(listener);
		unlockMouseOverArea.addListener(listener);
	}

	void render(GUIContext container, Graphics g) {
		super.render(container, g);
		if (towerLocked) {
			renderUnlockButton(container, g);
		}
	}

	private void renderUnlockButton(GUIContext container, Graphics g) {
		g.setColor(Color.darkGray);
		g.fill(unlockRect);
		unlockMouseOverArea.render(container, g);
		if (!unlockAreaActive) {
			RenderUtil.shadeIcon(g, unlockRect, RenderUtil.TRANSPARENT_RED);
		}else{
			RenderUtil.highlightIcon(g, unlockRect, 1, Color.yellow, false);
		}
		g.setColor(Color.white);
		g.drawString(towerData.unlockCost + " gold", unlockMouseOverArea.getX(), unlockMouseOverArea.getY());
	}

	void setUnlockAreaActive(boolean active) {
		unlockAreaActive = active;
	}

	boolean isMouseOver(Input input) {
		int x = input.getMouseX();
		int y = input.getMouseY();
		return super.isMouseOver(input) || unlockRect.contains(x, y);
	}

	void unlockTower(int currentMoney) {
		towerLocked = false;
		setActive(currentMoney >= towerData.buildCost);
	}

	TowerType getTowerType() {
		return tower;
	}

	TowerData getTowerData() {
		return towerData;
	}

	boolean hasUnlockMouseOverArea(MouseOverArea moa) {
		return unlockMouseOverArea == moa;
	}

	// @Override
	// boolean isActive() {
	// return Player.getMoney() >= stats.buildCost && !towerLocked;
	// }

	// @Override
	// int getPercentToShade() {
	// if (!isActive) {
	// return 100;
	// }
	// return 0;
	// }

}
