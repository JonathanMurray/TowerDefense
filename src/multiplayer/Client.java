package multiplayer;

import game.ClientGame;

import java.net.InetSocketAddress;

import messages.Message;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;

public class Client{
		
	ClientGame game;
	
	private static final String HOSTNAME = "192.168.1.3";
//	private static final String HOSTNAME = "127.0.0.1";

	private static final long CONNECT_TIMEOUT = 3 * 1000L;

	private IoSession session;
	private NioDatagramConnector connector;

	public Client() throws SlickException{
		
		game = new ClientGame(this);
		AppGameContainer container = new AppGameContainer(new ScalableGame(game, 1440, 1296, true));
		container.setDisplayMode(container.getScreenWidth(), container.getScreenHeight(), true);
		connector = new NioDatagramConnector();
		connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
		ProtocolCodecFactory codecFactory = new ObjectSerializationCodecFactory();
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(codecFactory));
		connector.setHandler(new ClientSessionHandler());

		
		while(true){
			try {
				ConnectFuture future = connector.connect(new InetSocketAddress(HOSTNAME, Server.PORT));
				future.awaitUninterruptibly();
				session = future.getSession();
				break;
			} catch (RuntimeIoException e) {
				System.err.println("Failed to connect.");
			}
		}
		
		container.start();
	}
	
	public static void main(String[] args) throws InterruptedException, SlickException {
		new Client();
	}
	


	private class ClientSessionHandler extends IoHandlerAdapter {

		@Override
		public void sessionOpened(IoSession session) throws InterruptedException {
			Client.this.session = session;
			System.out.println("session opnened: " + session);
		}

		@Override
		public void messageReceived(IoSession session, Object objectMessage) {
			Message message = (Message)objectMessage;
			game.messageReceived(message);
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) {
			cause.printStackTrace();
			session.close(true);
		}
	}
	
	public void sendMessageToServer(Message message){
		session.write(message);
	}
}
