package game;

import game.objects.HeroData.AbilityPair;

import org.newdawn.slick.state.BasicGameState;

public abstract class GamePlayState extends BasicGameState{
	abstract void setWaves(Waves waves);

	abstract public void offerHeroOneOfAbilities(AbilityPair abilityPair);

	public abstract boolean isHeroAliveAndCloseEnoughToMerchant();
}
