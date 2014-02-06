package game.actions.effects;

import game.GamePlayState;
import game.Map;
import game.Physics;
import game.actions.ParameterName;
import game.actions.Parameters;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;
import game.objects.enemies.Enemy;

import java.awt.Point;


import org.newdawn.slick.Animation;

import applicationSpecific.EnemyType;

public class ReceiveSummonedMinion implements Effect{
	
	private Animation animation;
	private EnemyType minionType;
	
	public ReceiveSummonedMinion(Animation animation, EnemyType minionType){
		this.animation = animation;
		this.minionType = minionType;
	}

	/**
	 * Return true if a minion was summoned, false otherwise.
	 */
	@Override
	public boolean execute(Entity actor, Entity target, Parameters context) {
		String contextMinionType = context.getString(ParameterName.MINION_TYPE, "");
		EnemyType minionType = contextMinionType.length() == 0? this.minionType : EnemyType.valueOf(contextMinionType);
		for (Point summonLocation : Physics.getAdjacentLocations(actor.getLocation())) {
			if (!Map.instance().blockedForEnemy((Enemy) actor, summonLocation.x, summonLocation.y)) {
				if(((Enemy)actor).canHaveMoreMinions()){
					if(animation != null){
						GamePlayState.addSpecialEffect(new AnimationBasedVisualEffect(actor.getPixelCenterLocation(), animation));
						GamePlayState.addSpecialEffect(new AnimationBasedVisualEffect(Physics.getPixelCenterLocation(summonLocation), animation));
					}
					((Enemy)actor).receiveMinion(minionType, summonLocation);
					return true;
				}
				
			}
		}
		return false;
	}

}
