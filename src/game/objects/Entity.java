package game.objects;

import game.DeathListener;
import game.EntityHealthListener;
import game.Map;
import game.Physics;
import game.SoundWrapper;
import game.Sounds;
import game.actions.Action;
import game.actions.Parameters;
import game.buffs.Buff;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import rendering.RenderUtil;
import rendering.OfflineRenderableEntity;
import rendering.RenderableEntity;

public abstract class Entity implements VisibleObject {

	
	protected RenderableEntity renderableEntity;
	
	protected int x;
	protected int y;
	private int baseMaxHealth;
	protected int maxHealth;
	protected int health;
	private double baseArmor;
	protected double armor;
	protected double totalDamageMultiplier = 1;

	public int getMultipliedDamage(int baseDamage) {
		return (int) ((double) baseDamage * totalDamageMultiplier);
	}

	protected HashMap<EntityAttribute, HashMap<String, Double>> attributeMultipliers = new HashMap<EntityAttribute, HashMap<String, Double>>();

	private ArrayList<EntityHealthListener> healthListeners = new ArrayList<EntityHealthListener>();
	private ArrayList<Object> listenersToBeRemoved = new ArrayList<Object>();
	private ArrayList<DeathListener> deathListeners = new ArrayList<DeathListener>();
	protected ArrayList<EntityAttributeListener> attributeListeners = new ArrayList<EntityAttributeListener>();
	private List<EntityActionListener> actionListeners = new ArrayList<EntityActionListener>();
	
	public ArrayList<Buff> buffs = new ArrayList<Buff>();
	private ArrayList<Buff> buffsToBeRemoved = new ArrayList<Buff>();
	private ArrayList<Buff> buffsToBeAdded = new ArrayList<Buff>();

	// sprite might be changed from subclass (1 sprite for each direction for
	// units)
	protected Animation currentSprite;

	private boolean alive = true;

	private SoundWrapper deathSound;

	public Entity(int maxHealth, double armor, Animation sprite, Point location, SoundWrapper deathSound, EntityAttributeListener... attributeListeners) {

		for (EntityAttributeListener listener : attributeListeners) {
			addAttributeListener(listener);
		}

		this.maxHealth = maxHealth;
		baseMaxHealth = maxHealth;
		notifyListenersAttributeChanged(EntityAttribute.MAX_HEALTH, maxHealth);
		health = maxHealth;
		this.armor = armor;
		baseArmor = armor;
		notifyListenersAttributeChanged(EntityAttribute.ARMOR, armor);
		this.currentSprite = sprite;
		x = location.x;
		y = location.y;

		this.deathSound = deathSound;

		for (EntityAttribute attribute : EntityAttribute.values()) {
			attributeMultipliers.put(attribute, new HashMap<String, Double>());
		}
	}
	
	protected void setRenderableEntity(RenderableEntity renderableEntity){
		this.renderableEntity = renderableEntity;
	}

	public abstract Team getTeam();

	public boolean containsPixelLocation(Point pixelLocation) {
		Rectangle spriteBounds = new Rectangle(getPixelLocation().x, getPixelLocation().y, currentSprite.getWidth(), currentSprite.getHeight());
		return spriteBounds.contains(pixelLocation);
	}
	
	public boolean performAction(Action action){
		return performAction(action, new Parameters());
	}

	public boolean performAction(Action action, Parameters parameters){
		boolean success = action.execute(this, parameters);
		for(EntityActionListener listener : actionListeners){
			listener.entityDidAction(this, action);
		}
		return success;
	}
	
	public boolean performActionWithoutNotifyingListeners(Action action){
		return performActionWithoutNotifyingListeners(action, new Parameters());
	}
	
	public boolean performActionWithoutNotifyingListeners(Action action, Parameters parameters){
		return action.execute(this, parameters);
	}
	
	public void addHealthListener(EntityHealthListener listener) {
		healthListeners.add(listener);
		listener.healthChanged(this, health, health);
	}

	public void removeHealthListener(EntityHealthListener listener) {
		listenersToBeRemoved.add(listener);
	}
	
	public void addActionListener(EntityActionListener listener){
		actionListeners.add(listener);
	}
	
	public void removeActionListener(EntityActionListener listener){
		listenersToBeRemoved.add(listener);
	}

