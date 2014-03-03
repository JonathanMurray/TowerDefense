package messages;

import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	public MessageType type;
	public MessageData data;
	
	
	public Message(MessageType messageType){
		this(messageType, null);
	}
	
	public Message(MessageType messageType, MessageData messageData) {
		this.type = messageType;
		this.data = messageData;
	}
	
	public String toString(){
		return type.toString() + data;
	}
	
	public int getNthDataValue(int n){
		return ((IntArrayMessageData)data).array[n];
	}
	
	public int getIntDataValue(){
		return ((IntMessageData)data).value;
	}
	
	public boolean getBoolDataValue(){
		return ((BoolMessageData)data).value;
	}
	
	
	
}
