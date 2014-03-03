package game.buffs;

import game.LoadedData;
import game.MessageListener;
import game.actions.Action;
import game.actions.ContextParameterMap;
import game.actions.ContextVariable;
import game.actions.Parameters;
import game.objects.Entity;
import game.objects.HeroInfo;
import messages.IntMessageData;
import messages.Message;
import messages.MessageType;

import org.newdawn.slick.Animation;

import applicationSpecific.AbilityType;

public class ActionWhenSpendMana extends Buff implements MessageListener{

	private int duration;
	private Action action;
	private int numberOfTimes;
	private int timesLeft;
	private Entity carrier;
	private ContextParameterMap contextParameterMap;

	public ActionWhenSpendMana(String id, int duration, int numberOfTimes, Action action, Animation animation, ContextParameterMap contextParameterMap) {
		super(id, animation);
		this.duration = duration;
		this.action = action;
		this.numberOfTimes = numberOfTimes;
		timesLeft = numberOfTimes;
		this.contextParameterMap = contextParameterMap;
		System.out.println(contextParameterMap);
	}

	@Override
	public void applyEffectOn(Entity carrier) {
		HeroInfo.INSTANCE.addListener(this);
		this.carrier = carrier;
	}

	@Override
	public void revertEffectOn(Entity carrier) {
		HeroInfo.INSTANCE.removeListener(this);
	}

	@Override
	protected void continuousEffect(Entity carrier, int delta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int getTotalDuration() {
		return duration;
	}

	@Override
	public Buff getCopy() {
		return new ActionWhenSpendMana(id, duration, numberOfTimes, action, getAnimationCopy(), contextParameterMap);
	}

	@Override
	public void messageReceived(Message message) {
		if(message.type == MessageType.HERO_USED_ABILITY){
			System.out.println("ActionWhenSPendMana.heroUsedAbility");
			AbilityType ability = AbilityType.values()[((IntMessageData)message.data).value];
			int manaCost = LoadedData.getAbilityData(ability).manaCost;
			Parameters parameters = contextParameterMap.getParameters(ContextVariable.MANA_CHANGE_AMOUNT, - manaCost);
			carrier.performAction(action, parameters);
			timesLeft --;
			if(timesLeft <= 0){
				HeroInfo.INSTANCE.removeListener(this);
				carrier.loseBuff(this);
			}
		}
	}

}
