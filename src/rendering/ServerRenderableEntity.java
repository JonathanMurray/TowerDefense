package rendering;

import game.Direction;

import java.awt.Point;

import multiplayer.Server;

import org.newdawn.slick.Graphics;

public class ServerRenderableEntity implements RenderableEntity{
	
	private Server server;

	public ServerRenderableEntity(Server server){
		this.server = server;
	}

	@Override
	public void render(Graphics g) {}
	@Override
	public void renderExtraVisuals(Graphics g) {}


	@Override
	public void setDirection(Direction direction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAutoUpdateSprite(boolean autoUpdate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPercentHealth(int percentHealth) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPercentMana(int percentMana) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(int delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLocationAndSpeed(Point pixelLocation, Point movementSpeed) {
		// TODO Auto-generated method stub
		
	}

}
