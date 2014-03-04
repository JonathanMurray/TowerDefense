package game;

import game.objects.HeroData.AbilityPair;
import game.objects.Projectile;
import game.objects.Tower;
import game.objects.VisualEffect;
import game.objects.enemies.Enemy;

import org.newdawn.slick.state.BasicGameState;

import applicationSpecific.TowerType;

public abstract class GamePlayState extends BasicGameState{
	abstract void setWaves(Waves waves);

	abstract public void offerHeroOneOfAbilities(AbilityPair abilityPair);

	public abstract boolean isHeroAliveAndCloseEnoughToMerchant();

	public abstract void giveRewardForWave(int i);

	public abstract void addEnemy(Enemy heroEnemy);

	public abstract void addSpecialEffect(VisualEffect animationBasedVisualEffect);

	public abstract void addTower(Tower tower, TowerType towerType);

	public abstract void addProjectile(Projectile p);

	public abstract void setAllowedToRun(boolean b);
}
