package messages;


public class BoolMessageData implements MessageData{
	private static final long serialVersionUID = 1L;
	boolean value;

	public BoolMessageData(boolean value) {
		this.value = value;
	}
	
}
