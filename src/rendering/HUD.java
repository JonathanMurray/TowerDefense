package rendering;

import game.LoadedData;
import game.ResourceLoader;
import game.objects.Entity;
import game.objects.EntityAttribute;
import game.objects.EntityAttributeListener;
import game.objects.HeroStat;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import messages.HUDMessage;
import messages.IntArrayMessageData;
import messages.IntMessageData;
import messages.Message;
import messages.MessageListener;
import messages.MessageType;
import messages.UserInputMessage;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;

import applicationSpecific.AbilityType;
import applicationSpecific.ItemType;
import applicationSpecific.TowerType;

public class HUD implements  ButtonRowListener, EntityAttributeListener, MessageListener{

	private Font f = new Font("Verdana", Font.BOLD, 32);
	private TrueTypeFont bigFont = new TrueTypeFont(f, true);
	public static org.newdawn.slick.Font NORMAL_FONT = null;
	private static final Color BACKGROUND = new Color(0.5f, 0.5f, 0.5f);

	private static final int ICON_WIDTH = 64;
	private static final int ICON_GAP = 5;
	private static final int ICON_HEIGHT = 64;
	private static int LEFT_ROW_X = 30;
	private static final int RIGHT_ROW_X = 755;
	private static final int UPPER_ROW_Y = 1000;
	private static final int LOWER_ROW_Y = 1195;

	private static final int DIALOG_LOCATION_X = 500;
	private static final int DIALOG_LOCATION_Y = 300;

	private int waveIndex;
	private int secondsUntilNextWave;
	private boolean waveHasMoreSpawns;
	private boolean isFinalWave;
	private boolean waveIsCleared;
	private boolean wavesHaveBegun;

	private int selectedTowerIndex;
	private static ArrayList<AbilityType> abilities = new ArrayList<AbilityType>();
	private AbilityType upcomingAbility;
	private boolean isChoosingAbility;
//	private static ArrayList<ItemType> usableItems = new ArrayList<ItemType>();
	private ArrayList<Dialog> dialogs = new ArrayList<Dialog>();
	private TextBox tooltip;
	private TextBox tooltipOverEnemy;

	private static TowerButtonRow buyTowerButtons;
	private static VendorButtonRow vendorButtons;
	private static AbilityButtonRow abilityButtons;
	private static InventoryButtonRow itemButtons;
	private static ButtonRow statButtons;
	private static ArrayList<ButtonRow> buttonRows = new ArrayList<ButtonRow>();

	private Point towerMouseOverPixelCenterLocation;
	private int towerMouseOverPixelAttackRange;
	private int towerMouseOverPixelSpecialRange;
	private boolean renderTowerMouseOver;

	private static HUD_keyChars keyChars;

	private GUIContext container;
	private static HUD instance;

	private int money;
	private int life;
	private int maxLife;
	private HashMap<HeroStat, Integer> heroStats = new HashMap<HeroStat, Integer>();
	private boolean isHeroDead;
	private int timeUntilHeroResurrection;
	private int timeUntilCanUseItemAgain;
	private int maxHealth;
	private int maxMana;
	private double armor;
	private int numAvailableStatpoints;
	private int movementSpeed;
	private double damageMultiplier;
	private double heroPartHealth;
	private double heroPartMana;
	private int maxAbilities;
	private int maxItems;
	private List<MessageListener> listeners = new ArrayList<>();

	public HUD(GUIContext container, HUD_keyChars keyChars, int maxLife, int maxAbilities, int maxItems) {
		this.container = container;
		HUD.keyChars = keyChars;
		this.maxLife = maxLife;
		this.maxAbilities = maxAbilities;
		this.maxItems = maxItems;
		setupBuyTowerButtons(container);
		setupBuyItemButtons(container);
		setupAbilityButtons(container);
		setupItemButtons(container);
		setupStatButtons(container);

		if (instance != null) {
			throw new IllegalStateException("Only 1 HUD!");
		}
		instance = this;
		
	}

