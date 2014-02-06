package game;



import game.actions.Action;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.newdawn.slick.Image;

public class AbilityData {
	public Action action;
	public String name;
	public String tooltip;
	public Image icon;
	public int cooldown;
	public int manaCost;
	public SoundWrapper sound;

	public AbilityData(Action action, String name, String tooltip,
			Image icon, int cooldown, int manaCost, SoundWrapper sound) {
		this.action = action;
		this.name = name;
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
		otherSymbols.setDecimalSeparator('.');
		String prettyCooldown = new DecimalFormat("#.#", otherSymbols).format(cooldown/1000.0);
		this.tooltip = "" + manaCost + " mana  (" + prettyCooldown + "s cooldown)\n" + tooltip;
		this.icon = icon;
		this.cooldown = cooldown;
		this.manaCost = manaCost;
		this.sound = sound;
	}
}
