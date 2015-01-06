package game;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class OfflineGame extends StateBasedGame implements Game{

	
	private static final String GAME_TITLE = "Slick TD";
	public static final int STATE_SPLASHSCREEN = 0;
	public static final int STATE_GAMEPLAY = 1;
	public static final int STATE_PAUSED = 2;
	
	private boolean render;
	
	public OfflineGame(boolean render) {
		super(GAME_TITLE);
		this.render = render;
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		GamePlayState gameplayState = new OfflineGamePlayState(render);
		addState(new SplashScreenState(gameplayState, render));
		addState(gameplayState);
		addState(new PausedState());
		enterState(STATE_SPLASHSCREEN);
		
	}
	
	public static void loseGame() {
		System.out.println("YOU LOST THE GAME");
	}

	public static void winGame() {
		System.out.println("YOU WON THE GAME");
	}
	
	

	
}
