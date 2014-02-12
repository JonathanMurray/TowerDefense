package multiplayer;

import java.io.Serializable;

public class AddVisualEffectData implements MessageData{
	private static final long serialVersionUID = 1L;
	int id;
	int pixelX;
	int pixelY;
	int horPixelsPerSec;
	int verPixelsPerSec;
	int animationId;
	
	public AddVisualEffectData(int id, int pixelX, int pixelY, int horPixelsPerSec, int verPixelsPerSec, int animationId) {
		super();
		this.id = id;
		this.pixelX = pixelX;
		this.pixelY = pixelY;
		this.horPixelsPerSec = horPixelsPerSec;
		this.verPixelsPerSec = verPixelsPerSec;
		this.animationId = animationId;
	}
}
