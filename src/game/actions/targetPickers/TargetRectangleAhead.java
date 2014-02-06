package game.actions.targetPickers;

import game.Game.Team;
import game.actions.ParameterName;
import game.actions.Parameters;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;
import game.objects.EntityNotFound;
import game.objects.Unit;
import game.Entities;
import game.GamePlayState;
import game.Physics;

import java.awt.Point;
import java.util.ArrayList;


import org.newdawn.slick.Animation;

public class TargetRectangleAhead implements TargetPicker {
	
	

	private int skipNFirstSquares; //TODO
	private int width;
	private int length;
	private Animation animation;
	private Team targetTeam;


	public TargetRectangleAhead(int skipNFirstSquares, int width, int length,
			Animation animation, Team targetTeam) {
		this.skipNFirstSquares = skipNFirstSquares;
		this.width = width;
		this.length = length;
		this.animation = animation;
		this.skipNFirstSquares = skipNFirstSquares;
		this.targetTeam = targetTeam;
	}


	@Override
	public Entity[] pickTargets(Entity actor, boolean renderAnimations, Parameters parameters) {
		int width = parameters.get(ParameterName.WIDTH, this.width);
		int length = parameters.get(ParameterName.LENGTH, this.length);
		int skipNFirstSquares = parameters.get(ParameterName.SKIP_N_FIRST_SQUARES, this.skipNFirstSquares);
		ArrayList<Entity> targets = new ArrayList<>();
		for (int stepsForward = skipNFirstSquares + 1; stepsForward <= skipNFirstSquares + length; stepsForward++) {
			for (int stepsRight = -(width-1)/2; stepsRight <= (width-1)/2; stepsRight++) {
				try {
					Point targetLocation = Physics.getRelativeLocation(actor.getLocation(), ((Unit)actor).getDirection(), stepsForward, stepsRight);
					if(renderAnimations && animation != null){
						GamePlayState.addSpecialEffect(new AnimationBasedVisualEffect( Physics.getPixelCenterLocation(targetLocation),animation));
					}	
					Entity target = Entities.getEntityOnLocation(targetLocation, targetTeam);
					targets.add(target);
				} catch (EntityNotFound e) {}
			}

		}
		return targets.toArray(new Entity[0]);
	}


}
