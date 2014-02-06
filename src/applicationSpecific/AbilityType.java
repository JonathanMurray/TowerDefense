package applicationSpecific;

public enum AbilityType {
	
	PUNCH,
	RUN,
	THREE_BOMB,
	LIGHTNING,
	IRON,
	HEALING,
	BASH,
	BREATH,
	HOT,
	VAMPIRE,
	SPRINT,
	SHOCK,
	GENIUS,
	FURY,
	MEDITATE,
	FOCUS,
	DEBUG_GOD
	
	
/*
	PUNCH(new AbilityAction() {
		public void execute(Hero hero) {
			SoundContainer sound = ResourceLoader.createSound("swordArmor.wav",
					1.2f);
			Sounds.play(sound);
			try {
				Enemy enemy = Opponent.getOneFrontEnemy(hero.getLocation(),
						hero.getDirection());
				Game.addSpecialEffect(new SpriteSpecialEffect(Physics
						.getPixelCenterLocation(enemy.getLocation()),
						ResourceLoader.createTileScaledAnimation(false,
								"abilities/explosion.png")));
				enemy.loseHealth(HeroInfo.getMultipliedDamage(12));
			} catch (EnemyNotFound e) {
			}
		}
	}, "Punch", "Basic melee attack", ResourceLoader
			.createImage("abilities/fist.png"), 600, 7),

	RUN(new AbilityAction() {
		public void execute(Hero hero) {
			hero.receiveBuff(new SpeedBuff(1.5, 4000, "runSpeed",
					ResourceLoader.createBlinkingAnimation(ResourceLoader
							.createScaledImage("abilities/whiteStripes.png",
									65, 65))));
			SoundContainer sound = ResourceLoader.createSound("wobble3.wav");
			Sounds.play(sound);
		}
	}, "Run", "Grants increased movement \nspeed for a short \nduration",
			ResourceLoader.createImage("abilities/boh.gif"), 15000, 10),

	THREE_BOMB(
			new AbilityAction() {
				public void execute(Hero hero) {
					SoundContainer sound = ResourceLoader.createSound(
							"fireWater3.wav", 1.4f);
					Sounds.play(sound);
					Game.addSpecialEffect(new SpriteSpecialEffect(hero,
							"abilities/fire.png"));
					for (Enemy enemy : Opponent.getEnemiesAdjacentTo(hero
							.getLocation())) {
						enemy.receiveBuff(new HealthOverTimeBuff(2000,
								-HeroInfo.getMultipliedDamage(36), 5000, false,
								"3bombDot", ResourceLoader.createAnimation(
										false, "abilities/fire.png")));
						Game.addSpecialEffect(new SpriteSpecialEffect(enemy,
								"abilities/fire.png"));
					}
				}
			},
			"3x Bomb",
			"Adjacent foes are set on \nfire causing them to lose \nlife 3 times",
			ResourceLoader.createImage("abilities/fire.png"), 10000, 35),

	LIGHTNING(
			new AbilityAction() {
				public void execute(Hero hero) {
					SoundContainer sound = ResourceLoader.createSound(
							"flameArrow3.wav", 1.4f);
					Sounds.play(sound);
					int distance = 5;
					int damage = 37;
					Point source = getSource(hero, distance);
					doDamage(hero, Physics.getRelativeLocation(
							hero.getLocation(),
							hero.getDirection().getVector(1, 0)), damage);
					for (int i = 1; i < distance; i++) {
						Point targetLocation = Physics.getRelativeLocation(
								source, hero.getDirection(), i);
						doDamage(hero, targetLocation, damage);
					}
				}

				private Point getSource(Hero hero, int distance) {
					Point left = Physics.getRelativeLocation(
							hero.getLocation(),
							hero.getDirection().getVector(1, -1));
					Point mid = Physics.getRelativeLocation(hero.getLocation(),
							hero.getDirection().getVector(1, 0));
					Point right = Physics.getRelativeLocation(
							hero.getLocation(),
							hero.getDirection().getVector(1, 1));
					if (Opponent.getEnemiesOnLine(mid, hero.getDirection(),
							distance).size() == 0) {
						if (Opponent.getEnemiesOnLine(left,
								hero.getDirection(), distance).size() > 0) {
							return left;
						} else if (Opponent.getEnemiesOnLine(right,
								hero.getDirection(), distance).size() > 0) {
							return right;
						}
					}
					return mid;
				}

				private void doDamage(Hero hero, Point location, int amount) {
					try {
						Game.addSpecialEffect(new SpriteSpecialEffect(Physics
								.getPixelCenterLocation(location),
								ResourceLoader.createTileScaledAnimation(false,
										"abilities/lightning.png")));
						Enemy enemy = Opponent.getEnemyOnLocation(location);
						enemy.loseHealth(HeroInfo.getMultipliedDamage(amount));
						enemy.stun(800);
					} catch (EnemyNotFound e) {
					}
				}
			},
			"Lightning",
			"A beam of lightning \n damages foes in front \nof the player.\nLong range",
			ResourceLoader.createImage("abilities/lightning.png"), 1400, 65),

	IRON(new AbilityAction() {
		public void execute(Hero hero) {
			SoundContainer sound = ResourceLoader.createSound("wobble3.wav");
			Sounds.play(sound);
			int duration = 10000;
			hero.receiveBuff(new ArmorBuff(3, duration, "ironArmor",
					ResourceLoader
							.createScaledAnimation(false, 65, 65, 160,
									"abilities/blueBuff.png",
									"abilities/blueBuff2.png")));
			hero.receiveBuff(new ShieldBuff(45, duration, "ironShield"));

		}
	}, "Iron", "Grants absorbtion and \nincreased armor for \na short time",
			ResourceLoader.createImage("abilities/armor.png"), 12000, 20),

	HEALING(new AbilityAction() {
		public void execute(Hero hero) {
			hero.gainHealth(140);
			Game.addSpecialEffect(new SpriteSpecialEffect(hero, 3,
					"abilities/healing.png", "abilities/healing2.png",
					"abilities/healing3.png"));
			SoundContainer sound = ResourceLoader
					.createSound("swooshMagic3.wav");
			Sounds.play(sound);
		}
	}, "Healing", "Instantly restores some \nhealth", ResourceLoader
			.createImage("abilities/healingIcon.png"), 14000, 70),

	BASH(new AbilityAction() {
		public void execute(Hero hero) {
			SoundContainer sound = ResourceLoader.createSound(
					"flameArrow3.wav", 1.3f);
			Sounds.play(sound);
			for (Point vector : hero.getDirection().get3FrontVectors()) {
				try {
					Point targetLocation = Physics.getRelativeLocation(
							hero.getLocation(), vector);
					Enemy enemy = Opponent.getEnemyOnLocation(targetLocation);
					Game.addSpecialEffect(new SpriteSpecialEffect(Physics
							.getPixelCenterLocation(targetLocation),
							ResourceLoader.createTileScaledAnimation(false,
									"abilities/bash.png")));
					enemy.loseHealth(HeroInfo.getMultipliedDamage(35));
					enemy.stun(2000);

					break;
				} catch (EnemyNotFound e) {
				}
			}
		}
	}, "Bash", "Slower melee attack that \nstuns its target", ResourceLoader
			.createImage("abilities/shield.png"), 1200, 20),

	BREATH(
			new AbilityAction() {
				public void execute(Hero hero) {
					SoundContainer sound = ResourceLoader.createSound(
							"fireWater3.wav", 1.4f);
					Sounds.play(sound);
					for (int stepsForward = 1; stepsForward <= 3; stepsForward++) {
						for (int stepsRight = -1; stepsRight <= 1; stepsRight++) {
							try {
								Point targetLocation = Physics
										.getRelativeLocation(
												hero.getLocation(),
												hero.getDirection(),
												stepsForward, stepsRight);
								Game.addSpecialEffect(new SpriteSpecialEffect(
										Physics.getPixelCenterLocation(targetLocation),
										ResourceLoader
												.createTileScaledAnimation(
														false, 90,
														"abilities/poison.png",
														"abilities/poison2.png")));
								Enemy enemy = Opponent
										.getEnemyOnLocation(targetLocation);

								enemy.receiveBuff(new HealthOverTimeBuff(
										467,
										-HeroInfo.getMultipliedDamage(60),
										7000,
										true,
										"breathDot",
										ResourceLoader
												.createTileScaledAnimation(
														false, 90,
														"abilities/poison.png",
														"abilities/poison2.png")));
							} catch (EnemyNotFound e) {
							}
						}

					}
				}
			},
			"Breath",
			"Foes in a square formation\nin front of hero are poisoned\nlosing health over time",
			ResourceLoader.createImage("abilities/QuadSerpent.png"), 1500, 50),

	HOT(
			new AbilityAction() {
				public void execute(Hero hero) {
					hero.receiveBuff(new HealthOverTimeBuff(500, 200, 10000,
							false, "HOTHealingBuff", ResourceLoader
									.createAnimation(false,
											"abilities/healing.png",
											"abilities/healing2.png")));
					SoundContainer sound = ResourceLoader
							.createSound("swooshMagic3.wav");
					Sounds.play(sound);
				}
			}, "HOT", "Heals the player \nslowly over a long\nperiod of time",
			ResourceLoader.createImage("abilities/bubble.png"), 15000, 50),

	VAMPIRE(new AbilityAction() {
		public void execute(Hero hero) {
			SoundContainer sound = ResourceLoader.createSound("swordArmor.wav",
					1.2f);
			Sounds.play(sound);
			for (Point vector : hero.getDirection().get3FrontVectors()) {
				try {
					Point targetLocation = Physics.getRelativeLocation(
							hero.getLocation(), vector);
					Enemy enemy = Opponent.getEnemyOnLocation(targetLocation);
					Game.addSpecialEffect(new SpriteSpecialEffect(enemy
							.getPixelCenterLocation(), ResourceLoader
							.createTileScaledAnimation(false, 120,
									"abilities/death.png",
									"abilities/death2.png")));
					enemy.loseHealth(HeroInfo.getMultipliedDamage(12));
					hero.gainHealth(5);
				} catch (EnemyNotFound e) {
				}
			}
		}
	}, "Vampire", "Bites up to 3 foes \nin front of hero \nstealing health",
			ResourceLoader.createImage("abilities/bite.gif"), 800, 25),

	SPRINT(
			new AbilityAction() {
				public void execute(Hero hero) {
					hero.receiveBuff(new SpeedBuff(2.1, 1000, "sprintSpeed",
							ResourceLoader.createScaledAnimation(false, 65, 65,
									70, "abilities/sprint1.png",
									"abilities/sprint2.png")));
					SoundContainer sound = ResourceLoader
							.createSound("wobble3.wav");
					Sounds.play(sound);
				}
			}, "Sprint",
			"Grants heavily increased \nmovement speed for a short moment",
			ResourceLoader.createImage("abilities/boh.gif"), 1000, 10),

	SHOCK(new AbilityAction() {
		public void execute(Hero hero) {
			Game.addSpecialEffect(new SpriteSpecialEffect(hero
					.getPixelCenterLocation(), ResourceLoader
					.createScaledAnimation(false, 110, 110, 130,
							"abilities/lightBuff1.png",
							"abilities/lightBuff2.png")));
			ArrayList<Enemy> closeEnemies = Opponent.getEnemiesWithinRange(
					hero.getLocation(), 2.8);
			for (Enemy enemy : closeEnemies) {
				enemy.loseHealth(HeroInfo.getMultipliedDamage(30));
				Game.addSpecialEffect(new SpriteSpecialEffect(enemy
						.getPixelCenterLocation(), ResourceLoader
						.createScaledAnimation(false, 110, 110, 130,
								"abilities/lightBuff1.png",
								"abilities/lightBuff2.png")));
				enemy.stun(2000);
			}
		}
	}, "Shock", "Damages and stuns \nnearby enemies", ResourceLoader
			.createImage("abilities/bubble.png"), 7000, 80),

	GENIUS(
			new AbilityAction() {
				public void execute(Hero hero) {
					int duration = 8000;
					hero.receiveBuff(new ManaOverTimeBuff(500, 25, duration,
							"geniusManaRegen"));
					Animation animation = ResourceLoader.createAnimation(false,
							"abilities/lightBuff1.png",
							"abilities/lightBuff2.png",
							"abilities/lightBuff3.png");
					hero.receiveBuff(new GeniusSparklingBuff(500, 3, 4,
							duration, animation));
				}
			},
			"Genius",
			"Restores lots of mana \nover time and continuosly \ndamages nearby foes",
			ResourceLoader.createImage("abilities/genius.png"), 30000, 1),

	FURY(new AbilityAction() {
		public void execute(Hero hero) {
			Animation animation = ResourceLoader.createAnimation(false,
					"abilities/fury1.png", "abilities/fury2.png",
					"abilities/fury3.png");
			hero.receiveBuff(new FuryBuff(170,
					HeroInfo.getMultipliedDamage(55), 10, 3, animation));
		}
	}, "Fury",
			"Hero unleashes a fast \nseries of strikes \nagainst nearby foes",
			ResourceLoader.createImage("abilities/furyIcon.png"), 30000, 150),

	
	 * CLOUD( new AbilityAction(){ public void execute(Hero hero){
	 * ArrayList<Enemy> enemies =
	 * Opponent.getEnemiesWithinRange(hero.getLocation(), 1.8); for(Enemy enemy
	 * : enemies){ enemy.receiveBuff(new HealthOverTimeBuff(400,
	 * -HeroInfo.getDamageWithStatBonus(5), 10000, false, "cloudDot")); }
	 * SoundContainer sound = ResourceLoader.createSound("bell.wav");
	 * Sounds.play(sound); } }, "Cloud", "An ability",
	 * ResourceLoader.createImage("abilities/bubble.png"), 5000, 30 ),
	 

	IMBA(new AbilityAction() {
		public void execute(Hero hero) {
			Animation animation = ResourceLoader.createAnimation(false,
					"abilities/lightBuff1.png", "abilities/lightBuff2.png",
					"abilities/lightBuff3.png");
			hero.receiveBuff(new FuryBuff(500, 999, 999, 4, animation));
		}
	}, "IMBA", "Its too good!\nIts just too good!\n>:)", ResourceLoader
			.createImage("abilities/bubble.png"), 1, 1);

	public AbilityAction action;
	public String name;
	public String tooltip;
	public Image icon;
	public int cooldown;
	public int manaCost;

	private AbilityType(AbilityAction action, String name, String tooltip,
			Image icon, int cooldown, int manaCost) {
		this.action = action;
		this.name = name;
		this.tooltip = tooltip;
		this.icon = icon;
		this.cooldown = cooldown;
		this.manaCost = manaCost;
	}
*/
}
