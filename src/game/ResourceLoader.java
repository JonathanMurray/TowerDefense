package game;


import java.awt.Dimension;
import java.util.HashMap;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;
import org.newdawn.slick.opengl.EmptyImageData;

import applicationSpecific.Paths;

public class ResourceLoader {
	
	public static final int DEFAULT_FRAME_DURATION = 200;
	
	private static HashMap<Dimension, Image> BLANK_IMAGES = new HashMap<>();
	static{
		BLANK_IMAGES.put(new Dimension(64, 18), new Image(new EmptyImageData(64, 18)));
		BLANK_IMAGES.put(new Dimension(10, 10), new Image(new EmptyImageData(10,10)));
	}

	public static SoundWrapper createSound(String soundRef, float volume) {
		String path = Paths.SOUNDS_FILEPATH + soundRef;
		try {
			Sound s = new Sound(path);
			return new SoundWrapper(s, volume);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}

	public static SoundWrapper createSound(String soundRef) {
		return createSound(soundRef, 1);
	}

	public static Music createMusic(String musicRef) {
		String path = Paths.SOUNDS_FILEPATH + musicRef;
		try {
			return new Music(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Image createImage(String imgRef) {
		String path = Paths.IMAGES_FILEPATH + imgRef;
		try {
			return new Image(path);
		} catch (Exception e) {
			System.out.println("exception for imgRef=" + imgRef);
			e.printStackTrace();
			System.exit(0);
		}
		System.exit(0);
		return null;
	}

	public static Image createScaledImage(String imgRef, int width, int height) {
		
		Image img = createImage(imgRef);
		return img.getScaledCopy(width, height);
	}

	public static Image createBlankImage(int width, int height) {
//		System.out.println("createBlankImage" + " " + width + "," + height);//TODO
//		return new Image(new EmptyImageData(width, height));
		if(!BLANK_IMAGES.containsKey(new Dimension(width, height))){
			System.err.println("createBlankImage(" +  width + " , " + height + ").   invalid arguments.");
			return BLANK_IMAGES.values().iterator().next();
		}
		return BLANK_IMAGES.get(new Dimension(width, height));
	}

	public static Animation createAnimation(boolean pingPong,
			String... imageRefs) {
		return createAnimation(pingPong, DEFAULT_FRAME_DURATION, imageRefs);
	}

	public static Animation createAnimation(boolean pingPong, int duration,
			String... imageRefs) {
		
		Image[] images = new Image[imageRefs.length];
		for (int i = 0; i < imageRefs.length; i++) {
			try {
				images[i] = new Image(Paths.IMAGES_FILEPATH + imageRefs[i]);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error loading " + imageRefs[i]);
				System.exit(0);
			}
		}

		Animation a = new Animation(images, duration, true); // auto update
		a.setPingPong(pingPong);
		return a;
	}
	
	public static Animation createScaledAnimation(boolean pingPong, int width,
			int height, String... imageRefs){
		
		return createScaledAnimation(pingPong, width, height, DEFAULT_FRAME_DURATION, imageRefs);
	}

	public static Animation createScaledAnimation(boolean pingPong, int width,
			int height, int duration, String... imageRefs) {
		
		Image[] images = new Image[imageRefs.length];
		for (int i = 0; i < imageRefs.length; i++) {
			try {
				images[i] = new Image(Paths.IMAGES_FILEPATH + imageRefs[i])
						.getScaledCopy(width, height);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Error loading " + imageRefs[i]);
				System.exit(0);
			}
		}

		Animation a = new Animation(images, duration, true); // auto update
		a.setPingPong(pingPong);
		return a;
	}

	public static Animation createTileScaledAnimation(boolean pingPong,
			String... imageRefs) {
		return createTileScaledAnimation(pingPong, DEFAULT_FRAME_DURATION, imageRefs);
	}

	public static Animation createTileScaledAnimation(boolean pingPong,
			int duration, String... imageRefs) {
		return createScaledAnimation(pingPong, Map.getTileWidth(),
				Map.getTileHeight(), duration, imageRefs);
	}

	public static Animation createBlinkingAnimation(Image image) {
		return createBlinkingAnimation(image, DEFAULT_FRAME_DURATION);
	}

	public static Animation createBlinkingAnimation(Image image, int duration) {
		Image blankImg = createBlankImage(image.getWidth(), image.getHeight());
		Image[] frames = new Image[] { image, blankImg };
		return new Animation(frames, duration, true);
	}

	public static DirectionSpriteSet createDirectionSpriteSet(boolean pingPong,
			String[] upImageRefs, String[] leftImageRefs,
			String[] downImageRefs, String[] rightImageRefs) {

		return new DirectionSpriteSet(ResourceLoader.createTileScaledAnimation(
				pingPong, downImageRefs),
				ResourceLoader.createTileScaledAnimation(pingPong,
						leftImageRefs),
				ResourceLoader.createTileScaledAnimation(pingPong,
						rightImageRefs),
				ResourceLoader.createTileScaledAnimation(pingPong, upImageRefs));
	}

	/**
	 * "images/entityUp1", "images/entityUp2" ...
	 */
	public static DirectionSpriteSet createDirectionEncodedDirectionSpriteSet(
			boolean pingPong, String fileBaseName,
			int numberOfImagesPerDirection, String imageFormat) {

		String[] upSpriteRefs = new String[numberOfImagesPerDirection];
		String[] leftSpriteRefs = new String[numberOfImagesPerDirection];
		String[] downSpriteRefs = new String[numberOfImagesPerDirection];
		String[] rightSpriteRefs = new String[numberOfImagesPerDirection];

		for (int i = 1; i <= numberOfImagesPerDirection; i++) {
			upSpriteRefs[i - 1] = fileBaseName + "Up" + Integer.toString(i)
					+ "." + imageFormat;
			leftSpriteRefs[i - 1] = fileBaseName + "Left" + Integer.toString(i)
					+ "." + imageFormat;
			downSpriteRefs[i - 1] = fileBaseName + "Down" + Integer.toString(i)
					+ "." + imageFormat;
			rightSpriteRefs[i - 1] = fileBaseName + "Right"
					+ Integer.toString(i) + "." + imageFormat;
		}

		return createDirectionSpriteSet(pingPong, upSpriteRefs, leftSpriteRefs,
				downSpriteRefs, rightSpriteRefs);
	}

	/**
	 * "images/entity11", "images/entity12" first number is direction. 1=down,
	 * 2=left, 3=right, 4=up
	 */
	public static DirectionSpriteSet createNumberEncodedDirectionSpriteSet(
			boolean pingPong, String baseName, int numberOfImagesPerDirection,
			String fileSuffix) {

		String[] upSpriteRefs = new String[numberOfImagesPerDirection];
		String[] leftSpriteRefs = new String[numberOfImagesPerDirection];
		String[] downSpriteRefs = new String[numberOfImagesPerDirection];
		String[] rightSpriteRefs = new String[numberOfImagesPerDirection];

		for (int i = 1; i <= numberOfImagesPerDirection; i++) {
			upSpriteRefs[i - 1] = baseName + "4" + Integer.toString(i) + "."
					+ fileSuffix;
			leftSpriteRefs[i - 1] = baseName + "2" + Integer.toString(i) + "."
					+ fileSuffix;
			downSpriteRefs[i - 1] = baseName + "1" + Integer.toString(i) + "."
					+ fileSuffix;
			rightSpriteRefs[i - 1] = baseName + "3" + Integer.toString(i) + "."
					+ fileSuffix;
		}

		return createDirectionSpriteSet(pingPong, upSpriteRefs, leftSpriteRefs,
				downSpriteRefs, rightSpriteRefs);
	}

}
