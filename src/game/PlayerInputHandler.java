package game;

import game.objects.Hero;
import game.objects.HeroInfo;
import game.objects.HeroStat;

import java.awt.Point;
import java.util.HashMap;

import org.newdawn.slick.Input;

import rendering.HUD;
import rendering.HUD_InputListener;

import applicationSpecific.AbilityType;
import applicationSpecific.ItemType;
import applicationSpecific.TowerType;

public class PlayerInputHandler implements HUD_InputListener {

	private static final int[] ABILITY_KEYS = new int[] { Input.KEY_1, Input.KEY_2, Input.KEY_3, Input.KEY_4, Input.KEY_5};
	private static final char[] ABILITY_CHARS = new char[] { '1', '2', '3', '4', '5' };

	private static final int[] ITEM_KEYS = new int[] { Input.KEY_Q, Input.KEY_W, Input.KEY_E, Input.KEY_R, Input.KEY_T };
	private static final char[] ITEM_CHARS = new char[] { 'Q', 'W', 'E', 'R', 'T' };

	private static final int[] STANDGROUND_KEYS = new int[] { Input.KEY_LCONTROL, Input.KEY_RCONTROL };

	private static TowerType selectedTower;
	
	public static PlayerInputHandler INSTANCE = new PlayerInputHandler();

	private static HashMap<Integer, Direction> MOVEMENT_KEYS = new HashMap<Integer, Direction>();
	static {
		MOVEMENT_KEYS.put(Input.KEY_UP, Direction.UP);
		MOVEMENT_KEYS.put(Input.KEY_LEFT, Direction.LEFT);
		MOVEMENT_KEYS.put(Input.KEY_DOWN, Direction.DOWN);
		MOVEMENT_KEYS.put(Input.KEY_RIGHT, Direction.RIGHT);
	}

	private static final HashMap<Integer, HeroStat> STAT_KEYS = new HashMap<Integer, HeroStat>();
	static {
		STAT_KEYS.put(Input.KEY_Z, HeroStat.STRENGTH);
		STAT_KEYS.put(Input.KEY_X, HeroStat.DEXTERITY);
		STAT_KEYS.put(Input.KEY_C, HeroStat.INTELLIGENCE);
	}

	private static final HashMap<HeroStat, Character> STAT_CHARS = new HashMap<HeroStat, Character>();
	static {
		STAT_CHARS.put(HeroStat.STRENGTH, 'Z');
		STAT_CHARS.put(HeroStat.DEXTERITY, 'X');
		STAT_CHARS.put(HeroStat.INTELLIGENCE, 'C');
	}

	public static void handleMouseInput(Input input, int delta, HUD hud) {
		int mouseX = input.getMouseX();
		int mouseY = input.getMouseY();

		if (input.isMousePressed(0)) { 
			Player.INSTANCE.tryToBuildTowerAtLocation(selectedTower, Map.getTileLocationFromMouseLocation(new Point(mouseX, mouseY)));
		} else if (input.isMousePressed(1)) {
			Player.INSTANCE.tryToSellTowerAtLocation(Map.getTileXFromMouseX(mouseX), Map.getTileYFromMouseY(mouseY));
		}

	}

	public static void handleKeyboardInput(Input input, int delta, HUD hud) {
		if (HeroInfo.INSTANCE.isHeroAlive()) {
			Hero hero = HeroInfo.INSTANCE.getHero();
			handleHeroMoveKeys(hero, input);
			handleHeroAbilityKeys(hero, input, hud);
			handleHeroItemKeys(hero, input, hud);
		}
		handleHeroStatKeys(input, hud);
	}

	private static void handleHeroMoveKeys(Hero hero, Input input) {
		for (int key : MOVEMENT_KEYS.keySet()) {
			if (input.isKeyDown(key)) {
				Direction direction = MOVEMENT_KEYS.get(key);
				if (isCtrlDown(input) && !hero.isMidMovement() && !hero.isStunned()) {
					hero.setDirection(direction);
				} else {
					hero.orderMovement(direction);
				}
			}
		}
	}

	private static boolean isCtrlDown(Input input) {
		for (int key : STANDGROUND_KEYS) {
			if (input.isKeyDown(key)) {
				return true;
			}
		}
		return false;
	}

	private static void handleHeroAbilityKeys(Hero hero, Input input, HUD hud) {
		for (int i = 0; i < HeroInfo.MAX_ABILITIES; i++) {
			if (input.isKeyDown(ABILITY_KEYS[i])) {
				try {
					AbilityType ability = hud.getAbilityWithNumber(i + 1);
					hero.tryToUseAbility(ability);
					if (input.isKeyPressed(ABILITY_KEYS[i])) {
						hud.abilityKeyWasPressed(i + 1);
					}
				} catch (IllegalArgumentException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	private static void handleHeroItemKeys(Hero hero, Input input, HUD hud) {
		for (int i = 0; i < HeroInfo.MAX_USABLE_ITEMS; i++) {
			if (input.isKeyPressed(ITEM_KEYS[i])) {
				try {
					ItemType item = hud.getItemWithNumber(i + 1);
					itemKeyWasPressed(hero, i, item);
					hud.itemKeyWasPressed(i + 1);
				} catch (IllegalArgumentException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}

	private static void itemKeyWasPressed(Hero hero, int itemIndex, ItemType item) {
		ItemData itemStats = LoadedData.getItemData(item);
		if (itemStats.canBeUsed()) {
			hero.tryToUseItem(item);
		}
	}

	private static void handleHeroStatKeys(Input input, HUD hud) {
		for (int key : STAT_KEYS.keySet()) {
			if (input.isKeyPressed(key)) {
				hud.statKeyWasPressed(STAT_KEYS.get(key));
				HeroInfo.INSTANCE.trySpendStatpointOn(STAT_KEYS.get(key));
				return;
			}
		}
	}

	static char getStatChar(HeroStat stat) {
		return STAT_CHARS.get(stat);
	}

	static char[] getAbilityChars() {
		return ABILITY_CHARS;
	}

	static char[] getItemChars() {
		return ITEM_CHARS;
	}

	@Override
	public void pressedBuyItem(ItemType itemType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void towerSelected(TowerType towerType) {
		selectedTower = towerType;
	}

	@Override
	public void pressedUnlockTower(TowerType towerType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressedReplaceAbility(AbilityType oldAbility, AbilityType newAbility) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressedAddAbility(AbilityType newAbility) {
		// TODO Auto-generated method stub

	}

}
