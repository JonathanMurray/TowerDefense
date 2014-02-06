package rendering;

public class HUD_keyChars {

	char strength;
	char intelligence;
	char dexterity;
	char[] abilities;
	char[] items;

	public HUD_keyChars(char strength, char dexterity, char intelligence,
			char[] abilities, char[] items) {
		this.strength = strength;
		this.dexterity = dexterity;
		this.intelligence = intelligence;
		this.abilities = abilities;
		this.items = items;
	}

}
