package game;


import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class PausedState extends BasicGameState{
	
	private static Image image;

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
		g.drawImage(image, 0, 0, new Color(1,1,1,0.2f));
		//g.drawImage(image, 0,0);
		g.setColor(Color.white);
		g.drawString("PAUSED", container.getWidth()/2-15, container.getHeight()/4);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if(container.getInput().isKeyPressed(Input.KEY_P)){
			System.out.println("enter gameplay again");
			game.enterState(Game.STATE_GAMEPLAY);
		}
	}

	@Override
	public int getID() {
		return Game.STATE_PAUSED;
	}

	public static void setImage(Image img) {
		image =  img;
	}

}
