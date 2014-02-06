package rendering;

import game.ResourceLoader;

import java.awt.Font;
import java.awt.Point;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.gui.MouseOverArea;


abstract class Button {

	private static Font f = new Font("Verdana", Font.BOLD, 16);
	private static TrueTypeFont bigFont = new TrueTypeFont(f, true);

	private Image background;
	private Rectangle iconRect;
	private MouseOverArea mouseOverArea;
	private Image icon;
	private char buttonChar;
	private int topCornerButtonNumber;
	private String[] texts;
	private String tooltip;
	private boolean notifiesMouseClick;

	private boolean isFlashing = false;
	private int flashLineWidth;
	private boolean flashAlsoFill;
	private Color flashColor;
	private final int FLASH_DURATION = 160;
	private int timeSinceFlashing = 0;
	
	private boolean wasChargingLastUpdate = false;
	boolean isActive = true;
	private int percentToShade;

	Button(GUIContext container, Rectangle iconRect,
			boolean notifiesMouseClick, Image icon, char buttonChar, int topCornerButtonNumber,
			String tooltip, String... texts) {
		this.iconRect = iconRect;
		this.mouseOverArea = new MouseOverArea(container, null, iconRect);
		setIcon(icon);
		this.topCornerButtonNumber = topCornerButtonNumber;
		this.buttonChar = buttonChar;
		this.texts = texts;
		this.tooltip = tooltip;
		this.notifiesMouseClick = notifiesMouseClick;
		background = ResourceLoader.createScaledImage(
				"interface/blueBackground.png", (int) iconRect.getWidth(),
				(int) iconRect.getHeight());
	}
	
	void setActive(boolean active){
		isActive = active;
	}

	void setIcon(Image icon) {
		if (icon == null) {
			throw new IllegalArgumentException("");
		}
		this.icon = icon.getScaledCopy((int) iconRect.getWidth(),
				(int) iconRect.getHeight());
		mouseOverArea.setNormalImage(this.icon);
		mouseOverArea.setMouseOverImage(this.icon);
		mouseOverArea.setMouseDownImage(this.icon);
	}

	void setTexts(String[] texts) {
		this.texts = texts;
	}

	void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
	
	void addToTopCornerNumber(int amount){
		topCornerButtonNumber += amount;
	}

	void update(int delta) {
		if (isFlashing) {
			timeSinceFlashing += delta;
			if (timeSinceFlashing >= FLASH_DURATION) {
				isFlashing = false;
			}
		}
		
		
		if(wasChargingLastUpdate && !isCharging()){
			flashOnce(3, Color.white, true);
		}
		
		wasChargingLastUpdate = isCharging();
	}

	void render(GUIContext container, Graphics g) {
		RenderUtil.renderIconWithText(g, background, icon, new Point(
				(int) iconRect.getX(), (int) iconRect.getY()), texts);
		mouseOverArea.render(container, g);
		if (!isActive) {
			RenderUtil.shadeIcon(g, iconRect, RenderUtil.TRANSPARENT_RED);
		}
		if (percentToShade > 0) {
			RenderUtil.shadePartOfIcon(g, iconRect, percentToShade, RenderUtil.TRANSPARENT_BLACK);
		}
		renderButtonChar(g);
		renderTopCornerButtonNumber(g);
		if (isFlashing) {
			highlight(container, g, flashLineWidth, flashColor, flashAlsoFill);
		}
	}

	private void renderButtonChar(Graphics g) {
		if(buttonChar != ' '){
			g.setFont(bigFont);
			g.setColor(new Color(0,0,0,0.7f));
			g.fillRect(iconRect.getX()+5, iconRect.getMaxY() - 25, 24, 22);
			g.setColor(Color.white);
			
			g.drawString("" + buttonChar, iconRect.getX() + 10,
					iconRect.getMaxY() - 24);
			g.setFont(HUD.NORMAL_FONT);
		}
	}
	
	private void renderTopCornerButtonNumber(Graphics g) {
		if(topCornerButtonNumber >= 2){
			g.setFont(bigFont);
			g.setColor(new Color(0,0,0,0.7f));
			g.fillRect(iconRect.getX()+40, iconRect.getMaxY() - 55, 24, 22);
			g.setColor(Color.white);
			
			g.drawString("" + topCornerButtonNumber, iconRect.getX() + 45,
					iconRect.getMaxY() - 55);
			g.setFont(HUD.NORMAL_FONT);
		}

	}

	void highlight(GUIContext container, Graphics g, int lineWidth, Color color, boolean alsoFill) {
		RenderUtil.highlightIcon(g, iconRect, lineWidth, color, alsoFill);
	}

	boolean isMouseOver(Input input) {
		int x = input.getMouseX();
		int y = input.getMouseY();
		return iconRect.contains(x, y);
	}

//	abstract int getPercentToShade();
	
	void setPercentToShade(int percentToShade){
		this.percentToShade = percentToShade;
	}
	
	boolean isCharging(){
		return percentToShade > 0 && percentToShade < 100;
	}

	void addListener(ComponentListener listener) {
		if (notifiesMouseClick) {
			mouseOverArea.addListener(listener);
		}
	}

	boolean hasMouseOverArea(MouseOverArea moa) {
		return mouseOverArea == moa;
	}
	
	boolean hasUnlockMouseOverArea(MouseOverArea moa){
		return false;
	}

	String getTooltip() {
		return tooltip;
	}

	public Point getUpperLeft() {
		return new Point((int) iconRect.getX(), (int) iconRect.getY());
	}

	void flashOnce(int lineWidth, Color color, boolean alsoFill) {
		flashLineWidth = lineWidth;
		flashColor = color;
		isFlashing = true;
		timeSinceFlashing = 0;
		flashAlsoFill = alsoFill;
	}
	
	boolean isBlank(){
		return false;
	}

	static class BlankButton extends Button {

		public BlankButton(GUIContext container, Rectangle iconRect) {
			super(container, iconRect, false, ResourceLoader.createBlankImage(
					10, 10), ' ',0, "");
		}
		
		boolean isBlank(){
			return true;
		}

		@Override
		protected boolean isCharging() {
			return false;
		}
	}

	public static class BasicButton extends Button {

		public BasicButton(GUIContext container, Rectangle iconRect,
				boolean notifiesListener, Image icon, String tooltip,
				String... texts) {
			super(container, iconRect, notifiesListener, icon, ' ', 0, tooltip,
					texts);
		}

		@Override
		protected boolean isCharging() {
			return false;
		}

	}

}
