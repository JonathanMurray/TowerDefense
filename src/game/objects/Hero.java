package game.objects;

import game.AbilityData;
import game.LoadedData;
import game.SoundHandler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.naming.OperationNotSupportedException;

import messages.HUDMessage;
import messages.IntMessageData;
import messages.Message;
import messages.MessageListener;

import org.newdawn.slick.Animation;

import rendering.ClientRenderableEntity;
import applicationSpecific.AbilityType;
import applicationSpecific.HeroType;
import applicationSpecific.ItemType;

public class Hero extends StepperUnit {
	int mana;
	int maxMana;
	int baseMaxMana;
	int baseManaRegenCooldown;
	public int manaRegenCooldown;
	private int timeSinceManaRegen;

	private int baseHealthRegenCooldown;
	public int healthRegenCooldown;
	private int timeSinceHealthRegen;

	private final int ITEM_COOLDOWN = 12000;
	private int timeUntilItemReady = 0;

	private HashMap<AbilityType, Integer> abilitiesWithTimeSinceUse = new HashMap<>();

	HeroType type;

	public Hero(HeroType type, HeroData stats, Point location, EntityAttributeListener... attributeListeners) {
		super(stats.baseHealth, stats.baseArmor, stats.spriteSet, stats.idleFrameIndex, stats.baseMovementCooldown, location, stats.birthSound,
				stats.deathSound, attributeListeners);
		baseMaxMana = stats.baseMana;
		maxMana = baseMaxMana;
		notifyListenersAttributeChanged(EntityAttribute.MAX_MANA, maxMana);
		mana = (int) (maxMana * 0.75);
		manaRegenCooldown = stats.manaRegenCooldown;
		baseManaRegenCooldown = manaRegenCooldown;
		notifyListenersAttributeChanged(EntityAttribute.MANA_REGEN, manaRegenCooldown);
		healthRegenCooldown = stats.healthRegenCooldown;
		baseHealthRegenCooldown = healthRegenCooldown;
		notifyListenersAttributeChanged(EntityAttribute.HEALTH_REGEN, healthRegenCooldown);

		this.type = type;

		HeroInfo.INSTANCE.addListener(new MessageListener() {

			@Override
			public void messageReceived(Message message) {
				switch ((HUDMessage)message.type) {
				case ABILITY_WAS_REPLACED:
					AbilityType oldAbility = AbilityType.values()[message.getNthDataValue(0)];
					AbilityType newAbility = AbilityType.values()[message.getNthDataValue(1)];
					abilitiesWithTimeSinceUse.remove(oldAbility);
					AbilityData stats = LoadedData.getAbilityData(newAbility);
					abilitiesWithTimeSinceUse.put(newAbility, stats.cooldown);
					break;
				case ABILITY_WAS_ADDED:
					IntMessageData d2 = (IntMessageData) message.data;
					AbilityType ability = AbilityType.values()[d2.value];
					stats = LoadedData.getAbilityData(ability);
					abilitiesWithTimeSinceUse.put(ability, stats.cooldown);
					break;
				
				default:
					break;
				}
			}
		});

		setRenderableEntity(ClientRenderableEntity.createHero(getPixelLocation(), stats.spriteSet, getDirection(),
				(int) (health / (float) maxHealth * 100), (int) (mana / (float) maxMana * 100)));
		
		//TODO Should be offline only when in offline game.
		//Should be ServerRenderableEntity when on server

	}

	@Override
	public void die(boolean wasKilled) {
		super.die(wasKilled);
		HeroInfo.INSTANCE.notifyHeroDeath();
	}

	public boolean tryToUseItem(ItemType item) {
		System.out.println("hero.tryToUseItem(" + item); //TODO
		if (timeUntilItemReady <= 0) {
			LoadedData.getItemData(item).wasUsed(this);
			timeUntilItemReady = ITEM_COOLDOWN;
			HeroInfo.INSTANCE.notifyHeroUsedItem(item, timeUntilItemReady);
			return true;
		}
		return false;
	}

	@Override
	public void stun(int duration) {
		super.stun(duration);
		HeroInfo.INSTANCE.notifyHeroStunned(true);
	}

	@Override
	void notifyStunFaded() {
		HeroInfo.INSTANCE.notifyHeroStunned(false);
	}

	private boolean canUseAbility(AbilityType ability) {
		return hasEnoughManaForAbility(ability) && isAbilityReady(ability) && !isStunned();
	}

	private boolean hasEnoughManaForAbility(AbilityType abilityType) {
		AbilityData ability = LoadedData.getAbilityData(abilityType);
		return mana >= ability.manaCost;
	}

	private boolean isAbilityReady(AbilityType abilityType) {
		int timeSinceUse = getAbilityTimeSinceUse(abilityType);
		return timeSinceUse > LoadedData.getAbilityData(abilityType).cooldown;
	}

