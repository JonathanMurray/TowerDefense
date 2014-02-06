package game.objects.enemies;

import game.Attack;
import game.Game;
import game.Game.Team;
import game.DeathListener;
import game.GamePlayState;
import game.EntityHealthListener;
import game.Map;
import game.Player;
import game.RangedAttack;
import game.ResourceLoader;
import game.Waves.LaneType;
import game.objects.AnimationBasedVisualEffect;
import game.objects.Entity;
import game.objects.HeroInfo;
import game.objects.SeekerUnit;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;


import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import applicationSpecific.EnemyType;

public class Enemy extends SeekerUnit {

	private StateMachine stateMachine;
	private LaneType laneOrigin;
	private int moneyRewarded;
	private int experienceRewarded;
	private Attack attack;
	private int aggroRange;
	private int timeSinceAttacked;
	private String mouseOverText;
	
	private List<Entity> minions = new ArrayList<Entity>();
	private int maxNumMinions = 2; //-1 means boundless number of minions
	
	public void receiveMinion(EnemyType minionType, Point location){
		if(maxNumMinions >= 0 && minions.size() >= maxNumMinions){
			return;
		}
		Enemy summon = Enemy.constructEnemyAtLocation(minionType,isInTDMode() ? LaneType.towerLane : LaneType.heroLane,location);
		minions.add(summon);
		summon.addDeathListener(new DeathListener() {
			public void entityDied(Entity entity, boolean wasKilled) {
				minions.remove(entity);
			}
		});
		GamePlayState.addEnemy(summon);
	}
	
	public boolean canHaveMoreMinions(){
		boolean unbounded = maxNumMinions < 0;
		return minions.size() < maxNumMinions || unbounded;
	}

	public static Enemy constructEnemy(EnemyType type, LaneType laneOrigin) {
		return constructEnemyAtLocation(type, laneOrigin, getSpawnLocation(laneOrigin));
	}
	
	public static Enemy constructEnemyAtLocation(EnemyType type, LaneType laneOrigin, Point location){
		EnemyData statsForThisType = Game.getEnemyData(type);
		return new Enemy(statsForThisType, laneOrigin, location);
	}

	private static Point getSpawnLocation(LaneType laneOrigin) {
		if (laneOrigin == LaneType.heroLane) {
			return Map.instance().getHeroEnemiesStartPoint();
		} else {
			return Map.instance().getTDStartPoint();
		}
	}

	private Enemy(EnemyData stats, LaneType laneOrigin, Point spawnLocation) {
		super(stats.maxHealth, stats.armor, stats.spriteSet, stats.idleFrameIndex,
				stats.movementCooldown, spawnLocation, stats.deathSound, stats.stunDurationMultiplier);

		this.laneOrigin = laneOrigin;
		setupData(stats);
		stateMachine = new StateMachine(this, laneOrigin);
		mouseOverText = "DPS:    " + stats.getAttackCopy().getAverageDPS() + "\n" + 
						"Health: " + stats.maxHealth + "\n" + 
						"Armor:  " + Math.round(stats.armor) + "\n" +
						stats.tinyDescription;
		maxNumMinions = stats.maxNumMinions;
		if(stats.isImmuneToSlow){
			setCompletelyImmuneToSlow();
		}
		if(stats.isImmuneToStun){
			setCompletelyImmuneToStun();
		}
	}

	private void setupData(EnemyData stats) {
		moneyRewarded = stats.moneyRewarded;
		experienceRewarded = stats.experienceRewarded;
		attack = stats.getAttackCopy();
		aggroRange = stats.aggroRange;
		if(stats.behaviourBuff != null){
			receiveBuff(stats.behaviourBuff.getCopy());
		}
		
	}
	
	public String getMouseOverText(){
		return mouseOverText;
	}

	boolean isRangedAttacker() {
		return attack instanceof RangedAttack;
	}

	public boolean isInTDMode() {
		if (stateMachine == null) {// only happens very early, before it is
									// initialized
			return laneOrigin == LaneType.towerLane;
		}
		return stateMachine.isInTDMode();
	}

	public LaneType getLaneOrigin() {
		return laneOrigin;
	}

	@Override
	public void die(boolean wasKilled) {
		super.die(wasKilled);
		if (wasKilled) {
			Player.INSTANCE.gainMoney(moneyRewarded);
			HeroInfo.INSTANCE.gainExperience(experienceRewarded);
		}
		for(Entity minion : minions){
			minion.dieWithoutNotifyingListeners();
		}
	}
	
	

	@Override
	public void update(int delta) {
		super.update(delta);
		timeSinceAttacked += delta;
		stateMachine.update(delta);
		if (!isStunned()) {
			attack.update(delta);
		}
	}

	@Override
	protected void handlePathIsBlocked() {
		stayAtSameLocation();
		stateMachine.handlePathIsBlocked();
	}

	@Override
	protected void handleEndOfPath() {
		stopMoving();
		stateMachine.handleEndOfPath();
	}

	double getAttackRange() {
		return attack.getRange();
	}

	int getAggroRange() {
		return aggroRange;
	}

	void enterCastle() {
		die(false);
		Player.INSTANCE.loseLife(1);
	}

	void tryToAttackTarget(Entity target) {
		if (target == null) {
			throw new IllegalArgumentException();
		}
		boolean withinDistance = isLocationWithinDistance(target.getLocation(),
				attack.getRange());
		if (attack.isReady() && withinDistance && !isStunned()) {
			attackTarget(target);
		}
	}

	private void attackTarget(Entity target) {
		attack.attackTarget(this, target, totalDamageMultiplier);
		boolean movingAwayFromTarget = isSeeking()
				&& !isTargetWithinAttackRangeOf(target, getPathDestination());
		if (movingAwayFromTarget) {
			stopMoving();
		}
		timeSinceAttacked = 0;
	}

	private boolean isTargetWithinAttackRangeOf(Entity target,
			Point attackLocation) {
		return attackLocation.distanceSq(target.getLocation()) <= Math.pow(
				attack.getRange(), 2);
	}

	@Override
	public void renderExtraVisuals(Graphics g) {
		super.renderExtraVisuals(g);
		if (attack.attackedRecently()) {
			g.setColor(Color.red);
			Point location = getPixelLocation();
			
			g.drawRect(location.x - 1, location.y - 1, Map.getTileWidth() + 2, Map.getTileHeight() + 2);
			g.drawRect(location.x - 2, location.y - 2, Map.getTileWidth() + 4, Map.getTileHeight() + 4);
		}
	}

	public void DEBUGPingLocation(Point location) {
		Point effectLocation = new Point(location.x * Map.getTileWidth() + 20,
				location.y * Map.getTileHeight() + 20);
		GamePlayState.addSpecialEffect(new AnimationBasedVisualEffect(effectLocation, ResourceLoader
				.createTileScaledAnimation(false, 100, "bubble2.png",
						"bubble.png")));
	}

	@Override
	public Team getTeam() {
		return Team.EVIL;
	}

	

}
