package rendering;

import game.Game;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;

import applicationSpecific.AbilityType;

class AbilityButtonRow extends ButtonRow {

	int heroMana;
	private boolean heroIsAlive = true;

	AbilityButtonRow(GUIContext container, Rectangle firstButtonRect, int buttonGap, int numberOfAbilities) {
		super(container, firstButtonRect, buttonGap, numberOfAbilities);
	}

	void abilityWasUsed(AbilityType abilityType, int timeUntilCanUseAgain) {
		for (Button b : buttons) {
			if (!b.isBlank() && ((AbilityButton) b).getAbilityType() == abilityType) {
				((AbilityButton) b).wasUsed(timeUntilCanUseAgain);
			}
		}
		
	}

	void heroDied() {
		heroIsAlive = false;
		updateAllActive();
	}
	
	void heroResurrected(){
		heroIsAlive = true;
		updateAllActive();
	}
	
	void updateAllActive(){
		if(heroIsAlive){
			for (Button b : buttons) {
				if (!b.isBlank()) {
					b.setActive(((AbilityButton)b).getAbilityData().manaCost <= heroMana);
				}
			}
		}
	}

	void notifyHeroManaChanged(int mana) {
		this.heroMana = mana;
		updateAllActive();
	}

	void addAbilityButton(AbilityType ability, char[] abilityChars) {
		int firstEmptyIndex = getFirstEmptyButtonIndex();
		AbilityButton button = new AbilityButton(container, getNthButtonRectangle(firstEmptyIndex + 1), ability, Game.getAbilityData(ability),
				abilityChars[firstEmptyIndex]);
		addButton(button);
	}

	AbilityType getAbilityOfButtonWithIndex(int index) {
		// return abilityButtons.get(index).getAbility();
		AbilityButton button = (AbilityButton) buttons.get(index);
		return button.getAbilityType();
	}

	void replaceAbilityWithNew(AbilityType oldAbility, AbilityType newAbility) {
		for (Button button : buttons) {
			if (button instanceof AbilityButton) {
				AbilityButton abilityButton = (AbilityButton) button;
				if (abilityButton.getAbilityType() == oldAbility) {
					abilityButton.setAbility(newAbility, Game.getAbilityData(newAbility));
					return;
				}
			}
		}
	}

}
