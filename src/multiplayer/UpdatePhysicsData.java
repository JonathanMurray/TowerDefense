package multiplayer;

public class UpdatePhysicsData implements MessageData{
	private static final long serialVersionUID = 1L;

	int id;
	int pixelX;
	int pixelY;
	int horPixelsPerSec;
	int verPixelsPerSec;

	public UpdatePhysicsData(int id, int pixelX, int pixelY, int horPixelsPerSec, int verPixelsPerSec) {
		this.id = id;
		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.horPixelsPerSec = horPixelsPerSec;
		this.verPixelsPerSec = verPixelsPerSec;
	}
	
}
