package multiplayer;

import java.io.Serializable;

import messages.MessageData;


public class AddVisualEffectData implements MessageData{
	private static final long serialVersionUID = 1L;
	public int id;
	public int pixelX;
	public int pixelY;
	public int horPixelsPerSec;
	public int verPixelsPerSec;
	public int animationId;
	
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
