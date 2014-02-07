package multiplayer;

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
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

	public class Client{
		
		ClientGame game;
		
		private static final String HOSTNAME = "192.168.1.3";

		private static final long CONNECT_TIMEOUT = 3 * 1000L;

		private IoSession session;
		private NioDatagramConnector connector;

		public Client() throws SlickException{
			
			game = new ClientGame();
			AppGameContainer container = new AppGameContainer(game);
			connector = new NioDatagramConnector();
			connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
			ProtocolCodecFactory codecFactory = new TextLineCodecFactory(Charset.forName("UTF-8"));
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
		

		class ClientSessionHandler extends IoHandlerAdapter {
	
			@Override
			public void sessionOpened(IoSession session) throws InterruptedException {
				Client.this.session = session;
				System.out.println("client session = " + session);
				System.out.println("client. sessionOpnened");
				
				while(!game.isInitialized){
					Thread.sleep(100);
				}
				session.write("READY");
			}
	
			@Override
			public void messageReceived(IoSession session, Object message) {
				
				String str = message.toString();
				System.out.println(str);
				Scanner sc = new Scanner(str);
				String command = sc.next();
				if(command.equals("ADD")){
					int pixelX = sc.nextInt();
					int pixelY = sc.nextInt();
					game.addVisualEffect(16, new Point(pixelX, pixelY), 62, 0, 0);
				}else if(command.equals("UPDATE")){
					
				}
			}
	
			@Override
			public void exceptionCaught(IoSession session, Throwable cause) {
				cause.printStackTrace();
				session.close(true);
			}
		}
}
