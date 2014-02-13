package xmlLoading;

import game.actions.effects.AoE_Effect;
import game.actions.effects.ChanceEffect;
import game.actions.effects.ChangeMana;
import game.actions.effects.CompositeEffect;
import game.actions.effects.CompositeEffectUntilFail;
import game.actions.effects.DrainLife;
import game.actions.effects.Effect;
import game.actions.effects.FaceTarget;
import game.actions.effects.GainCompleteSlowImmunity;
import game.actions.effects.GainCompleteStunImmunity;
import game.actions.effects.Healing;
import game.actions.effects.HeroDropItem;
import game.actions.effects.LoseHealth;
import game.actions.effects.ReceiveBuff;
import game.actions.effects.ReceiveSummonedMinion;
import game.actions.effects.ReversedRolesEffect;
import game.actions.effects.Stun;
import game.actions.effects.TeleportToTarget;

import org.w3c.dom.Element;

import applicationSpecific.EnemyType;
import applicationSpecific.ItemType;


public class EffectFactory extends XML_Factory{
	enum EffectId{
		AOE_EFFECT,
		COMPOSITE,
		COMPOSITE_UNTIL_FAIL,
		LOSE_HEALTH,
		DRAIN_LIFE,
		CHANGE_MANA,
		GAIN_SLOW_IMMUNITY,
		GAIN_STUN_IMMUNITY,
		HEALING,
		RECEIVE_BUFF,
		STUN,
		CHANCE_EFFECT,
		REVERSED_ROLES,
		RECEIVE_SUMMONED_MINION,
		HERO_DROP_ITEM,
		TELEPORT_TO_TARGET,
		FACE_TARGET
	}

	public Effect createEffect(Element element){
		ElementData data = getData(element);
		currentAttributesMap = data.attributes;
		EffectId id = EffectId.valueOf(data.type);
		switch(id){
		case AOE_EFFECT:
			return new AoE_Effect(readDouble("range"), createSubEffect(data.childElements), readTeam("targetTeam"));
		
		case COMPOSITE:
			return new CompositeEffect(createSubEffects(data.childElements));
			
		case COMPOSITE_UNTIL_FAIL:
			return new CompositeEffectUntilFail(createSubEffects(data.childElements));
			
		case LOSE_HEALTH:
			return new LoseHealth(read("amount"), createAnimation(data.childElements), readBoolean("ignoreArmor", false));
		
		case DRAIN_LIFE:
			return new DrainLife(read("damageAmount"), read("healingAmount"), createAnimation(data.childElements));
		
		case CHANGE_MANA:
			return new ChangeMana(read("amount"), createAnimation(data.childElements));
			
		case GAIN_SLOW_IMMUNITY:
			return new GainCompleteSlowImmunity();
			
		case GAIN_STUN_IMMUNITY:
			return new GainCompleteStunImmunity();
			
		case HEALING:
			return new Healing(read("amount"), createAnimation(data.childElements));
			
		case RECEIVE_BUFF:
			return new ReceiveBuff(createSubBuff(data.childElements));
			
		case STUN:
			return new Stun(createAnimation(data.childElements), (int) (readDouble("duration")*1000), readDouble("stunChance", 1), read("postStunImmunity", 0), readStr("buffId", ""));
			
		case CHANCE_EFFECT:
			return new ChanceEffect(readDouble("chance"), createSubEffect(data.childElements));
			
		case REVERSED_ROLES:
			return new ReversedRolesEffect(createSubEffect(data.childElements));
			
		case RECEIVE_SUMMONED_MINION:
			return new ReceiveSummonedMinion(createAnimation(data.childElements), EnemyType.valueOf(readStr("minion")));
			
		case HERO_DROP_ITEM:
			return new HeroDropItem(ItemType.valueOf(readStr("item")));
			
		case TELEPORT_TO_TARGET:
			return new TeleportToTarget(createAnimation(data.childElements));
			
		case FACE_TARGET:
			return new FaceTarget();
		}
		return null;
	}
}
