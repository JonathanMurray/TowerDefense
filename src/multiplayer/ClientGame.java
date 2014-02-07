package multiplayer;

import game.ResourceLoader;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.newdawn.slick.Animation;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class ClientGame extends BasicGame{
	
	List<VisualEffect> visualEffects = new ArrayList<>();

	HashMap<Integer, Animation> animations = new HashMap<>();
	
	boolean isInitialized = false;
	
	public ClientGame() {
		super("client game");
	}
	
	void addVisualEffect(int id, Point pixelPosition, int animationId, int speedX, int speedY){
		visualEffects.add(new VisualEffect(pixelPosition, speedX, speedY, animations.get(1)));
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		for(VisualEffect visualEffect : visualEffects) {
			visualEffect.render(g);
		}
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		animations.put(1, ResourceLoader.createScaledAnimation(true, 200,200,"stun.png"));
		isInitialized = true;
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		// TODO Auto-generated method stub
		
	}

}
