package game.objects;

import game.Attack;
import game.RangedAttack;
import game.buffs.Buff;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;

public class TowerData {

	
	public String name;
	public String tooltip;
	public int maxHealth;
	public int buildCost;
	public int unlockCost;
	public Animation sprite;
	public Image icon;
	private RangedAttack attack;
	public Buff behaviourBuff;
	public double specialRange;

	public TowerData(String name, String tooltip, int maxHealth, int buildCost,
			int unlockCost, Animation sprite, Image icon, RangedAttack attack,
			Buff behaviourBuff, double specialRange) {
		this.name = name;
		this.tooltip = 	"" + (attack != null ? (attack.getMinToMaxDamageString() + " dmg, " + attack.getAttackCooldownInSeconds() + "s, Range = " + attack.getRange()) + ", " : "")
						   + maxHealth + "HP\n" 
						   + tooltip;
		this.maxHealth = maxHealth;
		this.buildCost = buildCost;
		this.unlockCost = unlockCost;
		this.sprite = sprite;
		this.icon = icon;
		this.attack = attack;
		this.behaviourBuff = behaviourBuff;
		this.specialRange = specialRange;
	}
	
	public RangedAttack getAttack() {
		if (attack == null) {
			return null;
		}
		return (RangedAttack) Attack.getCopy(attack);
	}
	
}
