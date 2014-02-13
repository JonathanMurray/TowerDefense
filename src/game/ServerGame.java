package game;
import multiplayer.Server;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class ServerGame extends StateBasedGame implements Game{
	private Server server;

	public ServerGame(Server server){
		super("Server game");
		this.server = server;
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		GamePlayState gameplayState = new ServerGamePlayState(server);
		addState(new SplashScreenState(gameplayState, true));
		addState(gameplayState);
		enterState(OfflineGame.STATE_SPLASHSCREEN);
	}
}
