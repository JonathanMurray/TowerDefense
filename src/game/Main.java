package game;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;

import applicationSpecific.MapEditorData;

public class Main {
	public static void main(String[] args){
		
		
		//TODO
		/*
		 * SUpposed to make executable jar-files work better,
		 * provides the library path to needed natives
		 */
		//System.setProperty("org.lwjgl.librarypath", new File(new File(System.getProperty("user.dir"), "native"), LWJGLUtil.getPlatformName()).getAbsolutePath());
		//System.setProperty("net.java.games.input.librarypath", System.getProperty("org.lwjgl.librarypath"));
		
		
		System.out.println("Starting...  (time = " + (System.currentTimeMillis() % 1000000) + ")");
		
		try{
//			ScalableGame game = new ScalableGame(new Game(), 2048, 1638);
			ScalableGame game = new ScalableGame(new Game(), 1440, 1296, true);
			AppGameContainer container = new AppGameContainer(game);
			container.setShowFPS(true);

//			container.setDisplayMode(MapEditorData.PIXEL_WIDTH,
//			MapEditorData.PIXEL_HEIGHT, false);
//			container.setDisplayMode(640, 480, true);
//			container.setDisplayMode(1280, 1024, true);
			container.setDisplayMode(container.getScreenWidth(), container.getScreenHeight(), true);
//			container.setDisplayMode(1280, 768, false);
			container.start();
		}catch(SlickException e){
			e.printStackTrace();
			System.exit(0);
		}
		
	}
}
