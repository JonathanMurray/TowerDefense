package multiplayer;

import messages.MessageData;

public class SetAutoUpdateSpriteMessage  implements MessageData{
	private static final long serialVersionUID = 1L;
	public int id;
	public boolean setAuto;
	
	public SetAutoUpdateSpriteMessage(int id, boolean setAuto){
		this.id = id;
		this.setAuto = setAuto;
	}

}
