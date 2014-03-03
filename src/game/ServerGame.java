package game;
import messages.Message;
import multiplayer.Server;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class ServerGame extends StateBasedGame implements Game{
	private Server server;
	private ServerGamePlayState gameplayState;

	public ServerGame(Server server){
		super("Server game");
		this.server = server;
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		gameplayState = new ServerGamePlayState(server);
		addState(new SplashScreenState(gameplayState, true));
		addState(gameplayState);
		enterState(OfflineGame.STATE_SPLASHSCREEN);
	}

	public void setAllowedToRun(boolean allowedToRun) {
		gameplayState.setAllowedToRun(allowedToRun);
	}
	
	public void messageReceived(Message message){
		gameplayState.messageReceived(message);
	}
}