	public void addInputListener(MessageListener listener) {
		listeners.add(listener);
		if(buyTowerButtons.hasAddedSomeTowerButton){
			listener.messageReceived(new Message(UserInputMessage.PRESSED_SELECT_TOWER, new IntMessageData(selectedTowerIndex)));
		}
	}

	public static HUD instance() {
		return instance;
	}

	public void update(Input input, int delta) {
		tooltip = null; // Needed because we don't handle mouse move out from
						// button - event
						// tower - event
		tooltipOverEnemy = null; // Same reason as above
		renderTowerMouseOver = false;
		timeUntilHeroResurrection -= delta;
		for (ButtonRow buttonRow : buttonRows) {
			buttonRow.update(input, delta);
		}
		for (Dialog dialog : dialogs) {
			dialog.update(input, delta);
		}
	}

	public void render(GUIContext container, Graphics g) {
		if (NORMAL_FONT == null) {
			NORMAL_FONT = g.getFont();
		}
		renderBackground(container, g);
		renderButtons(container, g);
		renderStats(container, g);
		for (Dialog dialog : dialogs) {
			dialog.render(container, g);
		}
		if (tooltip != null) {
			tooltip.render(container, g);
		}
		if (tooltipOverEnemy != null) {
			tooltipOverEnemy.render(container, g);
		}
		renderTowerMouseOver(container, g);
	}

	public boolean isMouseOverHUDElements(Input input) {
		for (ButtonRow buttonRow : buttonRows) {
			if (buttonRow.isMouseOver(input)) {
				return true;
			}
		}
		return isMouseOverDialogs(input);
	}

	public void receiveWavesData(int waveIndex, int secondsUntilNextWave, boolean hasMoreSpawns, boolean isCleared, boolean wavesHaveBegun, boolean isFinalWave) {
		this.waveIndex = waveIndex;
		this.secondsUntilNextWave = secondsUntilNextWave;
		waveHasMoreSpawns = hasMoreSpawns;
		waveIsCleared = isCleared;
		this.wavesHaveBegun = wavesHaveBegun;
		this.isFinalWave = isFinalWave;
	}

	public void addAbilitySwapDialog(AbilityType newAbility) {
		if (isChoosingAbility) {
			return;
		}
		isChoosingAbility = true;

		DialogChoice replacement = new DialogChoice(LoadedData.getAbilityData(newAbility));
		DialogChoice[] old = new DialogChoice[abilities.size()];
		for (int i = 0; i < abilities.size(); i++) {
			old[i] = new DialogChoice(LoadedData.getAbilityData(abilities.get(i)));
		}
		Dialog dialog = KeyboardDialog.createDialogWithReplacement(this, container, "Choose ability to replace:", new Point(DIALOG_LOCATION_X,
				DIALOG_LOCATION_Y), replacement, old);
		dialogs.add(dialog);
		final AbilityType abilityToReplaceOld = newAbility;
		dialog.setListener(new DialogListener() {
			public void choiceWasMade(int choiceIndex) {
				AbilityType oldAbility = abilities.get(choiceIndex);
				for(MessageListener listener : listeners){
					listener.messageReceived(new Message(UserInputMessage.PRESSED_REPLACE_ABILITY, new IntArrayMessageData(oldAbility.ordinal(), abilityToReplaceOld.ordinal())));
				}
				isChoosingAbility = false;
			}

			public void mouseOverChoice(int choiceIndex) {
				// addTooltip(buttonTopLeft, text)
			}
		});
	}

