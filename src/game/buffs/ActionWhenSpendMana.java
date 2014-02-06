package game.buffs;

import game.Game;
import game.HeroInfoListener;
import game.actions.Action;
import game.actions.ContextParameterMap;
import game.actions.ContextVariable;
import game.actions.Parameters;
import game.objects.Entity;
import game.objects.HeroInfo;
import game.objects.HeroStat;

import org.newdawn.slick.Animation;

import applicationSpecific.AbilityType;
import applicationSpecific.ItemType;

public class ActionWhenSpendMana extends Buff implements HeroInfoListener{

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
	public void abilityWasReplacedByNew(AbilityType oldAbility, AbilityType newAbility) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void abilityWasAdded(AbilityType newAbility) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void heroUsedAbility(AbilityType ability, int timeUntilCanUseAgain) {
		System.out.println("ActionWhenSPendMana.heroUsedAbility");
		int manaCost = Game.getAbilityData(ability).manaCost;
		Parameters parameters = contextParameterMap.getParameters(ContextVariable.MANA_CHANGE_AMOUNT, - manaCost);
		carrier.performAction(action, parameters);
		timesLeft --;
		if(timesLeft <= 0){
			HeroInfo.INSTANCE.removeListener(this);
			carrier.loseBuff(this);
		}
		
	}

	@Override
	public void itemWasReplacedByNew(int oldItemIndex, ItemType newItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void itemWasEquipped(ItemType newItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void itemWasDropped(int itemIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void heroStatChanged(HeroStat stat, int newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void heroDied(int timeUntilResurrection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void heroResurrected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void numStatpointsChanged(int numAvailableStatpoints) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void heroHealthChanged(int newHealth, int maxHealth) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void heroUsedItem(int timeUntilCanUseAgain) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void heroManaChanged(int oldMana, int newMana, int maxMana) {
		
		
	}

	@Override
	public void heroIsNowInRangeOfVendor(boolean isInRange) {
		// TODO Auto-generated method stub
		
	}
	
}
