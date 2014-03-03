package multiplayer;

import messages.MessageData;


public class UpdatePhysicsData implements MessageData{
	private static final long serialVersionUID = 1L;

	public int id;
	public int pixelX;
	public int pixelY;
	public int horPixelsPerSec;
	public int verPixelsPerSec;

	public UpdatePhysicsData(int id, int pixelX, int pixelY, int horPixelsPerSec, int verPixelsPerSec) {
		this.id = id;
		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.horPixelsPerSec = horPixelsPerSec;
		this.verPixelsPerSec = verPixelsPerSec;
	}
	
}