	public void addAbilityChoiceDialog(final AbilityType... newAbilities) {
		if (isChoosingAbility) {
			return;
		}
		isChoosingAbility = true;

		DialogChoice[] choices = new DialogChoice[newAbilities.length + 1];
		for (int i = 0; i < newAbilities.length; i++) {
			choices[i] = new DialogChoice(LoadedData.getAbilityData(newAbilities[i]));
		}
		choices[newAbilities.length] = new DialogChoice(ResourceLoader.createBlankImage(10, 10), "None", "Keep your existing\nabilities");
		Dialog dialog = KeyboardDialog.createDialog(this, container, "Pick new ability:", new Point(DIALOG_LOCATION_X, DIALOG_LOCATION_Y), choices);
		dialogs.add(dialog);
		dialog.setListener(new DialogListener() {
			public void choiceWasMade(int choiceIndex) {
				if (choiceIndex != newAbilities.length) {
					upcomingAbility = newAbilities[choiceIndex];
				}
				isChoosingAbility = false;
			}

			public void mouseOverChoice(int choiceIndex) {
				// addTooltip(buttonTopLeft, text)
			}
		});
	}

	public void handleInput(Input input) {
		handleUpcomingAbility();
		boolean isMousePressed = input.isMousePressed(0);
		Iterator<Dialog> it = dialogs.iterator();
		while (it.hasNext()) {
			Dialog dialog = it.next();
			dialog.handleInput(input, isMousePressed);
			if (dialog.hasMadeChoice()) {
				it.remove();
			}
		}
	}

	@Override
	public void buttonWasPressed(ButtonRow sourceRow, Button sourceButton, int buttonIndex) {
		if (sourceRow == buyTowerButtons) {
			if(((TowerButton)sourceButton).isActive){
				selectedTowerIndex = buttonIndex;
				for(MessageListener listener : listeners){
					listener.messageReceived(new Message(UserInputMessage.PRESSED_SELECT_TOWER, new IntMessageData(selectedTowerIndex)));
				}
			}
			
		} else if (sourceRow == vendorButtons) {
			ItemType item = vendorButtons.getItemOfButtonWithIndex(buttonIndex);
			for(MessageListener listener : listeners){
				listener.messageReceived(new Message(UserInputMessage.PRESSED_BUY_ITEM, new IntMessageData(item.ordinal())));
			}
		}
	}
	
	@Override
	public void buttonWasUnlockPressed(ButtonRow sourceRow, Button sourceButton, int buttonIndex) {
		if(sourceRow != buyTowerButtons){
			throw new IllegalStateException("Only tower row has unlock");
		}
		TowerType tower = ((TowerButton)sourceButton).getTowerType();
		for(MessageListener listener : listeners){
			listener.messageReceived(new Message(UserInputMessage.PRESSED_UNLOCK_TOWER, new IntMessageData(tower.ordinal())));
		}
	}

	@Override
	public void buttonMouseOver(ButtonRow sourceRow, Button sourceButton, int buttonIndex) {
		Rectangle buttonRect = sourceRow.getNthButtonRectangle(buttonIndex + 1);
		tooltip = createButtonTooltip(new Point((int) buttonRect.getX(), (int) buttonRect.getY()), sourceRow.getTooltipOfButtonWithIndex(buttonIndex));
	}

	public void towerMouseOver(Point pixelCenterLocation, int pixelAttackRange, int pixelSpecialRange) {
		towerMouseOverPixelCenterLocation = pixelCenterLocation;
		towerMouseOverPixelAttackRange = pixelAttackRange;
		towerMouseOverPixelSpecialRange = pixelSpecialRange;
		renderTowerMouseOver = true;
	}

	public void enemyMouseOver(Point enemyPixelLocation, String text) {
		tooltipOverEnemy = createEnemyMouseOverText(enemyPixelLocation, text);
	}

	TextBox createButtonTooltip(Point buttonTopLeft, String text) {
		int width = 340;
		int height = 140;
		int space = 10;
		Rectangle tooltipRect = new Rectangle((int) buttonTopLeft.getX(), (int) buttonTopLeft.getY() - height - space, width, height);
		return new TextBox(tooltipRect, text);
	}
	
	public boolean hasAbilityWithNumber(int abilityNumber){
		return abilityNumber >= 1 && abilityNumber <= abilities.size();
	}

	public AbilityType getAbilityWithNumber(int abilityNumber) {
		if (abilityNumber < 1 || abilityNumber > abilities.size()) {
			throw new IllegalArgumentException("No ability with that number");
		}
		return abilities.get(abilityNumber - 1);
	}
	
