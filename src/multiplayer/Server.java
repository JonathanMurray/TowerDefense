package multiplayer;

import game.PlayerInputHandler;
import game.Player;
import game.ServerGame;
import game.objects.HeroInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import messages.ClientReady;
import messages.IntArrayMessageData;
import messages.IntArraysMessageData;
import messages.IntMessageData;
import messages.Message;
import messages.MessageType;
import messages.ServerSetupDone;
import messages.UserInputMessage;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import applicationSpecific.AbilityType;
import applicationSpecific.ItemType;
import applicationSpecific.TowerType;
import rendering.HUD;

public class Server{

	public static final int PORT = 9136;
	NioDatagramAcceptor acceptor;
	ServerGame game;
	private boolean setupIsDone = false;
	private boolean connectedToClients = false;
	private boolean clientsAreReady = false;
	List<IoSession> clientSessions = new ArrayList<IoSession>();
	
	private List<Message> bufferedSetupMessagesForClients = new ArrayList<Message>();

	public Server() throws IOException {
		
		acceptor = new NioDatagramAcceptor();
		
		System.out.println("local address: " + acceptor.getLocalAddress()); //TODO
		System.out.println("sessionConfig: " + acceptor.getSessionConfig());
		
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		acceptor.setHandler(new ServerIoHandler());
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.bind(new InetSocketAddress(PORT));
		System.out.println("NioDatagramAcceptor: " + acceptor);
		
		try {
			game = new ServerGame(this);
			AppGameContainer container = new AppGameContainer(game);
			System.out.println("Starting server game ...");
			container.start();
			System.out.println("Started server game.");
		} catch (SlickException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		
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
				System.out.println("NOTIFY NEW CLIENTS 'SETUP DONE' NOW");
				for(IoSession s : clientSessions){
					s.write(new Message(ServerSetupDone.SERVER_SETUP_DONE, null));
				}
			}
		}

		@Override
		public void messageReceived(IoSession session, Object objectMessage) throws Exception {
			Message message = (Message) objectMessage;
			//System.out.println("received: "  + message);
			
			if(message.type instanceof ClientReady){
				System.out.println("SERVER RECEIVED CLIENT READY");
				game.setAllowedToRun(true);
				clientsAreReady = true;
				for(Message savedMessage : bufferedSetupMessagesForClients){
					sendMessageToClients(savedMessage);
				}
				return;
			}
			
			if(!clientsAreReady){
				bufferedSetupMessagesForClients.add(message);
				return;
			}
			
			if(message.type instanceof UserInputMessage){
				game.messageReceivedFromClient(message);
			}
		}
		
		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
			System.out.println("IDLE " + session.getIdleCount(status));
		}
			
	}

	public void sendMessageToClients(Message message){
		if(!clientsAreReady){
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
		setupIsDone = true;
		if(connectedToClients){
			System.out.println("NOTIFY CLIENTS 'SETUP DONE' NOW");
			for(IoSession session : clientSessions){
				session.write(new Message(ServerSetupDone.SERVER_SETUP_DONE, null));
			}
		}else{
			System.out.println("CANT NOTIFY CLIENTS NOW: WILL DO SOON.");
//			setupIsDone = true;
		}
	}
}