	// public int getAbilityPercentRemainingCooldown(AbilityType abilityType) {
	// AbilityData stats = Game.getAbilityData(abilityType);
	// int timeSinceUse = getAbilityTimeSinceUse(abilityType);
	// return (int) (100 * ((float) (stats.cooldown - timeSinceUse) / (float)
	// stats.cooldown));
	// }

	public void tryToUseAbility(AbilityType abilityType) {
		if (canUseAbility(abilityType)) {
			AbilityData stats = LoadedData.getAbilityData(abilityType);
			performAction(stats.action);
			if (stats.sound != null) {
				SoundHandler.play(stats.sound);
			}

			addToMana(-stats.manaCost);
			abilitiesWithTimeSinceUse.put(abilityType, 0);
			HeroInfo.INSTANCE.notifyHeroUsedAbility(abilityType);
		}
	}

	private int getAbilityTimeSinceUse(AbilityType abilityType) {
		if (abilitiesWithTimeSinceUse.containsKey(abilityType)) {
			return abilitiesWithTimeSinceUse.get(abilityType);
		}
		throw new IllegalArgumentException("Hero doesn't have ability: " + abilityType);
	}

	public void addToMana(int amount) {
		if (mana + amount > maxMana) {
			setMana(maxMana);
		} else if (mana + amount < 0) {
			setMana(0);
		} else {
			setMana(mana + amount);
		}
	}

	public void gainFullMana() {
		setMana(maxMana);
	}

	public void setMana(int mana) {
		int oldMana = this.mana;
		if (mana > maxMana || mana < 0) {
			throw new IllegalArgumentException();
		}
		this.mana = mana;
		if (mana != oldMana) {
			HeroInfo.INSTANCE.notifyHeroManaChanged(oldMana, mana, false, maxMana);
		}
		renderableEntity.setPercentMana((int) (mana / (float)maxMana * 100));
	}

	@Override
	protected void multiplyAttribute(EntityAttribute attribute, double totalMultiplier) {
		switch (attribute) {
		case HEALTH_REGEN:
			healthRegenCooldown = (int) ((double) baseHealthRegenCooldown / totalMultiplier);
			notifyListenersAttributeChanged(attribute, healthRegenCooldown);
			break;
		case MANA_REGEN:
			manaRegenCooldown = (int) ((double) baseManaRegenCooldown / totalMultiplier);
			notifyListenersAttributeChanged(attribute, manaRegenCooldown);
			break;
		case MAX_MANA:
			maxMana = (int) ((double) baseMaxMana * totalMultiplier);
			notifyListenersAttributeChanged(attribute, maxMana);
			break;
		default:
			try {
				super.multiplyAttribute(attribute, totalMultiplier);
			} catch (OperationNotSupportedException e) {
				e.printStackTrace();
				System.exit(0);
			}
			return;
		}
	}

	@Override
	public void update(int delta) {
		super.update(delta);
		handleManaRegen(delta);
		handleHealthRegen(delta);
		handleItemCooldown(delta);
		Iterator<AbilityType> abilityIterator = abilitiesWithTimeSinceUse.keySet().iterator();
		while (abilityIterator.hasNext()) {
			AbilityType a = abilityIterator.next();
			abilitiesWithTimeSinceUse.put(a, abilitiesWithTimeSinceUse.get(a) + delta);
		}
	}

	private void handleManaRegen(int delta) {
		timeSinceManaRegen += delta;
		if (timeSinceManaRegen > manaRegenCooldown) {
			timeSinceManaRegen -= manaRegenCooldown;
			addToMana(1);
		}
	}

	private void handleHealthRegen(int delta) {
		timeSinceHealthRegen += delta;
		if (timeSinceHealthRegen >= healthRegenCooldown) {
			timeSinceHealthRegen -= healthRegenCooldown;
			gainHealth(1);
		}
	}

	private void handleItemCooldown(int delta) {
		if (timeUntilItemReady > 0) {
			timeUntilItemReady -= delta;
		}
	}

	// @Override
	// protected void renderStatBars(Graphics g) {
	// Point uiTopLeft = new Point(getPixelLocation());
	// uiTopLeft.translate(0, -16);
	// RenderUtil.renderHealthBar(g, uiTopLeft,new Dimension(Map.getTileWidth(),
	// 6), (double) health / (double) maxHealth, 2);
	// uiTopLeft.translate(0, 7);
	// RenderUtil.renderManaBar(g, uiTopLeft, new Dimension(Map.getTileWidth(),
	// 6), (double) mana / (double) maxMana, 2);
	// }

	@Override
	public Team getTeam() {
		return Team.GOOD;
	}
}
