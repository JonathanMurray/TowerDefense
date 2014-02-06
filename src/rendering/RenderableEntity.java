package rendering;

import game.Direction;
import game.DirectionSpriteSet;
import java.awt.Graphics;

public class RenderableEntity {
	
	private double pixelX;
	private double pixelY;
	private int horSpeed;
	private int vertSpeed;
	private boolean movementActive;
	private DirectionSpriteSet spriteSet;
	private Direction direction;
	
	
	public RenderableEntity(int x, int y, DirectionSpriteSet spriteSet, Direction direction){
		this.spriteSet = spriteSet;
		this.direction = direction;
	}
	
	public void render(Graphics g){
		spriteSet.getSprite(direction).draw((int)pixelX, (int)pixelY);
	}
	
	public void setPixelLocation(int x, int y){
		pixelX = x;
		pixelY = y;
	}
	
	public void setDirection(Direction direction){
		this.direction = direction;
	}

	/**
	 * Speeds are given in "pixels per second"
	 * @param horSpeed horizontal pixel movement / s
	 * @param vertSpeed vertical pixel movement / s
	 */
	public void setMovementSpeed(int horSpeed, int vertSpeed){
		this.horSpeed = horSpeed;
		this.vertSpeed= vertSpeed;
		this.movementActive = horSpeed != 0 || vertSpeed != 0;
	}
	
	/**
	 * Update location if movement is activated and speed != 0
	 * @param delta
	 */
	public void update(int delta){
		if(movementActive){
			pixelX += delta * (double)horSpeed/1000;
			pixelY += delta * (double)vertSpeed/1000;
		}
	}
	
}
