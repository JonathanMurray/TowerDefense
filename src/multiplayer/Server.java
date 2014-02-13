package multiplayer;

import game.ServerGame;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

public class Server{

	public static final int PORT = 9135;
	NioDatagramAcceptor acceptor;
	ServerGame game;
	List<IoSession> clientSessions = new ArrayList<IoSession>();

	public Server() throws IOException {
		
		try {
			game = new ServerGame(this);
			AppGameContainer container = new AppGameContainer(game);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		acceptor = new NioDatagramAcceptor();
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		acceptor.setHandler(new ServerIoHandler());
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.bind(new InetSocketAddress(PORT));
		System.out.println(acceptor);
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Starting server...  (time = " + (System.currentTimeMillis() % 1000000) + ")");
		new Server();
	}

	private class ServerIoHandler extends IoHandlerAdapter {
		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			cause.printStackTrace();
		}

		public void sessionOpened(IoSession session) {
			if(clientSessions.contains(session)){
				throw new IllegalStateException();
			}
			clientSessions.add(session);
		}

		@Override
		public void messageReceived(IoSession session, Object message) throws Exception {
			Message msg = (Message) message;
			if (msg.type == MessageType.CLIENT_READY) {
				session.write(new Message(MessageType.ADD_VISUAL_EFFECT, new AddVisualEffectData(1, 200, 200, 0, 100, 11)));
			}
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
			System.out.println("IDLE " + session.getIdleCount(status));
		}
	}
	
	public void sendMessageToClients(Message msg){
		for(IoSession session : clientSessions){
			session.write(msg);
		}
	}
}
