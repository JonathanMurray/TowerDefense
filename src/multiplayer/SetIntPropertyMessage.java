package multiplayer;

import messages.MessageData;

public class SetIntPropertyMessage  implements MessageData{
	private static final long serialVersionUID = 1L;
	public int id;
	public int value;
	
	public SetIntPropertyMessage(int id, int property){
		this.id = id;
		this.value = property;
	}

}
