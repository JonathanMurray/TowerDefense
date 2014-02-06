package rendering;


import java.awt.Point;

import org.newdawn.slick.Input;
import org.newdawn.slick.gui.GUIContext;


class KeyboardDialog extends Dialog {

	private int selectedChoice;

	static KeyboardDialog createDialogWithReplacement(HUD hud,
			GUIContext container, String title, Point upperLeft,
			DialogChoice replacement, DialogChoice[] old) {
		if (replacement == null) {
			throw new IllegalArgumentException();
		}
		return new KeyboardDialog(hud, container, title, upperLeft,
				replacement, old);
	}

	static KeyboardDialog createDialog(HUD hud, GUIContext container,
			String title, Point upperLeft, DialogChoice[] choices) {
		return new KeyboardDialog(hud, container, title, upperLeft, null,
				choices);
	}

	private KeyboardDialog(HUD hud, GUIContext container, String title,
			Point upperLeft, DialogChoice replacement, DialogChoice[] old) {
		super(hud, container, title, upperLeft, replacement, false, old);
		selectedChoice = 0;
	}

	@Override
	void handleInput(Input input, boolean isMousePressed) {

		if (input.isKeyPressed(Input.KEY_ENTER) && !hasMadeChoice()) {
			makeChoice(selectedChoice);
		}
		int numberOfButtons = choiceButtons.getNumberOfButtons();
		if (input.isKeyPressed(Input.KEY_RIGHT)) {
			selectedChoice = (selectedChoice + 1) % numberOfButtons;
		}
		if (input.isKeyPressed(Input.KEY_LEFT)) {
			selectedChoice = (selectedChoice + numberOfButtons - 1)
					% numberOfButtons;
		}
		setHighlighted(selectedChoice);
		Point upperLeft = new Point((int) choiceButtons.getNthButtonRectangle(
				selectedChoice + 1).getX(), (int) choiceButtons
				.getNthButtonRectangle(selectedChoice + 1).getY() - 20);
		tooltip = hud.createButtonTooltip(upperLeft,
				choiceButtons.getTooltipOfButtonWithIndex(selectedChoice));
	}

	@Override
	public void buttonWasUnlockPressed(ButtonRow sourceRow, Button sourceButton, int buttonIndex) {
		// TODO Auto-generated method stub
		
	}

}
