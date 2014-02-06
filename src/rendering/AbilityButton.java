package rendering;

import game.AbilityData;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;

import applicationSpecific.AbilityType;

class AbilityButton extends Button {

	private AbilityType ability;
	private AbilityData abilityData;
	private int totalTimeToWait;
	private int timeLeftToWait; 

	AbilityButton(GUIContext container, Rectangle iconRect,
			AbilityType ability, AbilityData stats, char buttonChar) {
		super(container, iconRect, false, stats.icon, buttonChar, 0,
				stats.tooltip, stats.name, stats.manaCost + " mana");
		this.ability = ability;
		this.abilityData = stats;
	}

	AbilityButton(GUIContext container, Rectangle iconRect) {
		super(container, iconRect, false, null, ' ', 0, "");
	}
	
	void wasUsed(int timeUntilCanUseAgain){
		totalTimeToWait = timeUntilCanUseAgain;
		timeLeftToWait = timeUntilCanUseAgain;
	}
	
	@Override
	void update(int delta){
		super.update(delta);
		if(timeLeftToWait > 0){
			timeLeftToWait -= delta;
			setPercentToShade((int)(100*timeLeftToWait/(double)totalTimeToWait));
		}
	}

//	@Override
//	boolean isActive() {
//		return HeroInfo.INSTANCE.isHeroAlive()
//				&& HeroInfo.INSTANCE.getHero().canUseAbility(ability);
//	}

//	@Override
//	int getPercentToShade() {
//		if (!HeroInfo.INSTANCE.isHeroAlive()) {
//			return 100;
//		}
//		if (!HeroInfo.INSTANCE.getHero().hasEnoughManaForAbility(ability)) {
//			return 100;
//		}
//		return HeroInfo.INSTANCE.getHero().getAbilityPercentRemainingCooldown(ability);
//	}

	void setAbility(AbilityType newAbility, AbilityData stats) {
		ability = newAbility;
		abilityData=stats;
		setIcon(stats.icon);
		setTexts(new String[] { stats.name, stats.manaCost + " mana" });
		setTooltip(stats.tooltip);
	}

	AbilityType getAbilityType() {
		return ability;
	}
	
	AbilityData getAbilityData(){
		return abilityData;
	}

}
