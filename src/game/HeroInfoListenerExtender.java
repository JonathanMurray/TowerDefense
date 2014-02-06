package game;

import game.objects.HeroStat;
import applicationSpecific.AbilityType;
import applicationSpecific.ItemType;

public class HeroInfoListenerExtender implements HeroInfoListener{



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
	public void heroUsedAbility(AbilityType ability, int timeUntilCanUseAgain) {
		// TODO Auto-generated method stub
		
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
	public void heroResurrected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void heroIsNowInRangeOfVendor(boolean isInRange) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void heroManaChanged(int oldMana, int newMana, int maxMana) {
		// TODO Auto-generated method stub
		
	}

}
