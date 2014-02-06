package game;

import org.newdawn.slick.Sound;

public class SoundWrapper {

	Sound sound;
	double volume;

	public SoundWrapper(Sound sound, double volume) {
		this.sound = sound;
		this.volume = volume;
		if (sound == null || volume < 0) {
			System.err.println("invalid args: " + sound + "  " + volume);
			System.exit(0);
		}
	}

}