	public boolean hasItemWithNumber(int itemNumber){
		return itemButtons.hasItemWithNumber(itemNumber);
	}

	public ItemType getItemWithNumber(int itemNumber) {
		return itemButtons.getItemWithNumber(itemNumber);
	}

	public int getNumberOfItem(ItemType itemType){
		return itemButtons.getNumberOfItem(itemType);
	}
	
	@Override
	public void messageReceived(Message message) {
		System.out.println("hud received msg:  " + message);
		switch((HUDMessage)message.type){
		case HERO_IN_RANGE_OF_VENDOR:
			vendorButtons.heroIsNowInRangeOfVendor(message.getBoolDataValue());
			break;
			
		case HERO_REVIVED:
			abilityButtons.heroResurrected();
			itemButtons.heroResurrected();
			isHeroDead = false;
			break;
			
		case HERO_MANA_CHANGED:
			int newMana = message.getNthDataValue(1);
			int maxMana = message.getNthDataValue(2);
			abilityButtons.notifyHeroManaChanged(newMana);
			heroPartMana = newMana / (double)maxMana;
			break;
			
		case MONEY_WAS_UPDATED:
			int newAmount = message.getIntDataValue();
			this.money = newAmount;
			buyTowerButtons.setMoney(newAmount);
			vendorButtons.setMoney(newAmount);
			break;
			
		case PLAYER_LIFE_WAS_UPDATED:
			this.life = message.getIntDataValue();
			break;
			
		case HERO_STAT_CHANGED:
			HeroStat stat = HeroStat.values()[message.getNthDataValue(0)];
			int newValue = message.getNthDataValue(1);
			heroStats.put(stat, newValue);
			break;
			
		case HERO_DIED:
			int timeUntilResurrection = message.getIntDataValue();
			timeUntilHeroResurrection = timeUntilResurrection;
			isHeroDead = true;
			abilityButtons.heroDied();
			itemButtons.heroDied();
			break;
			
		case NUM_STATPOINTS_CHANGED:
			System.out.println("hud.numStatpoitnschanged");//TODO
			
			int numAvailableStatpoints = message.getIntDataValue();
			this.numAvailableStatpoints = numAvailableStatpoints;
			for(Button b : statButtons.buttons){
				b.setActive(numAvailableStatpoints > 0);
			}
			break;
			
		case ITEM_WAS_USED:
			System.out.println("HUD.itemwasused");//TODO
			int timeUntilCanUseAgain = message.getNthDataValue(1);
			timeUntilCanUseItemAgain = timeUntilCanUseAgain;
			itemButtons.itemWasUsed(timeUntilCanUseAgain);
			break;
			
		case HERO_HEALTH_CHANGED:
			int newHealth = message.getNthDataValue(0);
			int maxHealth = message.getNthDataValue(1);
			heroPartHealth = newHealth / (double) maxHealth;
			break;
			
		case HERO_USED_ABILITY:
			AbilityType abilityType = AbilityType.values()[message.getNthDataValue(0)];
			timeUntilCanUseAgain = message.getNthDataValue(1);
			abilityButtons.abilityWasUsed(abilityType, timeUntilCanUseAgain);
			break;
			
		case ABILITY_WAS_REPLACED:
			AbilityType oldAbility = AbilityType.values()[message.getNthDataValue(0)];
			AbilityType newAbility = AbilityType.values()[message.getNthDataValue(1)];
			abilities.set(abilities.indexOf(oldAbility), newAbility);
			abilityButtons.replaceAbilityWithNew(oldAbility, newAbility);
			break;
			
		case ABILITY_WAS_ADDED:
			newAbility = AbilityType.values()[message.getIntDataValue()];
			abilities.add(newAbility);
			abilityButtons.addAbilityButton(newAbility, keyChars.abilities);
			break;
		
		case TOWER_WAS_MADE_AVAILABLE:
			//(MessageType.TOWER_WAS_ADDED, new IntArrayMessageData(towerType.ordinal(), tower.getLocation().x, tower.getLocation().y)));
			buyTowerButtons.addTowerButton(TowerType.values()[message.getIntDataValue()]);
			break;
			
		case TOWER_WAS_UNLOCKED:
			buyTowerButtons.unlockTower(TowerType.values()[message.getIntDataValue()]);
			break;
			
		case ITEM_WAS_ADDED:
			vendorButtons.addVendorButton(ItemType.values()[message.getIntDataValue()]);
			break;
			
		case ITEM_WAS_REMOVED:
			vendorButtons.removeVendorButton(ItemType.values()[message.getIntDataValue()]);
			break;
			
		case ITEM_WAS_EQUIPPED:
			itemButtons.itemWasAdded(ItemType.values()[message.getIntDataValue()], keyChars.items);
			break;
			
		case ITEM_WAS_REPLACED:
			int oldItemIndex = message.getNthDataValue(0);
			ItemType newItem = ItemType.values()[message.getNthDataValue(1)];
			itemButtons.replaceItemWithNew(oldItemIndex, newItem);
			break;
			
		case ITEM_WAS_DROPPED:
			itemButtons.itemWasDropped(message.getIntDataValue());
		case TOWER_WAS_SELECTED:
			break;
		case ABILITY_WAS_PRESSED:
			break;
		case ITEM_WAS_PRESSED:
			break;
		default:
			break;


		}
	}


