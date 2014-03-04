package game.objects;

import game.Direction;
import game.DirectionSpriteSet;
import game.Map;
import game.ResourceLoader;
import game.SoundWrapper;
import game.UnitMovementListener;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.naming.OperationNotSupportedException;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.util.pathfinding.Mover;

public abstract class Unit extends Entity implements Mover {

	protected int previousX;
	protected int previousY;

	private boolean isUnderStun;
	private int timeRemainingStunned;
	private int numberOfSlowImmunities;
	private int numberOfStunImmunities;

	private boolean isCompletelyImmuneToStun = false;
	private boolean isCompletelyImmuneToSlow = false;

	// From 0 to 1.
	// 1 means this unit recieves full stuns, 0.25 means it will be stunned 25%
	// of the
	// intended stun duration
	private double stunDurationMultiplier = 1;

	private int baseMovementCooldown;
	private int movementCooldown;// TODO
	private int timeUntilAlignedToTile;
	private int timeSinceLastMove;
	private boolean isMidMovement;
	private Point pixelLocation;

	private int idleFrameIndex;

	private Direction direction = Direction.DOWN;// affects which sprite will be
													// shown.
	// is set correctly when moving

	private DirectionSpriteSet spriteSet;

	private List<UnitMovementListener> movementListeners = new ArrayList<UnitMovementListener>();

	public Unit(int maxHealth, double armor, DirectionSpriteSet spriteSet, int idleFrameIndex, int movementCooldown, Point location, SoundWrapper deathSound,
			double stunDurationMultiplier, EntityAttributeListener... attributeListeners) {
		super(maxHealth, armor, spriteSet.getSprite(Direction.DOWN), location, deathSound, attributeListeners);
		this.spriteSet = spriteSet.getCopy();

		this.baseMovementCooldown = movementCooldown;
		this.movementCooldown = movementCooldown;
		notifyListenersAttributeChanged(EntityAttribute.MOVEMENT_SPEED, baseMovementCooldown);
		this.timeSinceLastMove = movementCooldown + 10;
		this.idleFrameIndex = idleFrameIndex;
		currentSprite.setCurrentFrame(idleFrameIndex);
		timeUntilAlignedToTile = 0;
		previousX = location.x;
		previousY = location.y;
		updatePixelLocation();
	}

	public void setCompletelyImmuneToStun() {
		isCompletelyImmuneToStun = true;
	}

	/**
	 * WARNING. Actually REMOVES the speed multipliers, so if you later change
	 * back to "not immune" these effects are not magically reapplied.
	 * 
	 * @param completelyImmune
	 */
	public void setCompletelyImmuneToSlow() {
		isCompletelyImmuneToSlow = true;
		HashMap<String, Double> speedMultipliers = super.attributeMultipliers.get(EntityAttribute.MOVEMENT_SPEED);
		Iterator<Entry<String, Double>> speedMultipliersIt = speedMultipliers.entrySet().iterator();
		while (speedMultipliersIt.hasNext()) {
			if (speedMultipliersIt.next().getValue() < 1) {
				speedMultipliersIt.remove();
			}
		}
	}

	@Override
	public void addAttributeMultiplier(EntityAttribute attribute, String multiplierID, double multiplier) throws OperationNotSupportedException {
		if (isCompletelyImmuneToSlow && attribute == EntityAttribute.MOVEMENT_SPEED) {
			if (multiplier < 1) {
				return;
			}
		}
		super.addAttributeMultiplier(attribute, multiplierID, multiplier);
	}

	public void addMovementListener(UnitMovementListener listener) {
		movementListeners.add(listener);
	}

	public boolean isMidMovement() {
		return isMidMovement;
	}

	public final void teleportToLocation(int newX, int newY) {
		Map.instance().setLocationBlockedByEntity(x, y, false);
		x = newX;
		y = newY;
		Map.instance().setLocationBlockedByEntity(x, y, true);
		isMidMovement = false;
	}

	protected final void setLocation(int newX, int newY) {
		if (newX == x && newY == y) {
			throw new IllegalArgumentException("same location");
		}
		isMidMovement = true;
		previousX = x;
		previousY = y;
		x = newX;
		y = newY;
		Map.instance().setLocationBlockedByEntity(previousX, previousY, false);
		Map.instance().setLocationBlockedByEntity(x, y, true);

		for (UnitMovementListener movementListener : movementListeners) {
			movementListener.newLocation(x, y);
		}
		Direction dir = Direction.getDirection(x - previousX, y - previousY);
		renderableEntity.setLocationAndSpeed(getPixelLocation(), new Point((int) (dir.dx * Map.getTileWidth() / (float) movementCooldown * 1000f), (int) (dir.dy * Map.getTileHeight()
				/ (float) movementCooldown * 1000f)));
	}

