package game;

import game.buffs.Buff;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;
import game.objects.Hero;
import game.objects.HeroInfo;
import game.objects.HeroStat;
import game.objects.ItemOnGround;
import game.objects.NeutralUnit;
import game.objects.Projectile;
import game.objects.VisibleObject;
import java.util.ArrayList;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import rendering.HUD;


public class LightGamePlayStateTEST extends BasicGameState {
	private static ArrayList<VisibleObject> objectsToRender;
	private static HUD hud;
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {

	}
	
	@Override
	public int getID() {
		return OfflineGame.STATE_GAMEPLAY;
	}
	
	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game) throws SlickException {
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		Map.instance().render(g);

		renderVisibleObjects(g);

		hud.render(container, g);
	}
	
	private void handleSettingsInput(StateBasedGame game, GameContainer container) {
		//handleGraphicsScale(container); //TODO
//		handleSpeedModifying(container);
		
		if(container.getInput().isKeyPressed(Input.KEY_M)){
			Sounds.toggleMute();
		}
		
//		if(container.getInput().isKeyPressed(Input.KEY_P)){
//			enterPausedState(game, container);
//		}
	}
	
	private void renderVisibleObjects(Graphics g) {
		for (VisibleObject object : objectsToRender) {
			if (!(object instanceof AnimationBasedVisualEffect)) {
				object.render(g);
				
			}
		}
		for (VisibleObject object : objectsToRender) {
			if (object instanceof AnimationBasedVisualEffect) {
				object.render(g);
			}
		}
		for (VisibleObject object : objectsToRender) {
			object.renderExtraVisuals(g);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		// TODO Auto-generated method stub
		
	}

	

	

}