	public void abilityKeyWasPressed(int abilityNumber) {
		abilityButtons.flashButton(abilityNumber);
	}

	public void itemKeyWasPressed(int itemNumber) {
		itemButtons.flashButton(itemNumber);
	}

	public void statKeyWasPressed(HeroStat stat) { // TODO coupled to stat-button order
		if (stat == HeroStat.STRENGTH) {
			statButtons.flashButton(1);
		} else if (stat == HeroStat.DEXTERITY) {
			statButtons.flashButton(2);
		} else if (stat == HeroStat.INTELLIGENCE) {
			statButtons.flashButton(3);
		}
	}

//	private void addItemAtFirstNullIndex(ItemType newItem) {
//		int firstNullItemIndex = -1;
//		for (int i = 0; i < usableItems.size(); i++) {
//			if (usableItems.get(i) == null) {
//				firstNullItemIndex = i;
//				break;
//			}
//		}
//		if (firstNullItemIndex != -1) {
//			usableItems.set(firstNullItemIndex, newItem);
//		} else {
//			usableItems.add(newItem);
//		}
//	}

	private void setupBuyTowerButtons(GUIContext container) {
		Rectangle firstButtonRect = new Rectangle(LEFT_ROW_X, UPPER_ROW_Y, ICON_WIDTH, ICON_HEIGHT);
		buyTowerButtons = new TowerButtonRow(container, firstButtonRect, ICON_GAP, 6);
		buyTowerButtons.addListener(this);
		buttonRows.add(buyTowerButtons);
	}

	private void setupBuyItemButtons(GUIContext container) {
		Rectangle firstButtonRect = new Rectangle(RIGHT_ROW_X, UPPER_ROW_Y, ICON_WIDTH, ICON_HEIGHT);
		vendorButtons = new VendorButtonRow(container, firstButtonRect, ICON_GAP, 6);
		vendorButtons.addListener(this);
		buttonRows.add(vendorButtons);
	}

	private void setupAbilityButtons(GUIContext container) {
		Rectangle firstButtonRect = new Rectangle(LEFT_ROW_X, LOWER_ROW_Y, ICON_WIDTH, ICON_HEIGHT);
		abilityButtons = new AbilityButtonRow(container, firstButtonRect, ICON_GAP, maxAbilities);
		abilityButtons.addListener(this);
		buttonRows.add(abilityButtons);
	}

