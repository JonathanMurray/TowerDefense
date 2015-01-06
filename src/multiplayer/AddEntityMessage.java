package multiplayer;

import messages.MessageData;


public class AddEntityMessage implements MessageData{
	private static final long serialVersionUID = 1L;
	public int id;
	public int pixelX;
	public int pixelY;
	public int spriteSetId; //The entity's sprite
	
	// negative value means the attribute is not used
	public int percentHealth;
	public int percentMana;
	
	//Empty string means not used
	public String name;
	
	public AddEntityMessage(int id, int pixelX, int pixelY, int spriteSetId, int percentHealth, int percentMana, String name) {
		this.id = id;
		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.spriteSetId = spriteSetId;
		this.percentHealth = percentHealth;
		this.percentMana = percentMana;
		this.name = name;
	}
}