	private void handleRemoveListeners() {
		for (Object listener : listenersToBeRemoved) {
			if (listener instanceof EntityHealthListener) {
				healthListeners.remove(listener);
			} else if (listener instanceof DeathListener) {
				deathListeners.remove(listener);
			} else if (listener instanceof EntityAttributeListener) {
				attributeListeners.remove(listener);
			} else if(listener instanceof EntityActionListener){
				actionListeners.remove(listener);
			} else {
				throw new IllegalStateException();
			}
		}
		listenersToBeRemoved.clear();
	}

	public void addDeathListener(DeathListener listener) {
		deathListeners.add(listener);
	}

	public void removeDeathListener(DeathListener listener) {
		listenersToBeRemoved.add(listener);
	}

	public void addAttributeListener(EntityAttributeListener listener) {
		attributeListeners.add(listener);
	}

	public void removeAttributeListener(EntityAttributeListener listener) {
		listenersToBeRemoved.add(listener);
	}

	@Override
	public void update(int delta) {
		for (Buff buff : buffs) {
			buff.update(this, delta);
		}
		
		if(renderableEntity != null){
			renderableEntity.update(delta);
		}

		handleAddBuffs();
		handleRemoveBuffs();
		handleRemoveListeners();
	}

	private void handleAddBuffs() {
		List<Buff> addedNow = new ArrayList<Buff>();
		for (Buff b : buffsToBeAdded) {
			buffs.add(b);
			addedNow.add(b);
		}
		buffsToBeAdded.clear();
		for (Buff b : addedNow) {
			b.applyEffectOn(this);
		}
	}

	private void handleRemoveBuffs() {
		List<Buff> removedNow = new ArrayList<Buff>();
		for (Buff b : buffsToBeRemoved) {
			buffs.remove(b);
			removedNow.add(b);
		}
		buffsToBeRemoved.clear();
		for (Buff b : removedNow) {
			b.revertEffectOn(this);
		}
	}

	// private void removeBuff(Buff buff){

	// Iterator<Buff> buffsIt = buffs.iterator();
	// while(buffsIt.hasNext()){
	// Buff b = buffsIt.next();
	// if(b.getID().equals(buff.getID())){
	// buffsIt.remove();
	// }
	// }
	// }

	public void receiveBuff(Buff buff) {
		if (buff == null) {
			throw new IllegalArgumentException();
		}

		if (buffs.contains(buff)) {
			if (buffsToBeRemoved.contains(buff)) {
				buffsToBeRemoved.remove(buff);
			}
			buffs.get(buffs.indexOf(buff)).resetDuration();
		} else {
			Buff b = buff.getCopy();
			buffsToBeAdded.add(b);
		}
	}

	public void loseBuff(Buff buff) {
		if (buffs.contains(buff)) {
			buffsToBeRemoved.add(buff);
		}
	}

	public boolean isAlive() {
		return alive;
	}

	public void addAttributeMultiplier(EntityAttribute attribute, String multiplierID, double multiplier) throws OperationNotSupportedException {
		if (multiplier <= 0) {
			throw new IllegalArgumentException("id = " + multiplierID + "      multiplier= " + multiplier);
		}
		attributeMultipliers.get(attribute).put(multiplierID, multiplier);
		updateAttribute(attribute);
	}

	public void removeAttributeMultiplier(EntityAttribute attribute, String multiplierID) throws OperationNotSupportedException {
		attributeMultipliers.get(attribute).remove(multiplierID);
		updateAttribute(attribute);
	}

	protected final void updateAttribute(EntityAttribute attribute) throws OperationNotSupportedException {
		double totalMultiplier = 1;
		for (Double multiplier : attributeMultipliers.get(attribute).values()) {
			totalMultiplier *= multiplier;
		}
		multiplyAttribute(attribute, totalMultiplier);
	}

	protected void multiplyAttribute(EntityAttribute attribute, double totalMultiplier) throws OperationNotSupportedException {
		double newValue;
		switch (attribute) {
		case ARMOR:
			armor = baseArmor * totalMultiplier;
			newValue = armor;
			break;
		case MAX_HEALTH:
			maxHealth = (int) ((double) baseMaxHealth * totalMultiplier);
			newValue = maxHealth;
			break;
		case DAMAGE:
			totalDamageMultiplier = totalMultiplier;
			newValue = totalDamageMultiplier;
			break;
		default:
			throw new OperationNotSupportedException();
		}
		notifyListenersAttributeChanged(attribute, newValue);
	}