	private void setupItemButtons(GUIContext container) {
		Rectangle firstButtonRect = new Rectangle(RIGHT_ROW_X + (ICON_WIDTH + ICON_GAP) * 1, LOWER_ROW_Y, ICON_WIDTH, ICON_HEIGHT);
		itemButtons = new InventoryButtonRow(container, firstButtonRect, ICON_GAP, maxItems);
		itemButtons.addListener(this);
		buttonRows.add(itemButtons);
	}

	private void setupStatButtons(GUIContext container) {
		Rectangle firstButtonRect = new Rectangle(530, LOWER_ROW_Y, ICON_WIDTH, ICON_HEIGHT);
		statButtons = new ButtonRow(container, firstButtonRect, ICON_GAP, 3);
		statButtons.addStatButton(ResourceLoader.createImage("interface/redSquare.png"), HeroStat.STRENGTH, keyChars.strength);
		statButtons.addStatButton(ResourceLoader.createImage("interface/orangeSquare.png"), HeroStat.DEXTERITY, keyChars.dexterity);
		statButtons.addStatButton(ResourceLoader.createImage("interface/blueSquare.png"), HeroStat.INTELLIGENCE, keyChars.intelligence);
		statButtons.addListener(this);
		buttonRows.add(statButtons);
	}

	private boolean isMouseOverDialogs(Input input) {
		for (Dialog dialog : dialogs) {
			if (dialog.isMouseOver(input)) {
				return true;
			}
		}
		return false;
	}

	private void renderBackground(GUIContext container, Graphics g) {
		g.setColor(BACKGROUND);
		Rectangle HUD_rect = new Rectangle(0, 937, 1200, 350);
		g.fillRoundRect(HUD_rect.getX(), HUD_rect.getY(), HUD_rect.getWidth(), HUD_rect.getHeight(), 7);
		g.setColor(Color.white);
		g.drawRoundRect(HUD_rect.getX(), HUD_rect.getY(), HUD_rect.getWidth(), HUD_rect.getHeight(), 7);
	}

	private void renderButtons(GUIContext container, Graphics g) {
		for (ButtonRow buttonRow : buttonRows) {
			buttonRow.render(container, g);
		}
		buyTowerButtons.highlight(container, g, selectedTowerIndex, Color.red);
	}

	private void renderStats(GUIContext container, Graphics g) {

		g.setColor(new Color(0, 0, 0, 0.4f));
		g.fillRoundRect(430, 20, 280, 160, 15);
		g.setColor(Color.black);
		g.drawRoundRect(430, 20, 280, 160, 15);

		g.setColor(Color.white);
		g.setFont(bigFont);
		if (wavesHaveBegun) {
			g.drawString("Wave " + (waveIndex + 1), 470, 30);
		}
		g.setFont(NORMAL_FONT);

		if (wavesHaveBegun) {
			if (!waveHasMoreSpawns && !isFinalWave) {
				String extraString = waveIsCleared ? " press s to skip" : "";
				g.drawString("(" + secondsUntilNextWave + ")" + extraString, 470, 70);
			}
		} else {
			g.drawString("First wave in " + secondsUntilNextWave + "s.", 470, 48);
			g.drawString("press s to start", 470, 70);
		}

		if (!isHeroDead) {
			g.drawString("HERO:", 470, 100);
		} else {
			g.drawString("REVIVAL IN " + (int)(timeUntilHeroResurrection / 1000.0) + " s", 470, 100);
		}
		
		RenderUtil.renderStatBar(g, new Point(470, 120), new Dimension(200, 8), Color.black, new Color(170,0,0), heroPartHealth, 2);
		RenderUtil.renderStatBar(g, new Point(470, 130), new Dimension(200, 8), Color.black, new Color(0,0,170), heroPartMana, 2);
		g.setColor(Color.white);
		g.drawString("BASE:", 470, 140);
		RenderUtil.renderStatBar(g, new Point(470, 160), new Dimension(200, 8), Color.black, new Color(170,0,0), life / (double) maxLife, 2);

		g.setColor(Color.white);
		g.setFont(NORMAL_FONT);
		int heroStatsX = 545;
		g.drawString("Life: " + life + " / " + maxLife, heroStatsX, 955);
		g.drawString("Money: " + money, heroStatsX, 975);
		g.drawString("Max health: " + maxHealth, heroStatsX, 1005);
		g.drawString("Max mana: " + maxMana, heroStatsX, 1025);
		DecimalFormat df = new DecimalFormat("#.##");
		g.drawString("Armor: " + df.format(armor), heroStatsX, 1045);
		g.drawString("Movement speed: " + movementSpeed, heroStatsX, 1065);
		g.drawString("x dmg: " + df.format(damageMultiplier), heroStatsX, 1085);

		g.drawString("Unspent points: " + numAvailableStatpoints, heroStatsX, 1135);

		g.setColor(new Color(0.5f, 1f, 0.6f));
		g.drawString("" + heroStats.get(HeroStat.STRENGTH), heroStatsX + 4, LOWER_ROW_Y + ICON_WIDTH / 2 - 10);
		g.drawString("" + heroStats.get(HeroStat.DEXTERITY), heroStatsX + 4 + ICON_WIDTH + ICON_GAP, LOWER_ROW_Y + ICON_WIDTH / 2 - 10);
		g.drawString("" + heroStats.get(HeroStat.INTELLIGENCE), heroStatsX + 4 + 2 * (ICON_WIDTH + ICON_GAP), LOWER_ROW_Y + ICON_WIDTH / 2 - 10);

	}

