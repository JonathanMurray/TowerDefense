package multiplayer;

import java.util.Scanner;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.util.SessionAttributeInitializingFilter;

public class ServerIoHandler extends IoHandlerAdapter{
	
	
	@Override
    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception{
        cause.printStackTrace();
    }
	
	public void sessionOpened(IoSession session){
		System.out.println("session opened: " + session);
	}

    @Override
    public void messageReceived( IoSession session, Object message ) throws Exception{
        String str = message.toString();
        System.out.println(session.getId() + ": " + str);
        if(str.equals("READY")){
        	session.write("ADD 100 200");
        }
    }

    @Override
    public void sessionIdle( IoSession session, IdleStatus status ) throws Exception{
        System.out.println( "IDLE " + session.getIdleCount( status ));
    }
}