	protected final void stayAtSameLocation() {
		notifyRenderableEntityNoMovement();
		previousX = x;
		previousY = y;
	}

	public void faceTarget(Entity target) {
		Point targetLocation = target.getLocation();
		try {
			setDirection(Direction.getDirection(targetLocation.x - x, targetLocation.y - y));
		} catch (IllegalArgumentException e) {
			System.err.println("Unit: " + this + "\nTarget: " + target);
			e.printStackTrace();
		}
	}

	public void setDirection(Direction newDirection) {
		direction = newDirection;
		renderableEntity.setDirection(newDirection);
	}

	public Direction getDirection() {
		return direction;
	}

	/**
	 * 0.5 = half speed 2 = double speed
	 * 
	 * @throws OperationNotSupportedException
	 */
	/*
	 * public boolean multiplySpeed(double speedMultiplier){
	 * if(!immuneToMovementImpairing){
	 * multiplySpeedIgnoreImmunity(speedMultiplier); return true; } return
	 * false; }
	 */

	/*
	 * public void addSpeedMultiplier(String multiplierID, double
	 * speedMultiplier){ speedMultipliers.put(multiplierID, speedMultiplier);
	 * updateMovementCooldown(); }
	 * 
	 * public void removeSpeedMultiplier(String multiplierID){
	 * speedMultipliers.remove(multiplierID); updateMovementCooldown(); }
	 */

	@Override
	protected void multiplyAttribute(EntityAttribute attribute, double totalMultiplier) throws OperationNotSupportedException {
		switch (attribute) {
		case MOVEMENT_SPEED:
			multiplyMovementSpeed(totalMultiplier);
			break;
		default:
			super.multiplyAttribute(attribute, totalMultiplier);
			break;
		}
	}

	private void multiplyMovementSpeed(double totalMultiplier) {
		int oldMovementCooldown = movementCooldown;
		movementCooldown = (int) ((double) baseMovementCooldown / totalMultiplier);
		timeUntilAlignedToTile = (int) ((double) movementCooldown * ((double) timeUntilAlignedToTile / (double) oldMovementCooldown));
		notifyListenersAttributeChanged(EntityAttribute.MOVEMENT_SPEED, getPrettifiedMovementSpeed());
	}

	public int getPrettifiedMovementSpeed() {
		return (int) ((double) 1000 / (double) movementCooldown * 100);
	}

	/**
	 * 0.5 = half speed 2 = double speed
	 */
	/*
	 * public void multiplySpeedIgnoreImmunity(double speedMultiplier){ int
	 * oldMovementCooldown = movementCooldown; movementCooldown = (int)(
	 * (double)movementCooldown / speedMultiplier); timeUntilAlignedToTile =
	 * (int)((double)movementCooldown *
	 * ((double)timeUntilAlignedToTile/(double)oldMovementCooldown)); }
	 */

	public int getTimeSinceLastMove() {
		return timeSinceLastMove;
	}

	@Override
	public Point getPixelLocation() {
		return new Point(pixelLocation);
	}

