package messages;


public class IntArraysMessageData implements MessageData{
	private static final long serialVersionUID = 1L;
	public int[] array1;
	public int[] array2;
	public IntArraysMessageData(int[] array1, int[] array2) {
		this.array1 = array1;
		this.array2 = array2;
	}
}
