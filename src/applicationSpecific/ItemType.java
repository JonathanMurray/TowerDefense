package applicationSpecific;


public enum ItemType {
	
	RESTORE,
	RESTORE_PLUS,
	HEALTH_POTION,
	HEALTH_POTION_PLUS,
	HEALTH_POTION_PLUS2,
	MANA_POTION,
	MANA_POTION_PLUS,
	MANA_POTION_PLUS2,
	SPECIAL_POTION,
	BOH,
	FROST_BITE,
	DEMON_HEART,
	MANA_STONE,
	BLOOD_SUCKER,
	ESCAPE,
	DEATH_MASK,
	FIRE_STONE;
	
/*
	RESTORE(
			"Restore",
			"Restores health and mana \nEffect is canceled if hero \ntakes damage",
			new ItemAction() {
				public void execute(Hero hero) {
				}
			}, new ItemAction() {
				public void execute(Hero hero) {
					Effect restore = new CompositeActionEffect(new Healing(8, null), new Healing(8, null));
					Buff restoration = new BuffOutsideCombat(new ContinuousSelfEffectsBuff(restore, 500, 10000, "restoration"));
					hero.receiveBuff(new BuffOutsideCombat(restoration));
				}
			}, new ItemContinuousAction() {
				public void execute(Hero hero, int delta) {
				}
			}, new ItemAction() {
				public void execute(Hero hero) {
				}
			}, true, 5, 0, ResourceLoader
					.createImage("items/clarityPotion.png"), false),

	healthPotion("Health", "Instantly restores some health \nto the hero",
			new ItemAction() {
				public void execute(Hero hero) {
				}
			}, new ItemAction() {
				public void execute(Hero hero) {
					hero.gainHealth(150);
				}
			}, new ItemContinuousAction() {
				public void execute(Hero hero, int delta) {
				}
			}, new ItemAction() {
				public void execute(Hero hero) {
				}
			}, true, 12, 0, ResourceLoader
					.createImage("items/healthPotion.png"), false),

	manaPotion("Mana", "Instantly restores some mana \nto the hero",
			new ItemAction() {
				public void execute(Hero hero) {
				}
			}, new ItemAction() {
				public void execute(Hero hero) {
					hero.changeMana(150);
				}
			}, new ItemContinuousAction() {
				public void execute(Hero hero, int delta) {
				}
			}, new ItemAction() {
				public void execute(Hero hero) {
				}
			}, true, 12, 0, ResourceLoader.createImage("items/bluePotion.png"),
			false),

	specialPotion("Special",
			"Instantly restores much mana \nand health to the hero",
			new ItemAction() {
				public void execute(Hero hero) {
				}
			}, new ItemAction() {
				public void execute(Hero hero) {
					hero.gainHealth(700);
					hero.changeMana(400);
				}
			}, new ItemContinuousAction() {
				public void execute(Hero hero, int delta) {
				}
			}, new ItemAction() {
				public void execute(Hero hero) {
				}
			}, true, 35, 0, ResourceLoader
					.createImage("items/specialPotion.png"), false),

	boots("BOH", "Grants increased movement \nspeed", new ItemAction() {
		public void execute(Hero hero) {
			hero.addAttributeMultiplier(UnitAttribute.MOVEMENT_SPEED,
					"BOHSpeed", 1.2);
		}
	}, null, new ItemContinuousAction() {
		public void execute(Hero hero, int delta) {
		}
	}, new ItemAction() {
		public void execute(Hero hero) {
			hero.removeAttributeMultiplier(UnitAttribute.MOVEMENT_SPEED,
					"BOHSpeed");
		}
	}, false, 20, 0, ResourceLoader.createImage("items/boots.png"), true),

	frostBite(
			"Frost bite",
			"Freezes nearby enemies for \na short time when used.\nCan be used several times",
			new ItemAction() {
				public void execute(Hero hero) {
				}
			}, new ItemAction() {
				public void execute(Hero hero) {
					for (Entity nearbyEnemy : Entities.getEntitiesWithinRange(
							hero.getLocation(), 4, Team.EVIL)) {
						((Unit) nearbyEnemy).stun(1500);
					}
				}
			}, new ItemContinuousAction() {
				public void execute(Hero hero, int delta) {
				}
			}, new ItemAction() {
				public void execute(Hero hero) {
				}
			}, false, 40, 0, ResourceLoader.createImage("items/frostBite.png"),
			true),

	demonHeart(
			"Demon heart",
			"Increases hero damage, but removes \nall natural health regeneration.",
			new ItemAction() {
				public void execute(Hero hero) {
					hero.addAttributeMultiplier(UnitAttribute.HEALTH_REGEN,
							"demonHeartRegen", 0.01);
					hero.addAttributeMultiplier(UnitAttribute.DAMAGE,
							"demonHeartDamage", 1.3);
				}
			}, null, new ItemContinuousAction() {
				public void execute(Hero hero, int delta) {
				}
			}, new ItemAction() {
				public void execute(Hero hero) {
					hero.removeAttributeMultiplier(UnitAttribute.HEALTH_REGEN,
							"demonHeartRegen");
					hero.removeAttributeMultiplier(UnitAttribute.DAMAGE,
							"demonHeartDamage");
				}
			}, false, 40, 0,
			ResourceLoader.createImage("items/volcanoGem.png"), true),

	manaStone(
			"Mana stone",
			"Increases hero mana regeneration,\nand can be consumed to instantly \nrestore all mana",
			new ItemAction() {
				public void execute(Hero hero) {
					hero.addAttributeMultiplier(UnitAttribute.MANA_REGEN,
							"manaStoneRegen", 1.3);
				}
			}, new ItemAction() {
				public void execute(Hero hero) {
					hero.gainFullMana();
				}
			}, new ItemContinuousAction() {
				public void execute(Hero hero, int delta) {
				}
			}, new ItemAction() {
				public void execute(Hero hero) {
					hero.removeAttributeMultiplier(UnitAttribute.MANA_REGEN,
							"manaStoneRegen");
				}
			}, true, 40, 0, ResourceLoader.createImage("items/manaStone.png"),
			true),

	bloodSucker("Blood sucker", "Drains the life out of \nadjacent foes.",
			new ItemAction() {
				public void execute(Hero hero) {
				}
			}, null, new ItemContinuousAction() {
				private int timeSinceSuck = 0;
				private final int cooldown = 800;
				private final int amount = 3;

				public void execute(Hero hero, int delta) {
					timeSinceSuck += delta;
					if (timeSinceSuck > cooldown) {
						for (Entity adjacentEnemy : Entities
								.getEntitiesAdjacentTo(hero.getLocation(), Team.EVIL)) {
							GamePlayState.addSpecialEffect(new AnimationSpecialEffect(
									adjacentEnemy.getPixelCenterLocation(),
									ResourceLoader.createTileScaledAnimation(
											false, "abilities/blood.png")));
							adjacentEnemy.loseHealth(HeroInfo
									.getMultipliedDamage(amount));
							hero.gainHealth(amount);
						}
						timeSinceSuck = 0;
					}
				}
			}, new ItemAction() {
				public void execute(Hero hero) {
				}
			}, false, 40, 0, ResourceLoader.createImage("items/helmet.png"),
			true),

	escape(
			"Escape",
			"Teleports to base and gains full \nhealth and mana. Hero is stunned \nfor a few seconds.",
			new ItemAction() {
				public void execute(Hero hero) {
				}
			}, new ItemAction() {
				public void execute(Hero hero) {
					hero.gainFullHealth();
					hero.gainFullMana();
					hero.teleportToLocation(17, 20);
					hero.stun(3000);
				}
			}, new ItemContinuousAction() {
				public void execute(Hero hero, int delta) {
				}
			}, new ItemAction() {
				public void execute(Hero hero) {
				}
			}, true, 40, 0,
			ResourceLoader.createImage("items/wizardStaff.png"), true);

	public String name;
	public String tooltip;
	private ItemAction equipAction;
	private ItemAction useAction;
	private ItemContinuousAction continuousAction;
	private ItemAction dropAction;
	private boolean isDroppedOnUse;
	public int buyCost;
	public int unlockCost;
	public Image icon;
	public boolean isUnique;

	ItemType(String name, String tooltip, ItemAction equipAction,
			ItemAction useAction, ItemContinuousAction continuousAction,
			ItemAction dropAction, boolean isDroppedOnUse, int buyCost,
			int unlockCost, Image icon, boolean isUnique) {
		this.name = name;
		this.tooltip = tooltip;
		this.buyCost = buyCost;
		this.equipAction = equipAction;
		this.useAction = useAction;
		this.continuousAction = continuousAction;
		this.dropAction = dropAction;
		this.isDroppedOnUse = isDroppedOnUse;
		this.unlockCost = unlockCost;
		this.icon = icon;
		this.isUnique = isUnique;
	}

	@Override
	public int getUnlockCost() {
		return unlockCost;
	}

	@Override
	public int getBuyCost() {
		return buyCost;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getTooltip() {
		return tooltip;
	}

	@Override
	public Image getIcon() {
		return icon;
	}

	public void wasEquipped(Hero hero) {
		equipAction.execute(hero);
	}

	public void wasUsed(Hero hero) {
		if (!canBeUsed()) {
			throw new IllegalStateException();
		}
		useAction.execute(hero);
	}

	public void continuousEffect(Hero hero, int delta) {
		continuousAction.execute(hero, delta);
	}

	public void wasDropped(Hero hero) {
		dropAction.execute(hero);
	}

	public boolean canBeUsed() {
		return useAction != null;
	}

	public boolean isDroppedOnUse() {
		return isDroppedOnUse;
	}

	public static interface ItemAction {
		public void execute(Hero hero);
	}

	public static interface ItemContinuousAction {
		public void execute(Hero hero, int delta);
	}
*/
}
