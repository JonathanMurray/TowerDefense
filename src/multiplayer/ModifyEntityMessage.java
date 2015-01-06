package multiplayer;

import messages.MessageData;


public class ModifyEntityMessage implements MessageData{
	private static final long serialVersionUID = 1L;

	public int id;
	public int pixelX;
	public int pixelY;
	public int horPixelsPerSec;
	public int verPixelsPerSec;

	public ModifyEntityMessage(int id, int pixelX, int pixelY, int horPixelsPerSec, int verPixelsPerSec) {
		this.id = id;
		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.horPixelsPerSec = horPixelsPerSec;
		this.verPixelsPerSec = verPixelsPerSec;
	}
	
}
