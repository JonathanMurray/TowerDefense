package multiplayer;

import game.Game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

public class Server extends Game {

	public static final int PORT = 9135;
	NioDatagramAcceptor acceptor;

	public Server() throws IOException {
		super(false);
		acceptor = new NioDatagramAcceptor();
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		acceptor.setHandler(new ServerIoHandler());
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.bind(new InetSocketAddress(PORT));
		System.out.println(acceptor);
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Starting server...  (time = " + (System.currentTimeMillis() % 1000000) + ")");
		try {
			AppGameContainer container = new AppGameContainer(new Server());
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	private class ServerIoHandler extends IoHandlerAdapter {
		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			cause.printStackTrace();
		}

		public void sessionOpened(IoSession session) {
			System.out.println("session opened: " + session);
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
}
