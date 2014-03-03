package game.actions.effects;

import game.GamePlayStateInstance;
import game.actions.ParameterName;
import game.actions.Parameters;
import game.buffs.StunWithImmunity;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;
import game.objects.Unit;

import java.util.Random;

import org.newdawn.slick.Animation;


public class Stun implements Effect{
	private Animation animation;
	private int duration;
	private double stunChance;
	private int postStunImmunity;
	private String stunBuffId;

	public Stun(Animation animation, int duration, double stunChance,int postStunImmunity, String stunBuffId) {
		this.animation = animation;
		this.duration= duration;
		this.stunChance = stunChance;
		
		this.postStunImmunity = postStunImmunity;
		this.stunBuffId = stunBuffId;
	}
	

	@Override
	public boolean execute(Entity actor, Entity target, Parameters context) {
		int duration = context.get(ParameterName.DURATION, this.duration);
		double stunChance = context.getDouble(ParameterName.STUN_CHANCE, this.stunChance);
		int postStunImmunity = context.get(ParameterName.POST_STUN_IMMUNITY, this.postStunImmunity);
		String stunBuffId = context.getString(ParameterName.STUN_BUFF_ID, this.stunBuffId);
		if(new Random().nextDouble() <= stunChance){
			if(postStunImmunity == 0){
				((Unit)target).stun(duration);
			}else{
				target.receiveBuff(new StunWithImmunity(stunBuffId, duration, postStunImmunity));
			}
		}
		if(animation != null){
			GamePlayStateInstance.INSTANCE.addSpecialEffect(new AnimationBasedVisualEffect(target, animation));
		}
		return true;
	}
}
