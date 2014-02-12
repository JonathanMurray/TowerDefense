package multiplayer;

import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	MessageType type;
	MessageData data;
	public Message(MessageType messageType, MessageData messageData) {
		super();
		this.type = messageType;
		this.data = messageData;
	}
	
	public String toString(){
		return type.toString() + data;
	}
	
}