	public void addSlowImmunity() {
		numberOfSlowImmunities++;
		try {
			updateAttribute(EntityAttribute.MOVEMENT_SPEED);
		} catch (OperationNotSupportedException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void removeSlowImmunity() {
		numberOfSlowImmunities--;
		if (numberOfSlowImmunities < 0) {
			throw new IllegalStateException();
		}
		try {
			updateAttribute(EntityAttribute.MOVEMENT_SPEED);
		} catch (OperationNotSupportedException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * This effect lasts until changed. multiplier should be between 0 and 1. 1
	 * is default and means unit recieves full stun. 0.5 means it will be
	 * stunned for half the duration.
	 * 
	 * @param multiplier
	 *            should be between 0 and 1. low is good.
	 */
	public void setStunDurationMultiplier(double multiplier) {
		stunDurationMultiplier = multiplier;
	}

	public void addStunImmunity() {
		numberOfStunImmunities++;
	}

	public void removeStunImmunity() {
		numberOfStunImmunities--;
		if (numberOfStunImmunities < 0) {
			throw new IllegalStateException();
		}
	}

	/*
	 * public void setImmuneToMovementImpairing(boolean
	 * immuneToMovementImpairing){
	 * 
	 * //Don't want to revert this effect when temp. immune runs out
	 * temporaryImmuneToMovementImpairing = false;
	 * 
	 * this.immuneToMovementImpairing = immuneToMovementImpairing;
	 * if(immuneToMovementImpairing){ removeMovementImpairingBuffs();
	 * timeRemainingStunned = 0; } }
	 */

	/*
	 * public void receiveTemporaryImmuneToMovementImpairing(int duration){
	 * setImmuneToMovementImpairing(true); temporaryImmuneToMovementImpairing =
	 * true; timeLeftOnTemporaryImmune = duration; }
	 */
	/*
	 * private void removeMovementImpairingBuffs(){ for(Buff buff : buffs){
	 * if(buff.isMovementImpairing()){ loseBuff(buff); } } }
	 */

	/*
	 * @Override public void receiveBuff(Buff buff){
	 * if(buff.isMovementImpairing() && immuneToMovementImpairing){ return; }
	 * super.receiveBuff(buff); }
	 */

	public void stun(int stunDuration) {
		/*
		 * if(immuneToMovementImpairing){ //TODO Se upp med friendly stuns (de
		 * ska f�rst�s fortf. funka ..) return; }
		 */

		stunDuration *= stunDurationMultiplier;

		if (isUnderStun) {
			timeRemainingStunned = Math.max(timeRemainingStunned, stunDuration);
		} else {
			isUnderStun = true;
			timeRemainingStunned = stunDuration;
		}
	}

	public boolean isStunned() {
		return isUnderStun && numberOfStunImmunities == 0 && !isCompletelyImmuneToStun;
	}

	@Override
	public void update(int delta) {
		super.update(delta);
		handleStun(delta);
		timeSinceLastMove += delta;
		if (!isStunned()) {
			timeUntilAlignedToTile -= delta;
			if (timeUntilAlignedToTile <= 0) {
				handleChanceToMove();
			}
		}
		// handleTemporaryImmuneToMovementImpairing(delta);
		updatePixelLocation();
		currentSprite = spriteSet.getSprite(direction);

		// !midMovement wouldn't work here since it seems to be set to false for
		// a short moment right when passing a tile.
		// The animation would be reset every tile = choppy graphics
		if (timeSinceLastMove >= movementCooldown + 10) {
			currentSprite.setAutoUpdate(false);
			renderableEntity.setAutoUpdateSprite(false);
			currentSprite.setCurrentFrame(idleFrameIndex);
		} else {
			currentSprite.setAutoUpdate(true);
			renderableEntity.setAutoUpdateSprite(true);
		}

	}

	private void handleStun(int delta) {
		if (isUnderStun) {
			timeRemainingStunned -= delta;
			if (timeRemainingStunned <= 0) {
				isUnderStun = false;
				notifyStunFaded();
			}
		}
	}

	void notifyStunFaded() {

	}

	private void handleChanceToMove() {
		if (waitingForChanceToMove()) {
			tryToMove();
			boolean didMove = !(x == previousX && y == previousY);
			if (didMove) {
				direction = Direction.getDirection(x - previousX, y - previousY);
				timeSinceLastMove = 0;
				timeUntilAlignedToTile += movementCooldown;
			} else {
				timeUntilAlignedToTile = 0;
			}

		} else {
			isMidMovement = false;
			timeUntilAlignedToTile = 0;
			notifyRenderableEntityNoMovement();
		}
	}
	
	private void notifyRenderableEntityNoMovement(){
		renderableEntity.setLocationAndSpeed(getPixelLocation(), new Point(0,0));
	}

	private void updatePixelLocation() {
		if (isMidMovement()) {
			double partMovement = ((double) (movementCooldown - timeUntilAlignedToTile) / (double) movementCooldown);
			int dx = x - previousX;
			int dy = y - previousY;
			int midMovementX = (int) Math.round((((double) previousX + partMovement * (double) dx) * Map.getTileWidth()));
			int midMovementY = (int) Math.round((((double) previousY + partMovement * (double) dy) * Map.getTileHeight()));
			pixelLocation = new Point(midMovementX, midMovementY);
		} else {
			pixelLocation = new Point(x * Map.getTileWidth(), y * Map.getTileHeight());
		}
	}

	protected abstract boolean waitingForChanceToMove();

	/**
	 * @return True if movement was successful
	 */
	protected abstract boolean tryToMove();

	@Override
	public void render(Graphics g) {
		super.render(g);
		if (isStunned()) {

			g.drawImage(ResourceLoader.createImage("stun.png"), getPixelLocation().x, getPixelLocation().y - 40);
		}
	}

	@Override
	public void renderExtraVisuals(Graphics g) {
		super.renderExtraVisuals(g);
		if (isStunned()) {
			g.setColor(Color.orange);
			g.drawRect(getPixelLocation().x, getPixelLocation().y, Map.getTileWidth(), Map.getTileHeight());
		}
	}

}
