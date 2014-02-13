package multiplayer;

	import game.ClientGame;

import java.awt.Point;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;

import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import applicationSpecific.TowerType;

	public class Client{
		
		ClientGame game;
		
//		private static final String HOSTNAME = "192.168.1.3";
		private static final String HOSTNAME = "127.0.0.1";

		private static final long CONNECT_TIMEOUT = 3 * 1000L;

		private IoSession session;
		private NioDatagramConnector connector;

		public Client() throws SlickException{
			
			game = new ClientGame();
			AppGameContainer container = new AppGameContainer(game);
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
				System.out.println("client session = " + session);
				System.out.println("client. sessionOpnened");
				
				while(!game.isFinishedInit()){
					Thread.sleep(100);
				}
				session.write(new Message(MessageType.CLIENT_READY, null));
			}
	
			@Override
			public void messageReceived(IoSession session, Object message) {
				Message msg = (Message)message;
				System.out.println("server: " + msg);
				
				switch(msg.type){
				case ADD_VISUAL_EFFECT:
					AddVisualEffectData d1 = (AddVisualEffectData)msg.data;
					game.addVisualEffect(d1.id, new Point(d1.pixelX, d1.pixelY), d1.animationId, d1.horPixelsPerSec, d1.verPixelsPerSec);
					break;
				case UPDATE_PHYSICS:
					UpdatePhysicsData d2 = (UpdatePhysicsData)msg.data;
					game.setVisualEffectPhysics(d2.id, new Point(d2.pixelX, d2.pixelY), d2.horPixelsPerSec, d2.verPixelsPerSec);
					break;
				case CLIENT_READY:
					break;
				case ITEM_WAS_ADDED:
					break;
				case ITEM_WAS_REMOVED:
					break;
				case MONEY_WAS_UPDATED:
					break;
				case PLAYER_LIFE_WAS_UPDATED:
					break;
				case REMOVE:
					break;
				case TOWER_WAS_ADDED:
					IdMessageData d9 = (IdMessageData)msg.data;
					game.towerWasAdded(TowerType.values()[d9.id]);
					break;
				case TOWER_WAS_UNLOCKED:
					break;
				default:
					break;
				
				}
			}
	
			@Override
			public void exceptionCaught(IoSession session, Throwable cause) {
				cause.printStackTrace();
				session.close(true);
			}
		}
}
