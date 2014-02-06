package rendering;


import java.awt.Font;
import java.awt.Point;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;


 abstract class Dialog implements ButtonRowListener {

	private Font f = new Font("Verdana", Font.BOLD, 17);
	private TrueTypeFont titleFont = new TrueTypeFont(f, true);

	protected static final int ICON_WIDTH = 80;
	private final int ICON_GAP = 20;
	private final int TITLE_HEIGHT = 30;

	protected HUD hud;
	private Rectangle rectangle;
	protected Button replacementButton;
	protected ButtonRow choiceButtons;
	private int highlightedIndex = 0;
	protected TextBox tooltip;

	private String title;

	private boolean choiceHasBeenMade = false;
	private DialogListener listener;

	 Dialog(HUD hud, GUIContext container, String title, Point upperLeft,
			DialogChoice replacement, boolean notifiesListener,
			DialogChoice... old) {
		this.hud = hud;
		this.title = title;
		int x = upperLeft.x;
		int y = upperLeft.y;
		int width = ICON_GAP + (ICON_GAP + ICON_WIDTH) * old.length;
		int height;
		int choicesY;
		if (replacement != null) {
			height = ICON_WIDTH * 2 + ICON_GAP * 4 + 30 + TITLE_HEIGHT;
			choicesY = y + 3 * ICON_GAP + ICON_WIDTH + 30 + TITLE_HEIGHT;
			replacementButton = new Button.BasicButton(container,
					new Rectangle(x + 50, y + ICON_GAP * 2 + TITLE_HEIGHT,
							ICON_WIDTH, ICON_WIDTH), true,
					replacement.getScaledIcon(ICON_WIDTH, ICON_WIDTH),
					replacement.getTooltip(), replacement.getText());
		} else {
			height = ICON_WIDTH + ICON_GAP * 2 + 30 + TITLE_HEIGHT;
			choicesY = y + ICON_GAP + 30 + TITLE_HEIGHT;
		}
		this.rectangle = new Rectangle(x, y, width, height);
		choiceButtons = new ButtonRow(container, new Rectangle(x + ICON_GAP,
				choicesY, ICON_WIDTH, ICON_WIDTH), ICON_GAP, old.length);
		for (DialogChoice choice : old) {
			choiceButtons.addBasicButton(choice.getTooltip(), notifiesListener,
					choice.getScaledIcon(ICON_WIDTH, ICON_WIDTH),
					choice.getText());
		}
		choiceButtons.addListener(this);
	}

	 void update(Input input, int delta) {
		choiceButtons.update(input, delta);
	}

	protected void setHighlighted(int choiceIndex) {
		highlightedIndex = choiceIndex;
	}

	 void setListener(DialogListener listener) {
		this.listener = listener;
	}

	 void makeChoice(int choiceIndex) {
		choiceHasBeenMade = true;
		listener.choiceWasMade(choiceIndex);
	}

	 void handleInput(Input input, boolean isMousePressed) {

	}

	 boolean hasMadeChoice() {
		return choiceHasBeenMade;
	}

	 void render(GUIContext container, Graphics g) {
		g.setColor(Color.gray);
		g.fill(rectangle);
		renderTitle(g);
		if (replacementButton != null) {
			renderReplacement(container, g);
		}
		choiceButtons.render(container, g);
		choiceButtons.highlight(container, g, highlightedIndex, Color.green);
		g.setColor(Color.white);
		g.drawRoundRect(rectangle.getX(), rectangle.getY(),
				rectangle.getWidth(), rectangle.getHeight(), 5);
		g.setFont(HUD.NORMAL_FONT);
		if (tooltip != null) {
			tooltip.render(container, g);
		}
	}

	private void renderTitle(Graphics g) {
		g.setColor(Color.white);
		g.setFont(titleFont);
		g.drawString(title, rectangle.getX() + 5, rectangle.getY() + 7);
		g.setFont(HUD.NORMAL_FONT);
	}

	private void renderReplacement(GUIContext container, Graphics g) {
		g.setColor(Color.white);
		g.drawString("Let", rectangle.getX() + 5, rectangle.getY() + ICON_GAP
				* 2 + ICON_WIDTH / 2 - 10 + TITLE_HEIGHT);
		replacementButton.render(container, g);
		g.setColor(Color.white);
		g.drawString("replace:", rectangle.getX() + 50 + ICON_WIDTH + 10,
				rectangle.getY() + ICON_GAP * 2 + ICON_WIDTH / 2 - 10
						+ TITLE_HEIGHT);
	}

	 boolean isMouseOver(Input input) {
		return rectangle.contains(input.getMouseX(), input.getMouseY());
	}

	 @Override
	 public void buttonWasPressed(ButtonRow sourceRow, Button sourceButton,int buttonIndex) {
		makeChoice(buttonIndex);
	}

	 @Override
	 public void buttonMouseOver(ButtonRow sourceRow, Button sourceButton, int buttonIndex) {
		tooltip = hud.createButtonTooltip(sourceButton.getUpperLeft(),
				sourceRow.getTooltipOfButtonWithIndex(buttonIndex));
	}

}
