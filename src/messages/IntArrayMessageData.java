package messages;

public class IntArrayMessageData implements MessageData{
	private static final long serialVersionUID = 1L;
	public int[] array;
	
	public IntArrayMessageData(int... array){
		this.array = array;
	}

}