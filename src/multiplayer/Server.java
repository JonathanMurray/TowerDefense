package multiplayer;

import game.OfflinePlayerInputHandler;
import game.ServerGame;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import messages.IntArrayMessageData;
import messages.IntArraysMessageData;
import messages.Message;
import messages.MessageType;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import rendering.HUD;

public class Server{

	public static final int PORT = 9136;
	NioDatagramAcceptor acceptor;
	ServerGame game;
	private boolean setupIsDone = false;
	private boolean connectedToClients = false;
	private boolean clientsAreReadyToGo = false;
	List<IoSession> clientSessions = new ArrayList<IoSession>();
	private HUD hud;
	
	private List<Message> bufferedSetupMessagesForClients = new ArrayList<Message>();

	public Server() throws IOException {
		
		acceptor = new NioDatagramAcceptor();
		
		System.out.println("local address: " + acceptor.getLocalAddress()); //TODO
		System.out.println("sessionConfig: " + acceptor.getSessionConfig());
		
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		acceptor.setHandler(new ServerIoHandler());
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.bind(new InetSocketAddress(PORT));
		System.out.println(acceptor);
		
		try {
			game = new ServerGame(this);
			AppGameContainer container = new AppGameContainer(game);
			System.out.println("server. before container.start()");
			container.start();
			System.out.println("server. after container.start()");
		} catch (SlickException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		
	}
	
	public void notifyHUDCreated(HUD hud){
		this.hud = hud;
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
			System.out.println("session opened: " + session);
			if(clientSessions.contains(session)){
				throw new IllegalStateException();
			}
			clientSessions.add(session);
			connectedToClients = true;
			if(setupIsDone){
				System.out.println("NOTIFY CLIENTS SETUP DONE NOW");
				for(IoSession s : clientSessions){
					s.write(new Message(MessageType.PLAYER_AND_HERO_SETUP_DONE, null));
				}
			}
		}

		@Override
		public void messageReceived(IoSession session, Object objectMessage) throws Exception {
			Message message = (Message) objectMessage;
			//System.out.println("received: "  + message);
			
			if(message.type == MessageType.CLIENT_READY){
				System.out.println("SERVER RECEIVED CLIENT READY");
				game.setAllowedToRun(true);
				clientsAreReadyToGo = true;
				for(Message savedMessage : bufferedSetupMessagesForClients){
					sendMessageToClients(savedMessage);
				}
				return;
			}
			
			if(!clientsAreReadyToGo){
				bufferedSetupMessagesForClients.add(message);
				return;
			}
			
			switch(message.type){
			
			case CLIENT_PRESSED_KEYS:
				IntArraysMessageData d1 = (IntArraysMessageData)message.data;
				OfflinePlayerInputHandler.handleKeyboardInput(d1.array1, d1.array2, hud);
				break;
			case CLIENT_PRESSED_LEFT_MOUSE:
				IntArrayMessageData d2 = (IntArrayMessageData)message.data;
				OfflinePlayerInputHandler.handleLeftMousePressed(d2.array[0], d2.array[1], hud);
				break;
			case CLIENT_PRESSED_RIGHT_MOUSE:
				IntArrayMessageData d3 = (IntArrayMessageData)message.data;
				OfflinePlayerInputHandler.handleRightMousePressed(d3.array[0], d3.array[1], hud);
				break;
			case ADD_VISUAL_EFFECT:
				break;
			case ITEM_WAS_ADDED:
				break;
			case ITEM_WAS_REMOVED:
				break;
			case MONEY_WAS_UPDATED:
				break;
			case PLAYER_AND_HERO_SETUP_DONE:
				break;
			case PLAYER_LIFE_WAS_UPDATED:
				break;
			case REMOVE_CLIENT_ENTITY:
				break;
			case TOWER_WAS_ADDED:
				break;
			case TOWER_WAS_UNLOCKED:
				break;
			case UPDATE_PHYSICS:
				break;
			default:
				game.messageReceived(message);
			}
			
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
			System.out.println("IDLE " + session.getIdleCount(status));
		}
	}
	
	public void sendMessageToClients(Message message){
		if(!clientsAreReadyToGo){
			bufferedSetupMessagesForClients.add(message);
			return;
		}
		System.out.println("send to all clients: " + message);
		for(IoSession session : clientSessions){
			session.write(message);
		}
	}
	
	public void notifyClientsSetupDone(){
		//THIS SHOULD NOT CALL SENDMESSAEGETOCLIENTS
		//It would be blocked by the fact that clients are not ready for (normal) messages yet.
		
		if(connectedToClients){
			System.out.println("NOTIFY CLIENTS SETUP DONE NOW");
			for(IoSession session : clientSessions){
				session.write(new Message(MessageType.PLAYER_AND_HERO_SETUP_DONE, null));
			}
		}else{
			System.out.println("CANT NOTIFY CLIENTS NOW: WILL DO SOON.");
			setupIsDone = true;
		}
	}
}
