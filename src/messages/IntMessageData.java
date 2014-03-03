package messages;


public class IntMessageData implements MessageData{
	private static final long serialVersionUID = 1L;
	public int value;

	public IntMessageData(int id) {
		this.value = id;
	}	
}
