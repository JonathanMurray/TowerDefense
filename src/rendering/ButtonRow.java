package rendering;

import game.objects.HeroStat;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.gui.MouseOverArea;

import rendering.Button.BlankButton;

class ButtonRow implements ComponentListener {

	GUIContext container;
	Rectangle firstButtonRect;
	private int buttonGap;
	ArrayList<Button> buttons = new ArrayList<Button>();
	private ArrayList<ButtonRowListener> listeners = new ArrayList<ButtonRowListener>();

	ButtonRow(GUIContext container, Rectangle firstButtonRect, int buttonGap, int numberOfButtons) {
		this.container = container;
		this.firstButtonRect = firstButtonRect;
		this.buttonGap = buttonGap;
		for (int i = 0; i < numberOfButtons; i++) {
			buttons.add(new Button.BlankButton(container, getNthButtonRectangle(i + 1)));
		}
	}

	void setPercentToShadeForAll(int percentToShade) {
		for (Button b : buttons) {
			if (!b.isBlank()) {
				b.setPercentToShade(percentToShade);
			}
		}
	}

	void addBasicButton(String tooltip, boolean notifiesListener, Image icon, String... texts) {
		Button.BasicButton button = new Button.BasicButton(container, getNthButtonRectangle(getFirstEmptyButtonIndex() + 1), notifiesListener, icon, tooltip,
				texts);
		button.addListener(this);
		addButton(button);
	}

	void addStatButton(Image icon, HeroStat stat, char buttonChar) {
		addButton(new StatButton(container, getNthButtonRectangle(getFirstEmptyButtonIndex() + 1), icon, stat, buttonChar));
	}

	void addButton(Button button) {
		buttons.set(getFirstEmptyButtonIndex(), button);
	}

	int getFirstEmptyButtonIndex() {
		int index = 0;
		for (Button button : buttons) {
			if (button instanceof BlankButton) {
				return index;
			}
			index++;
		}
		return buttons.size();
	}

	void update(Input input, int delta) {
		for (Button button : buttons) {
			button.update(delta);
		}
		handleMouseOverEvents(input);
	}

	private void handleMouseOverEvents(Input input) {
		int buttonIndex = 0;
		for (Button button : buttons) {
			if (!(button instanceof Button.BlankButton)) { // Don't want tooltip
															// for blank button
				if (button.isMouseOver(input)) {
					for (ButtonRowListener listener : listeners) {
						listener.buttonMouseOver(this, button, buttonIndex);
					}
				}
			}
			buttonIndex++;
		}
	}

	int getNumberOfButtons() {
		return buttons.size();
	}

	int indexOfButton(Button button) {
		return buttons.indexOf(button);
	}

	Rectangle getNthButtonRectangle(int n) {
		return new Rectangle(firstButtonRect.getX() + (n - 1) * (firstButtonRect.getWidth() + buttonGap), firstButtonRect.getY(), firstButtonRect.getWidth(),
				firstButtonRect.getHeight());
	}

	String getTooltipOfButtonWithIndex(int index) {
		return buttons.get(index).getTooltip();
	}

	boolean isMouseOver(Input input) {
		for (Button button : buttons) {
			if (button.isMouseOver(input)) {
				return true;
			}
		}
		return false;
	}

	void render(GUIContext container, Graphics g) {
		for (Button button : buttons) {
			button.render(container, g);
		}
	}

	void highlight(GUIContext container, Graphics g, int highlightedIndex, Color color) {
		buttons.get(highlightedIndex).highlight(container, g, 1, color, false);
	}

	void addListener(ButtonRowListener listener) {
		listeners.add(listener);
	}

	@Override
	public void componentActivated(AbstractComponent source) {
		for (int i = 0; i < buttons.size(); i++) {
			if (buttons.get(i).hasMouseOverArea((MouseOverArea) source) && buttons.get(i).isActive) {
				for (ButtonRowListener listener : listeners) {
					listener.buttonWasPressed(this, buttons.get(i), i);
				}
				return;
			}
			if (buttons.get(i).hasUnlockMouseOverArea((MouseOverArea) source)) {
				for (ButtonRowListener listener : listeners) {
					listener.buttonWasUnlockPressed(this, buttons.get(i), i);
				}
				return;
			}
		}

	}

	void flashButton(int buttonNumber) {
		buttons.get(buttonNumber - 1).flashOnce(1, new Color(0.5f, 0.8f, 1f), false);
	}

}