	protected void notifyListenersAttributeChanged(EntityAttribute attribute, double newValue) {
		for (EntityAttributeListener listener : attributeListeners) {
			listener.entityAttributeChanged(this, attribute, newValue);
		}
	}

	public Point getLocation() {
		return new Point(x, y);
	}

	public Point getPixelCenterLocation() {
		Point p = getPixelLocation();
		p.translate(Map.getTileWidth() / 2, Map.getTileHeight() / 2);
		return p;
	}

	/**
	 * Should return a NEW point
	 */
	public abstract Point getPixelLocation();

	public boolean isLocationWithinDistance(Point location, double distance) {
		return getLocation().distanceSq(location) <= distance * distance;
	}

	public boolean isLocationAdjacent(Point location) {
		return isLocationWithinDistance(location, Physics.getAdjacencyDistance());
	}

	public void gainHealth(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("amount=" + amount);
		}
		setHealth(Math.min(health + amount, maxHealth), true);
	}
	
	public void gainHealthWithoutNotifyingListeners(int amount){
		if (amount < 0) {
			throw new IllegalArgumentException("amount=" + amount);
		}
		setHealth(Math.min(health + amount, maxHealth), false);
	}

	public void gainFullHealth() {
		setHealth(maxHealth, true);
	}

	public void loseHealth(int amount) {
		amount = Math.max((int) (amount - armor), 1);
		loseHealthIgnoringArmor(amount);
	}

	public void loseHealthIgnoringArmor(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("amount=" + amount);
		}
		setHealth(health - amount, true);
	}
	
	public void loseHealthIgnoringArmorWithoutNotifyingListeners(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("amount=" + amount);
		}
		setHealth(health - amount, false);
	}

	private void setHealth(int newHealth, boolean notifyListeners) {
		int previousHealth = health;
		health = newHealth;
		if(notifyListeners && previousHealth != health){
			for (EntityHealthListener listener : healthListeners) {
				listener.healthChanged(this, previousHealth, newHealth);
			}
		}
		
		renderableEntity.setPercentHealth((int) (health / (float)maxHealth * 100));

		if (health <= 0 && alive) {
			die(true);
		}
	}

	public boolean isBelowPercentHealth(int percent) {
		return (double) health / (double) maxHealth * 100 <= (double) percent;
	}

	public void changeArmor(double amount) {
		armor += amount;
		for(EntityAttributeListener listener : attributeListeners){
			listener.entityAttributeChanged(this, EntityAttribute.ARMOR, armor);
		}
	}

	double getArmor() {
		return armor;
	}

	@Override
	public boolean shouldBeRemovedFromGame() {
		return !alive;
	}

	public void notifyBirth() {
		Map.instance().setLocationBlockedByEntity(x, y, true);
		alive = true;
	}

	/**
	 * loseHealth() sets wasKilled = true
	 * 
	 * @param wasKilled
	 */
	public void die(boolean wasKilled) {
		alive = false;
		Map.instance().setLocationBlockedByEntity(x, y, false);
		if (wasKilled && deathSound != null) {
			Sounds.play(deathSound);
		}
		for (DeathListener listener : deathListeners) {
			listener.entityDied(this, wasKilled);
		}
	}
	
	public void dieWithoutNotifyingListeners(){
		alive = false;
		Map.instance().setLocationBlockedByEntity(x, y, false);
	}

	@Override
	public void render(Graphics g) {
		if(renderableEntity != null){
			renderableEntity.render(g);
		}
		
//		Point topLeft = getPixelLocation();
//		currentSprite.draw(topLeft.x, topLeft.y);
	}

	@Override
	public void renderExtraVisuals(Graphics g) {
		if(renderableEntity != null){
			renderableEntity.renderExtraVisuals(g);
		}
//		renderStatBars(g);
//		for (Buff buff : buffs) {
//			if (buff.hasAnimation()) {
//				Image buffImage = buff.getCurrentFrame();
//				g.drawImage(buffImage, getPixelCenterLocation().x - buffImage.getWidth() / 2, getPixelCenterLocation().y - buffImage.getHeight() / 2);
//			}
//		}
	}

//	protected void renderStatBars(Graphics g) {
//		Point uiTopLeft = getPixelLocation();
//		uiTopLeft.translate(0, -9);
//		RenderUtil.renderHealthBar(g, uiTopLeft, new Dimension(Map.getTileWidth(), 6), (double) health / (double) maxHealth, 1);
//	}

}
