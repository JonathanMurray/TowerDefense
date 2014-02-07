package multiplayer;

import game.Game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

public class Server extends Game{
	
	public static final int PORT = 9135;
	
	NioDatagramAcceptor acceptor;
	
	
	public Server() throws IOException {
		super(false);
		acceptor = new NioDatagramAcceptor();
        acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ))));
        acceptor.setHandler(  new ServerIoHandler() );
        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );
        acceptor.bind( new InetSocketAddress(PORT) );
        System.out.println(acceptor);
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("Starting server...  (time = " + (System.currentTimeMillis() % 1000000) + ")");
		try{
			AppGameContainer container = new AppGameContainer(new Server());
			container.start();
		}catch(SlickException e){
			e.printStackTrace();
			System.exit(0);
		}
	}
}
