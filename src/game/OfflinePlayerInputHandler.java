package game;

import game.objects.Hero;
import game.objects.HeroInfo;
import game.objects.HeroStat;

import java.awt.Point;
import java.util.HashMap;

import messages.Message;

import org.apache.commons.lang.ArrayUtils;
import org.newdawn.slick.Input;

import rendering.HUD;
import applicationSpecific.AbilityType;
import applicationSpecific.ItemType;
import applicationSpecific.TowerType;

public class OfflinePlayerInputHandler implements MessageListener {

	static final int[] ABILITY_KEYS = new int[] { Input.KEY_1, Input.KEY_2, Input.KEY_3, Input.KEY_4, Input.KEY_5};
	static final char[] ABILITY_CHARS = new char[] { '1', '2', '3', '4', '5' };

	static final int[] ITEM_KEYS = new int[] { Input.KEY_Q, Input.KEY_W, Input.KEY_E, Input.KEY_R, Input.KEY_T };
	static final char[] ITEM_CHARS = new char[] { 'Q', 'W', 'E', 'R', 'T' };

	static final int[] STANDGROUND_KEYS = new int[] { Input.KEY_LCONTROL, Input.KEY_RCONTROL };

	private static TowerType selectedTower;
	
	public static OfflinePlayerInputHandler INSTANCE = new OfflinePlayerInputHandler();

	static HashMap<Integer, Direction> MOVEMENT_KEYS = new HashMap<Integer, Direction>(); //TODO COUPLED
	static {
		MOVEMENT_KEYS.put(Input.KEY_UP, Direction.UP);
		MOVEMENT_KEYS.put(Input.KEY_LEFT, Direction.LEFT);
		MOVEMENT_KEYS.put(Input.KEY_DOWN, Direction.DOWN);
		MOVEMENT_KEYS.put(Input.KEY_RIGHT, Direction.RIGHT);
	}

	private static final HashMap<Integer, HeroStat> STAT_KEYS = new HashMap<Integer, HeroStat>(); //TODO COUPLED
	static {
		STAT_KEYS.put(Input.KEY_Z, HeroStat.STRENGTH);
		STAT_KEYS.put(Input.KEY_X, HeroStat.DEXTERITY);
		STAT_KEYS.put(Input.KEY_C, HeroStat.INTELLIGENCE);
	}
	
	static final int[] ALL_KEYS = ArrayUtils.addAll(ArrayUtils.addAll(ITEM_KEYS, STANDGROUND_KEYS), //TODO COUPLED TO MOVEMENT_KEYS AND STAT_KEYS!!!
			new int[]{Input.KEY_UP, Input.KEY_LEFT, Input.KEY_DOWN, Input.KEY_RIGHT, Input.KEY_Z, Input.KEY_X, Input.KEY_C});
	


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
			handleLeftMousePressed(mouseX, mouseY, hud);
		} else if (input.isMousePressed(1)) {
			handleRightMousePressed(mouseX, mouseY, hud);
		}
	}
	
	public static void handleLeftMousePressed(int mouseX, int mouseY, HUD hud){
		Player.INSTANCE.tryToBuildTowerAtLocation(selectedTower, Map.getTileLocationFromMouseLocation(new Point(mouseX, mouseY)));
	}
	
	public static void handleRightMousePressed(int mouseX, int mouseY, HUD hud){
		Player.INSTANCE.tryToSellTowerAtLocation(Map.getTileXFromMouseX(mouseX), Map.getTileYFromMouseY(mouseY));
	}

	
	//Redundant with other version of this function
	//But might be worth it, since other version is inefficient (?)
	public static void handleKeyboardInput(Input input, HUD hud) {
		if (HeroInfo.INSTANCE.isHeroAlive()) {
			Hero hero = HeroInfo.INSTANCE.getHero();
			handleHeroMoveKeys(hero, input);
			handleHeroAbilityKeys(hero, input, hud);
			handleHeroItemKeys(hero, input, hud);
		}
		handleHeroStatKeys(input, hud);
		
	}
	
	

	//Redundant with other version of this function
	//But might be worth it, since other version is more efficient (?)
	public static void handleKeyboardInput(int[] keysDown, int[] pressedKeys, HUD hud){ 
		//The point of this version is to be able to send int arrays over network instead of a big Input-object.
		if (HeroInfo.INSTANCE.isHeroAlive()) {
			Hero hero = HeroInfo.INSTANCE.getHero();
			for(int i = 0; i < keysDown.length; i++){
				int key = keysDown[i];
				if(MOVEMENT_KEYS.keySet().contains(key)){
					Direction direction = MOVEMENT_KEYS.get(key);
					if(CollectionsUtil.arrayContains(keysDown, key) && !hero.isMidMovement() && !hero.isStunned()){
						hero.setDirection(direction);
					}else{
						hero.orderMovement(direction);
					}
				}else if(CollectionsUtil.arrayContains(ABILITY_KEYS, key)){
					int abilityIndex = CollectionsUtil.getIndexOf(ABILITY_KEYS, key);
					AbilityType ability = hud.getAbilityWithNumber(abilityIndex + 1);
					hero.tryToUseAbility(ability);
					if (CollectionsUtil.arrayContains(pressedKeys, key)) {
						hud.abilityKeyWasPressed(abilityIndex + 1);
					}
				}
			}
			
			for(int i = 0; i < pressedKeys.length; i++){
				int key = pressedKeys[i];
				int itemIndex = CollectionsUtil.getIndexOf(ITEM_KEYS, key);
				if(itemIndex < 0){
					System.out.println(key);
					System.out.println(ITEM_KEYS[0] + " " + ITEM_KEYS[1] + " " + ITEM_KEYS[2] + " " + ITEM_KEYS[3] + " " + ITEM_KEYS[4] + " " );
				}
				ItemType item = hud.getItemWithNumber(itemIndex + 1);
				itemKeyWasPressed(hero, itemIndex, item);
				hud.itemKeyWasPressed(itemIndex + 1);
			}
		}
		
		for(int i = 0; i < pressedKeys.length; i++){
			int key = pressedKeys[i];
			hud.statKeyWasPressed(STAT_KEYS.get(key));
			HeroInfo.INSTANCE.trySpendStatpointOn(STAT_KEYS.get(key));
		}		
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
				AbilityType ability = hud.getAbilityWithNumber(i + 1);
				hero.tryToUseAbility(ability);
				if (input.isKeyPressed(ABILITY_KEYS[i])) {
					hud.abilityKeyWasPressed(i + 1);
				}
			}
		}
	}

	private static void handleHeroItemKeys(Hero hero, Input input, HUD hud) {
		for (int i = 0; i < HeroInfo.MAX_USABLE_ITEMS; i++) {
			if (input.isKeyPressed(ITEM_KEYS[i])) {
				ItemType item = hud.getItemWithNumber(i + 1);
				itemKeyWasPressed(hero, i, item);
				hud.itemKeyWasPressed(i + 1);
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
	public void messageReceived(Message message){
		switch(message.type){
		case TOWER_WAS_SELECTED:
			selectedTower = TowerType.values()[message.getIntDataValue()];
			break;
			
		}
	}
}
