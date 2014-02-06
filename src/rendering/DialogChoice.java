package rendering;

import game.AbilityData;
import game.objects.TowerData;

import org.newdawn.slick.Image;

import applicationSpecific.SuperTowerType;

 class DialogChoice {

	private Image icon;
	private String text;
	private String tooltip;

	 DialogChoice(TowerData towerStats) {
		this(towerStats.icon, towerStats.name, towerStats.tooltip);
	}

	 DialogChoice(AbilityData stats) {
		this(stats.icon, stats.name, stats.tooltip);
	}

	 DialogChoice(SuperTowerType type) {
		this(type.icon, type.name, type.tooltip);
	}

	 DialogChoice(Image icon, String text, String tooltip) {
		this.icon = icon;
		this.text = text;
		this.tooltip = tooltip;
	}

	 String getText() {
		return text;
	}

	 String getTooltip() {
		return tooltip;
	}

	 Image getScaledIcon(int width, int height) {
		return icon.getScaledCopy(width, height);
	}
}
