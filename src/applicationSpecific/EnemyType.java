package applicationSpecific;

public enum EnemyType {

	SKELETON_WARRIOR,
	SKELETON_PROPHET,
	SKELETON_PIRATE,
	WOLF,
	FROST_MAIDEN,
	WIND_WRAITH,
	OLD_MAGE,
	SHADOW_WITCH,
	DARK_ANGEL;
	
	/*
	SKELETON_WARRIOR(100, 2, new Attack(7, 5, 1000, ResourceLoader.createSound(
			"swordSwing.wav", 0.6f)), 6, 800, ResourceLoader
			.createDirectionEncodedDirectionSpriteSet(true,
					"characters/skeletonWarrior/skeleton", 3, "png"),
			IDLE_FRAME_INDEX(), ResourceLoader
					.createSound("death/skeletonWarriorDeath.wav", 1.7f), 5, 1, null,
					"Harmless"),

	SKELETON_PROPHET(165, 2, new RangedAttack(6, 6, 700, 3.6, 8, Color.green,
			0.6, ResourceLoader.createSound("iceSpell3.wav")), 5, 850,
			ResourceLoader.createDirectionEncodedDirectionSpriteSet(true,
					"characters/skeletonProphet/skeleton", 3, "png"),
			IDLE_FRAME_INDEX(), ResourceLoader
					.createSound("death/skeletonProphetDeath.wav", 1.7f), 7, 2,
			new HealNearbyWounded(50, 85, 4.5, 1800, 3000, 40), 
			"Healer"),

	SKELETON_PIRATE(260, 5, new Attack(15, 9, 1200,
			ResourceLoader.createSound("skeletonSwing.wav")), 6, 650,
			ResourceLoader.createNumberEncodedDirectionSpriteSet(true,
					"characters/skeletonPirate/skeleton", 3, "png"),
			IDLE_FRAME_INDEX(), ResourceLoader
					.createSound("death/skeletonPirateDeath.wav", 1.6f), 9, 2,
			new MovementImpairingImmunityAtLowHealth(35),
			"Steadfast"),

	WOLF(75, 2, new Attack(6, 2, 1000,
			ResourceLoader.createSound("skeletonSwing.wav")), 8, 450,
			ResourceLoader.createNumberEncodedDirectionSpriteSet(true,
					"characters/wolf/skeleton", 3, "png"), IDLE_FRAME_INDEX(),
			ResourceLoader.createSound("death/wolfDeath.wav", 1.5f), 4, 1, null,
			""),

	FROST_MAIDEN(300, 3, new Attack(10, 10, 800,
			ResourceLoader.createSound("skeletonSwing.wav")), 6, 500,
			ResourceLoader.createNumberEncodedDirectionSpriteSet(true,
					"characters/frostMaiden/skeleton", 3, "png"),
			IDLE_FRAME_INDEX(), ResourceLoader
					.createSound("death/boneCrush.wav", 1.5f), 8, 2, null,
					""),

	WIND_WRAITH(240, 2, new RangedAttack(5, 10, 1000, 5, 6, Color.blue, 0.7,
			ResourceLoader.createSound("skeletonSwing.wav")), 8, 560,
			ResourceLoader.createNumberEncodedDirectionSpriteSet(true,
					"characters/windWraith/skeleton", 3, "png"),
			IDLE_FRAME_INDEX(), ResourceLoader
					.createSound("death/poofDeath.wav", 1.5f), 0, 0, null,
					""),

	OLD_MAGE(420, 2, new RangedAttack(10, 10, 2000, 5, 8, Color.green, 0.6,
			ResourceLoader.createSound("skeletonSwing.wav")), 6, 760,
			ResourceLoader.createNumberEncodedDirectionSpriteSet(true,
					"characters/oldMage/skeleton", 3, "png"),
			IDLE_FRAME_INDEX(), ResourceLoader
					.createSound("death/zombieMoan.wav"), 12, 2,
			new OldMageAction(),
			"Shielding summoner"),

	SHADOW_WITCH(700, 7, new RangedAttack(1, 10, 500, 4, 8, new Color(0.8f,
			0.2f, 1f), 0.6, ResourceLoader.createSound("skeletonSwing.wav")),
			6, 600, ResourceLoader.createNumberEncodedDirectionSpriteSet(true,
					"characters/shadowWitch/skeleton", 3, "png"),
			IDLE_FRAME_INDEX(), ResourceLoader
					.createSound("death/zombieMoan.wav"), 15, 3,
			new ShadowWitchAction(4500, 10, 12000, 2000, 2.7, 8000),
			"Powerful caster"),
			
	DARK_ANGEL(2100, 9, new Attack(40, 70, 2000, ResourceLoader.createSound("skeletonSwing.wav")),
			8, 600, ResourceLoader.createNumberEncodedDirectionSpriteSet(true,
					"characters/darkAngel/skeleton", 3, "png"),
			IDLE_FRAME_INDEX(), ResourceLoader
					.createSound("death/zombieMoan.wav"), 25, 5,
			new DarkAngelAction(4500, 10, 12000, 2000, 2700, 8000),
			"Unidentified");

	private static int IDLE_FRAME_INDEX() {
		return 1;
	}

	public int maxHealth;
	public double armor;
	private Attack attack;
	public int aggroRange;
	public int movementCooldown;
	public DirectionSpriteSet spriteSet;
	public int idleFrameIndex;
	public SoundContainer deathSound;
	public int moneyRewarded;
	public int experienceRewarded;
	public Enemy.ActionHandler actionHandler;
	public String tinyDescription;

	EnemyType(int maxHealth, double armor, Attack attack, int aggroRange,
			int movementCooldown, DirectionSpriteSet spriteSet,
			int idleFrameIndex, SoundContainer deathSound, int moneyRewarded,
			int experienceRewarded, Enemy.ActionHandler actionHandler, 
			String tinyDescription) {

		this.maxHealth = maxHealth;
		this.armor = armor;
		this.spriteSet = spriteSet;
		this.idleFrameIndex = idleFrameIndex;
		this.deathSound = deathSound;
		this.movementCooldown = movementCooldown;
		this.moneyRewarded = moneyRewarded;
		this.attack = attack;
		this.aggroRange = aggroRange;
		this.experienceRewarded = experienceRewarded;
		this.actionHandler = actionHandler;
		this.tinyDescription = tinyDescription;
		
	}

	public Attack getAttackCopy() {
		return Attack.getCopy(attack);
	}
*/
}
