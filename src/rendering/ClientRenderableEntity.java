package rendering;

import game.Direction;
import game.DirectionSpriteSet;
import game.Map;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ClientRenderableEntity implements RenderableEntity{

	private double pixelX;
	private double pixelY;
	private int horSpeed;
	private int vertSpeed;
	private boolean movementActive;
	private DirectionSpriteSet spriteSet;
	private Direction direction;
	private List<Animation> attachedAnimations = new ArrayList<Animation>();

	private boolean renderHealth;
	private int percentHealth;
	private boolean renderMana;
	private int percentMana;
	private boolean renderName;
	private String name;
	
	public ClientRenderableEntity(double pixelX, double pixelY, DirectionSpriteSet spriteSet, Direction direction, int percentHealth, int percentMana, String name){
		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.horSpeed = 0;
		this.vertSpeed = 0;
		this.spriteSet = spriteSet;
		this.direction = direction;
		this.percentHealth = percentHealth;
		this.percentMana = percentMana;
		
		renderHealth = percentHealth >= 0;
		renderMana = percentMana >= 0;
		renderName = name != null;
	}

	public static ClientRenderableEntity createHero(Point pixelLocation, DirectionSpriteSet spriteSet, Direction direction,
			int percentHealth, int percentMana) {
		ClientRenderableEntity hero = new ClientRenderableEntity(pixelLocation, spriteSet, direction);
		hero.renderHealth = true;
		hero.percentHealth = percentHealth;
		hero.renderMana = true;
		hero.percentMana = percentMana;
		hero.renderName = false;
		return hero;
	}

	public static ClientRenderableEntity createEnemy(Point pixelLocation, DirectionSpriteSet spriteSet, Direction direction,
			int percentHealth) {
		ClientRenderableEntity enemy = new ClientRenderableEntity(pixelLocation, spriteSet, direction);
		enemy.renderHealth = true;
		enemy.percentHealth = percentHealth;
		enemy.renderMana = false;
		enemy.renderName = false;
		return enemy;
	}

	public static ClientRenderableEntity createTower(Point pixelLocation, Animation animation, int percentHealth) {
		ClientRenderableEntity tower = new ClientRenderableEntity(pixelLocation, new DirectionSpriteSet(null, null, null, animation), Direction.UP);
		tower.renderHealth = true;
		tower.percentHealth = percentHealth;
		tower.renderMana = false;
		tower.renderName = false;
		return tower;
	}

	public static ClientRenderableEntity createNeutral(Point pixelLocation, DirectionSpriteSet spriteSet, Direction direction, String name) {
		ClientRenderableEntity neutral = new ClientRenderableEntity(pixelLocation, spriteSet, direction);
		neutral.renderHealth = false;
		neutral.renderMana = false;
		neutral.renderName = true;
		neutral.name = name;
		return neutral;
	}

	private ClientRenderableEntity(Point pixelLocation, DirectionSpriteSet spriteSet, Direction direction) {
		pixelX = pixelLocation.getX();
		pixelY = pixelLocation.getY();
		setMovementSpeed(0,0);
		this.spriteSet = spriteSet;
		this.direction = direction;
	}

	public void render(Graphics g) {
		spriteSet.getSprite(direction).draw((int) pixelX, (int) pixelY);
	}

	public void renderExtraVisuals(Graphics g) {
		renderName(g);
		renderStatBars(g);
		renderAnimations(g);
	}

	private void renderName(Graphics g) {
		if (renderName) {
			g.setColor(Color.black);
			if (HUD.NORMAL_FONT != null) {
				g.setFont(HUD.NORMAL_FONT);
			}
			g.drawString(name, (float) pixelX - 17, (float) pixelY - 19);
		}
	}

	private void renderStatBars(Graphics g) {
		if (renderHealth && renderMana) {
			RenderUtil.renderHealthBar(g, new Point((int) pixelX, (int) pixelY - 9), new Dimension(Map.getTileWidth(), 6), percentHealth / 100f, 1);
			RenderUtil.renderManaBar(g, new Point((int) pixelX, (int) pixelY - 2), new Dimension(Map.getTileWidth(), 6), percentMana / 100f, 1);
		} else if (renderHealth) {
			RenderUtil.renderHealthBar(g, new Point((int) pixelX, (int) pixelY - 2), new Dimension(Map.getTileWidth(), 6), percentHealth / 100f, 1);
		}
	}

	private void renderAnimations(Graphics g) {
		for (Animation animation : attachedAnimations) {
			Image image = animation.getCurrentFrame();
			g.drawImage(image, (float) (pixelX - image.getWidth() / 2), (float) (pixelY - image.getHeight() / 2));
		}
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	public void setAutoUpdateSprite(boolean autoUpdate){
		spriteSet.getSprite(direction).setAutoUpdate(autoUpdate);
	}
	
	public void setPercentHealth(int percentHealth){
		this.percentHealth = percentHealth;
	}
	
	public void setPercentMana(int percentMana){
		this.percentMana = percentMana;
	}
	

	/**
	 * Update location if movement is activated and speed != 0
	 * 
	 * @param delta
	 */
	public void update(int delta) {
		if (movementActive) {
			pixelX += delta * (double) horSpeed / 1000;
			pixelY += delta * (double) vertSpeed / 1000;
		}
	}
	
	private void setMovementSpeed(int horSpeed, int vertSpeed){
		this.horSpeed = horSpeed;
		this.vertSpeed = vertSpeed;
		this.movementActive = horSpeed != 0 || vertSpeed != 0;
	}

	@Override
	public void setLocationAndSpeed(Point pixelLocation, Point movementSpeed) {
		setMovementSpeed(movementSpeed.x, movementSpeed.y);
		pixelX = pixelLocation.x;
		pixelY = pixelLocation.y;
	}

}
