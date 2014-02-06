package xmlLoading;

import game.actions.ContextParameterMap;
import game.buffs.ActionWhenSpendMana;
import game.buffs.ArmorBuff;
import game.buffs.AttributeMultiplierBuff;
import game.buffs.Buff;
import game.buffs.BuffOutsideCombat;
import game.buffs.CompositeBuff;
import game.buffs.ContagiousBuff;
import game.buffs.EffectOnSelfWhenLowLife;
import game.buffs.LeechBuff;
import game.buffs.ReceiveMoreDamageBuff;
import game.buffs.RepeatActionBuff;
import game.buffs.RepeatEffectOnSelfBuff;
import game.buffs.ShieldBuff;
import game.buffs.StunImmunity;
import game.buffs.StunWithImmunity;

import org.newdawn.slick.Animation;
import org.w3c.dom.Element;

public class BuffFactory extends XML_Factory{
	
	static enum BuffId{
		ARMOR,
		ATTRIBUTE_MULTIPLIER,
		OUTSIDE_COMBAT,
		COMPOSITE,
		CONTAGIOUS,
		LEECH,
		RECEIVE_MORE_DAMAGE,
		REPEAT_ACTION,
		REPEAT_EFFECT_ON_SELF,
		SHIELD,
		SLOW_IMMUNITY,
		STUN_WITH_IMMUNITY,
		EFFECT_ON_SELF_WHEN_LOW_LIFE,
		ACTION_WHEN_SPEND_MANA;
	}
	
	public Buff createBuff(Element buffElement){
		ElementData data = getData(buffElement);
		currentAttributesMap = data.attributes;
		BuffId id = BuffId.valueOf(data.type);
		Animation animation = createAnimation(data.childElements);
		ContextParameterMap contextParameterMap = createContextParameterMap(data.childElements);
		switch(id){
		case ARMOR:
			return new ArmorBuff(read("amount"), (int)(readDouble("duration", 0)*1000), readStr("buffId"), animation);
			
		case ATTRIBUTE_MULTIPLIER:
			return new AttributeMultiplierBuff(readEntityAttr("attribute"), readDouble("multiplier"), (int)(readDouble("duration", 0)*1000), readStr("buffId"), animation);
			
		case OUTSIDE_COMBAT:
			return new BuffOutsideCombat(createSubBuff(data.childElements));
			
		case COMPOSITE:
			return new CompositeBuff(createSubBuffs(data.childElements));
			
		case CONTAGIOUS:
			return new ContagiousBuff((int)(readDouble("duration", 0)*1000), createSubBuff(data.childElements), readDouble("range"), readStr("buffId"),animation);
			
		case LEECH:
			return new LeechBuff((int) (readDouble("damageCooldown")*1000), (int) (readDouble("healingCooldown")*1000), (int)(readDouble("duration", 0)*1000), readStr("buffId"), animation);		
		
		case RECEIVE_MORE_DAMAGE:
			return new ReceiveMoreDamageBuff((int)(readDouble("duration", 0)*1000), readDouble("multiplier"), readStr("buffId"), animation);
			
		case REPEAT_ACTION:
			return new RepeatActionBuff(createSubAction(data.childElements), (int) (readDouble("cooldown")*1000), (int)(readDouble("duration", 0)*1000), readStr("buffId"), animation);

		case REPEAT_EFFECT_ON_SELF:
			return new RepeatEffectOnSelfBuff(createSubEffect(data.childElements), (int) (readDouble("cooldown")*1000), (int)(readDouble("duration", 0)*1000), readStr("buffId"), animation);
			
		case SHIELD:
			return new ShieldBuff(read("amount"), (int)(readDouble("duration", 0)*1000), readStr("buffId"), animation);
			
		case SLOW_IMMUNITY:
			return new StunImmunity(readStr("buffId"), (int)(readDouble("duration", 0)*1000), animation);
			
		case STUN_WITH_IMMUNITY:
			return new StunWithImmunity(readStr("buffId"), (int)(readDouble("duration")*1000), read("postStunImmunity"), animation);
			
		case EFFECT_ON_SELF_WHEN_LOW_LIFE:
			return new EffectOnSelfWhenLowLife(readStr("buffId"), read("healthThresholdPercent"), createSubEffect(data.childElements), animation, (int)(readDouble("duration", 0)*1000));
		
		case ACTION_WHEN_SPEND_MANA:
			return new ActionWhenSpendMana(readStr("buffId"), (int)(readDouble("duration", 0)*1000), read("numberOfTimes", 1), createSubAction(data.childElements), animation, contextParameterMap);
		}
		throw new IllegalArgumentException();
	}

}
