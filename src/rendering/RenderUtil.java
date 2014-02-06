package rendering;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.MouseOverArea;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.Path.Step;

public class RenderUtil {

	static final Color TRANSPARENT_BLACK = new Color(0, 0, 0, 0.5f);
	static final Color TRANSPARENT_RED = new Color(0.2f,0,0,0.7f);
	private static Font f = new Font("Verdana", Font.PLAIN, 12);
	private static TrueTypeFont font = new TrueTypeFont(f, true);
	
	public static void renderStatBar(Graphics g, Point topLeft ,Dimension dimension, Color background, Color filled, double amountFull, int frameWidth) {
		g.setColor(background);
		g.fillRect(topLeft.x, topLeft.y, dimension.width, dimension.height);
		g.setColor(filled);
		g.fillRect(
				topLeft.x + frameWidth,
				topLeft.y + frameWidth,
				Math.max(0, (int) ((double) dimension.width * amountFull)
						- frameWidth * 2), dimension.height - frameWidth * 2);
	}

	public static void renderHealthBar(Graphics g, Point topLeft, Dimension dimension, double amountFull, int frameWidth) {
		renderStatBar(g, topLeft, dimension, Color.black, Color.red, amountFull, frameWidth);
	}

	public static void renderManaBar(Graphics g, Point topLeft,Dimension dimension, double amountFull, int frameWidth) {
		renderStatBar(g, topLeft, dimension, Color.black, Color.blue,amountFull, frameWidth);
	}

	static void renderIconWithText(Graphics g, Image background,
			Image icon, Point location, String... texts) {
		g.drawImage(background, location.x, location.y);
		g.drawImage(icon, location.x, location.y);
		Rectangle iconRect = new Rectangle(location.x, location.y,
				background.getWidth(), background.getHeight());
		renderText(g, iconRect, texts);
	}

	static void renderIconWithText(Graphics g, Image icon,
			Point location, String... texts) {
		g.setColor(Color.darkGray);
		g.fillRect(location.x, location.y, icon.getWidth(), icon.getHeight());
		g.drawImage(icon, location.x, location.y);
		Rectangle iconRect = new Rectangle(location.x, location.y,
				icon.getWidth(), icon.getHeight());
		renderText(g, iconRect, texts);
	}

	private static void renderText(Graphics g, Rectangle iconRect,
			String... texts) {
		int textHeight = 20;
		int space = 3;
		g.setFont(font);
		for (int i = 0; i < texts.length; i++) {
			g.setColor(Color.darkGray);
			g.fillRect(iconRect.getX(), iconRect.getY() - (textHeight + space)
					* (i + 1) - 2, iconRect.getWidth(), textHeight);
			g.setColor(Color.white);
			g.drawString(texts[texts.length - 1 - i], iconRect.getX() + 2,
					iconRect.getY() - (textHeight + space) * (i + 1) - 1);
		}
		g.setFont(HUD.NORMAL_FONT);
	}

	static void highlightIcon(Graphics g, Rectangle iconRect, int lineWidth, Color color, boolean alsoFill) {
		if(alsoFill){
			Color transparentColor = new Color(color);
			transparentColor.a = 0.5f;
			g.setColor(transparentColor);
			g.fill(iconRect);
		}
		g.setColor(color);
		Rectangle rect = iconRect;
		g.draw(rect);
		for(int i = 0; i < lineWidth - 1; i++){
			rect = new Rectangle(rect.getX() - 1, rect.getY() - 1, rect.getWidth()+2, rect.getHeight()+2);
		}
		
	}

	static void shadeIcon(Graphics g, Rectangle iconRect, Color color) {
		g.setColor(color);
		g.fill(iconRect);
	}

	static void shadePartOfIcon(Graphics g, Rectangle iconRect,
			int percentShaded, Color color) {
		int height = (int) ((float) iconRect.getHeight()
				* (float) percentShaded / (float) 100);
		g.setColor(color);
		g.fillRect(iconRect.getX(), iconRect.getY(), iconRect.getWidth(),
				height);
	}

	

	static void renderPathString(Graphics g, Path path, int x, int y) {
		String s = "";
		if (path == null) {
			g.drawString("PATH == NULL", x, y);
			return;
		}
		for (int i = 0; i < path.getLength(); i++) {
			s += stepToString(path.getStep(i)) + " ";
		}
		g.drawString(s, x, y);
	}

	static String stepToString(Step step) {
		return "(" + step.getX() + "," + step.getY() + ")";
	}

	static Rectangle getRect(MouseOverArea mouseOverArea) {
		return new Rectangle(mouseOverArea.getX(), mouseOverArea.getY(),
				mouseOverArea.getWidth(), mouseOverArea.getHeight());
	}
}