	private void renderTowerMouseOver(GUIContext container, Graphics g) {
		if (renderTowerMouseOver) {
			g.setColor(new Color(0, 1, 0, 0.3f));
			g.fill(new Circle(towerMouseOverPixelCenterLocation.x, towerMouseOverPixelCenterLocation.y, towerMouseOverPixelAttackRange));
			g.setColor(new Color(Color.pink.r, Color.pink.g, Color.pink.b, 0.6f));
			g.fill(new Circle(towerMouseOverPixelCenterLocation.x, towerMouseOverPixelCenterLocation.y, towerMouseOverPixelSpecialRange));
		}
	}

	private void handleUpcomingAbility() {
		if (upcomingAbility != null) {
			int numAbilities = 0;
			for(AbilityType a : abilities){
				if(a != null){
					numAbilities++;
				}
			}
			if(numAbilities == maxAbilities){
				addAbilitySwapDialog(upcomingAbility);
			}else{
				for(MessageListener listener : listeners){
					listener.messageReceived(new Message(UserInputMessage.PRESSED_ADD_ABILITY, new IntMessageData(upcomingAbility.ordinal())));
				}
				
			}
			upcomingAbility = null;
		}
	}

	private TextBox createEnemyMouseOverText(Point enemyTopLeft, String text) {
		int width = 140;
		int height = 83;
		int space = 10;
		Rectangle tooltipRect = new Rectangle((int) enemyTopLeft.getX() - 45, (int) enemyTopLeft.getY() - height - space, width, height);
		return new TextBox(tooltipRect, text);
	}

//	private TextBox createTowerMouseOverText(Point towerTopLeft, String text) {
//		int width = 100;
//		int height = 22;
//		int space = 10;
//		Rectangle tooltipRect = new Rectangle((int) towerTopLeft.getX() - 31, (int) towerTopLeft.getY() - height - space, width, height);
//		return new TextBox(tooltipRect, text);
//	}
	
	

	@Override
	public void entityAttributeChanged(Entity entity, EntityAttribute attribute, double newValue) {
		switch(attribute){
		case MAX_HEALTH:
			maxHealth = (int) Math.round(newValue);
			break;
			
		case ARMOR:
			armor = newValue;
			break;	
		
		case DAMAGE:
			damageMultiplier = newValue;
			break;
			
		case MAX_MANA:
			maxMana = (int) Math.round(newValue);
			break;
			
		case MOVEMENT_SPEED:
			movementSpeed = (int) Math.round(newValue);
			break;
			
		default:
				
		}
	}


	
	
	

	

}
