package game.actions.targetPickers;

import game.Entities;
import game.OfflineGamePlayState;
import game.actions.Parameters;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;
import game.objects.Team;

import org.newdawn.slick.Animation;



public class TargetAdjacentEntities implements TargetPicker{
	
	private Animation animation;
	private Team targetTeam;

	public TargetAdjacentEntities(Animation animation, Team targetTeam){
		this.animation = animation;
		this.targetTeam = targetTeam;
	}

	@Override
	public Entity[] pickTargets(Entity actor, boolean renderAnimation, Parameters parameters) {
		if(renderAnimation && animation != null){
			OfflineGamePlayState.addSpecialEffect(new AnimationBasedVisualEffect(actor, animation));
		}
		return Entities.getEntitiesAdjacentTo(actor.getLocation(), targetTeam).toArray(new Entity[0]);
	}
	
	
	
	
	

}
