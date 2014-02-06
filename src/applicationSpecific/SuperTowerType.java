package applicationSpecific;

import game.Attack;
import game.RangedAttack;
import game.ResourceLoader;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

public enum SuperTowerType {

	FIRE_TOWER("Fire totem", "some tower", 500, 5, ResourceLoader
			.createTileScaledAnimation(false, "bubble.png"), ResourceLoader
			.createImage("bubble.png"), new RangedAttack(120, 50, 3000, 12, 19,
			Color.red, 2)),

	LIGHTNING_TOWER("Lightning pole", "some tower", 500, 5, ResourceLoader
			.createTileScaledAnimation(false, "bubble.png"), ResourceLoader
			.createImage("bubble.png"), new RangedAttack(20, 8, 500, 6, 10,
			Color.orange, 2)),

	WATER_TOWER("Water staff", "some tower", 500, 5, ResourceLoader
			.createTileScaledAnimation(false, "bubble.png"), ResourceLoader
			.createImage("bubble.png"), new RangedAttack(600, 20, 1500, 6, 10,
			Color.blue, 2));

	public String name;
	public String tooltip;
	public int maxHealth;
	public double armor;
	public Animation sprite;
	public Image icon;
	private RangedAttack attack;

	SuperTowerType(String name, String tooltip, int maxHealth, double armor,
			Animation sprite, Image icon, RangedAttack attack) {
		this.name = name;
		this.tooltip = tooltip;
		this.maxHealth = maxHealth;
		this.armor = armor;
		this.sprite = sprite;
		this.icon = icon;
		this.attack = attack;
	}

	public RangedAttack getAttackCopy() {
		return (RangedAttack) Attack.getCopy(attack);
	}
}
