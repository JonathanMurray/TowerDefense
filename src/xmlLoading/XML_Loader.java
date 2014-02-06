package xmlLoading;

import game.AbilityData;
import game.Attack;
import game.DirectionSpriteSet;
import game.ItemData;
import game.RangedActionAttack;
import game.RangedAttack;
import game.ResourceLoader;
import game.SoundWrapper;
import game.WaveReward;
import game.WaveReward.ExclusiveItems;
import game.actions.Action;
import game.actions.effects.Effect;
import game.buffs.Buff;
import game.objects.HeroData;
import game.objects.TowerData;
import game.objects.HeroData.AbilityPair;
import game.objects.enemies.EnemyData;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


import org.newdawn.slick.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import applicationSpecific.AbilityType;
import applicationSpecific.EnemyType;
import applicationSpecific.HeroType;
import applicationSpecific.ItemType;
import applicationSpecific.TowerType;

public class XML_Loader {
	
	public static HashMap<TowerType, TowerData> loadTowerStats(String xmlFilePath) {
		HashMap<TowerType, TowerData> statsMap = new HashMap<>();
		NodeList types = getElements(xmlFilePath, "type");	
		for(int i = 0; i < types.getLength(); i++){
			Element towerType = (Element) types.item(i);
			RangedAttack attack = (RangedAttack)createAttack(towerType);
			Buff behaviourBuff = null;
			if(towerType.getElementsByTagName("buff").getLength() > 0){
				Element buffElement = (Element) towerType.getElementsByTagName("buff").item(0);
				if(buffElement.getParentNode() == towerType){
					behaviourBuff = new BuffFactory().createBuff(buffElement);
				}
			}
			double specialRange = 0;
			if(towerType.hasAttribute("specialRangeLink")){
				String xpath = towerType.getAttribute("specialRangeLink");
				try {
					XPath xpathObject =  XPathFactory.newInstance().newXPath();
					specialRange = Double.parseDouble((String)xpathObject.evaluate(xpath, towerType, XPathConstants.STRING));	
				} catch (XPathExpressionException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
			TowerType towerTypeID = TowerType.valueOf(towerType.getAttribute("id"));
			String tooltip = createTooltip(towerType);
			TowerData stats = new TowerData(
					towerType.getAttribute("name"),
					tooltip,
					Integer.parseInt(towerType.getAttribute("maxHealth")),
					Integer.parseInt(towerType.getAttribute("buildCost")),
					Integer.parseInt(towerType.getAttribute("unlockCost")),
					ResourceLoader.createTileScaledAnimation(false,towerType.getAttribute("sprite")),
					ResourceLoader.createImage(towerType.getAttribute("icon")),
					attack, 
					behaviourBuff,
					specialRange
			);
			statsMap.put(towerTypeID, stats);
		}
		return statsMap;
	}
	
	public static HashMap<EnemyType, EnemyData> loadEnemyStats(String xmlFilePath){
		HashMap<EnemyType, EnemyData> statsMap = new HashMap<>();
		NodeList types = getElements(xmlFilePath, "type");
		for(int i = 0; i < types.getLength(); i++){
			Element enemyType = (Element) types.item(i);
			DirectionSpriteSet spriteSet = createSpriteSet(enemyType.getAttribute("spriteNamingSystem"),
					enemyType.getAttribute("spriteBasePath"), enemyType.getAttribute("spriteFileType"));		
			int idleFrameIndex = 1;
			SoundWrapper deathSound = ResourceLoader.createSound(
					enemyType.getAttribute("deathSound"),
					Float.parseFloat(enemyType.getAttribute("deathSoundVolume"))
			);
			Buff behaviourBuff = null;
			if(enemyType.getElementsByTagName("buff").getLength() > 0){
				Element buffElement = (Element) enemyType.getElementsByTagName("buff").item(0);
				behaviourBuff = new BuffFactory().createBuff(buffElement);
			}
			Attack attack = createAttack(enemyType);
			String idString = enemyType.getAttribute("id");
			EnemyType enemyTypeID = EnemyType.valueOf(idString);
			final int DEFAULT_MAX_NUM_MINIONS = 2;
			EnemyData stats = new EnemyData(
					enemyType.getAttribute("name"),
					Integer.parseInt(enemyType.getAttribute("health")),
					Double.parseDouble(enemyType.getAttribute("armor")),
					attack,
					Integer.parseInt(enemyType.getAttribute("aggroRange")),
					Integer.parseInt(enemyType.getAttribute("movementCooldown")),
					spriteSet,
					idleFrameIndex,
					deathSound,
					Integer.parseInt(enemyType.getAttribute("moneyReward")),
					Integer.parseInt(enemyType.getAttribute("expReward")),
					behaviourBuff,
					enemyType.getAttribute("description"),
					read(enemyType, "maxNumMinions", DEFAULT_MAX_NUM_MINIONS),
					readBoolean(enemyType, "immuneToSlow", false),
					readBoolean(enemyType, "immuneToStun", false),
					readDouble(enemyType, "stunDurationMultiplier", 1)
					);
			statsMap.put(enemyTypeID, stats);
		}
		return statsMap;
	}
	

	
	public static HashMap<AbilityType, AbilityData> loadAbilityStats(String xmlFilePath) {
		HashMap<AbilityType, AbilityData> statsMap = new HashMap<>();
		NodeList types = getElements(xmlFilePath, "type");
		for(int i = 0; i < types.getLength(); i++){
			Element type = (Element) types.item(i);
			Element actionElement = (Element) type.getElementsByTagName("action").item(0);
			Action action = new ActionFactory().createAction(actionElement);
			AbilityType typeID = AbilityType.valueOf(type.getAttribute("id"));
			String tooltip = createTooltip(type);
			SoundWrapper sound = ResourceLoader.createSound(
					type.getAttribute("sound"),
					Float.parseFloat(type.getAttribute("soundVolume")));
			AbilityData stats = new AbilityData(
					action,
					type.getAttribute("name"),
					tooltip,
					ResourceLoader.createImage(type.getAttribute("icon")),
					(int) (Double.parseDouble(type.getAttribute("cooldown"))*1000),
					Integer.parseInt(type.getAttribute("manaCost")),
					sound
			);
			
			statsMap.put(typeID, stats);
		}
		return statsMap;
	}
	
	public static HashMap<ItemType, ItemData> loadItemStats(String xmlFilePath){
		HashMap<ItemType, ItemData> statsMap = new HashMap<>();
		NodeList types = getElements(xmlFilePath, "type");
		for(int i = 0; i < types.getLength(); i++){
			Element typeElement = (Element) types.item(i);
			Action action = null;
			if(typeElement.getElementsByTagName("action").getLength() > 0){
				Element actionElement = (Element) typeElement.getElementsByTagName("action").item(0);
				action = new ActionFactory().createAction(actionElement);
			}
			Buff itemBuff = null;
			if(typeElement.getElementsByTagName("buff").getLength() > 0 ){
				Element buffElement = (Element)typeElement.getElementsByTagName("buff").item(0);
				itemBuff= new BuffFactory().createBuff(buffElement);
			}
			
			ItemType typeID = ItemType.valueOf(typeElement.getAttribute("id"));
			String tooltip = createTooltip(typeElement);
			boolean isDroppedOnUse = true;
			if(typeElement.hasAttribute("dropOnUse")){
				isDroppedOnUse = Boolean.parseBoolean(typeElement.getAttribute("dropOnUse"));
			}
			
			ItemData stats = new ItemData(
					typeElement.getAttribute("name"), 
					tooltip, 
					action, itemBuff, isDroppedOnUse, 
					Integer.parseInt(typeElement.getAttribute("buyCost")), 
					ResourceLoader.createImage(typeElement.getAttribute("icon")), 
					Boolean.parseBoolean(typeElement.getAttribute("unique")));
			
			statsMap.put(typeID, stats);
		}
		return statsMap;
	}
	
	public static String createTooltip(Element element){
		String tooltip = element.getAttribute("tooltip");
		if(element.getElementsByTagName("links").getLength() == 0){
			return tooltip;
		}
		NodeList linkElements = ((Element) element.getElementsByTagName("links").item(0)).getElementsByTagName("link");
		int indexOfLink;
		XPath xPathObject = XPathFactory.newInstance().newXPath();
		while((indexOfLink = tooltip.indexOf('@')) != -1){
			String link = tooltip.substring(indexOfLink, indexOfLink + 2);
			int linkNumber = Integer.parseInt("" + link.charAt(1));
			String xPath = linkElements.item(linkNumber - 1).getTextContent();
			try {
				String linkedValue = (String)xPathObject.evaluate(xPath, element, XPathConstants.STRING);
				tooltip = tooltip.replaceAll(link, linkedValue);
			} catch (XPathExpressionException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		return tooltip;
	}

	
	public static HashMap<HeroType, HeroData> loadHeroStats(String xmlFilePath){
		HashMap<HeroType, HeroData> statsMap = new HashMap<>();
		NodeList types = getElements(xmlFilePath, "type");
		int idleFrameIndex = 1;
		
		for(int i = 0; i < types.getLength(); i++){
			Element typeElement = (Element) types.item(i);
			Element startAbilitiesElement = (Element) typeElement.getElementsByTagName("startAbilities").item(0);
			AbilityPair startAbilities = createAbilityPair(startAbilitiesElement);
			Element subsequentAbilitiesElement = (Element) typeElement.getElementsByTagName("subsequentAbilities").item(0);
			AbilityPair[] subsequentAbilities = createSubsequentAbilities(subsequentAbilitiesElement);
			DirectionSpriteSet spriteSet = createSpriteSet(typeElement.getAttribute("spriteNamingSystem"), 
					typeElement.getAttribute("spriteBasePath"), typeElement.getAttribute("spriteFileType"));
			HeroType typeID = HeroType.valueOf(typeElement.getAttribute("id"));
			Element startItemsElement = (Element) typeElement.getElementsByTagName("startItems").item(0);
			ItemType[] startItems = createItems(startItemsElement);
			HeroData stats = new HeroData(
					read(typeElement, "health", 0),
					read(typeElement, "healthRegenCooldown", 0), 
					read(typeElement, "armor", 0),
					read(typeElement, "mana", 0),
					read(typeElement, "manaRegenCooldown", 0),
					ResourceLoader.createSound(typeElement.getAttribute("birthSound"),
							read(typeElement, "birthSoundVolume", 1)),
					ResourceLoader.createSound(typeElement.getAttribute("birthSound"),
							read(typeElement, "deathSoundVolume", 1)),
					startAbilities,
					subsequentAbilities,
					startItems,
					spriteSet,
					idleFrameIndex,
					read(typeElement, "movementCooldown", 0),
					read(typeElement, "strength", 0),
					read(typeElement, "dexterity", 0),
					read(typeElement, "intelligence", 0));
			statsMap.put(typeID, stats);
		}
		return statsMap;
	}
	
	public static WaveReward[] loadWaveRewards(String xmlFilePath){
		HashMap<HeroType, HeroData> statsMap = new HashMap<>();
		NodeList rewardElements = getElements(xmlFilePath, "wave");
		List<WaveReward> waveRewards = new ArrayList<>();
		for(int i = 0; i < rewardElements.getLength(); i++){
			WaveReward reward = new WaveReward();
			Element rewardElement = (Element)rewardElements.item(i);
			NodeList items = rewardElement.getElementsByTagName("item");
			for(int j = 0; j < items.getLength(); j++){
				
				Element itemElement = (Element) items.item(j);
				if(itemElement.getParentNode() != rewardElement){
					continue;
					//THis is a nested item (inside an "exclusive"-node. They will be handled later
				}
				ItemType itemType = ItemType.valueOf(itemElement.getTextContent());
				double probability = itemElement.hasAttribute("probability")? 
						Double.parseDouble(itemElement.getAttribute("probability"))
						: 1;
				reward.items.put(itemType, probability);
			}
			NodeList exclusiveElements = rewardElement.getElementsByTagName("exclusive");
			for(int j = 0; j < exclusiveElements.getLength(); j++){
				Element exclusiveElement = (Element) exclusiveElements.item(j);
				NodeList exlusiveItemElements = exclusiveElement.getElementsByTagName("item");
				HashMap<ItemType, Double> exlusiveItemsMap = new HashMap<>();
				for(int k = 0; k < exlusiveItemElements.getLength(); k++){
					Element exlusiveItemElement = (Element) exlusiveItemElements.item(k);
					exlusiveItemsMap.put(
							ItemType.valueOf(exlusiveItemElement.getTextContent()), 
							Double.parseDouble(exlusiveItemElement.getAttribute("probability")));
				}
				reward.exclusiveItems.add(new WaveReward.ExclusiveItems(exlusiveItemsMap));
			}
			NodeList towers = rewardElement.getElementsByTagName("tower");
			for(int j = 0; j < towers.getLength(); j++){
				reward.towers.add(TowerType.valueOf(towers.item(j).getTextContent()));
			}
			if(rewardElement.getElementsByTagName("money").getLength() > 0);
			reward.money += Integer.parseInt(rewardElement.getElementsByTagName("money").item(0).getTextContent());
			waveRewards.add(reward);
		}
		
		return waveRewards.toArray(new WaveReward[0]);
	}
	
	
	private static int read(Element element, String attributeName, int defaultValue) {
		if(element.hasAttribute(attributeName)){
			return Integer.parseInt(element.getAttribute(attributeName));
		}
		return defaultValue;
		
	}
	
	private static boolean readBoolean(Element element, String attributeName, boolean defaultValue) {
		if(element.hasAttribute(attributeName)){
			return Boolean.parseBoolean(element.getAttribute(attributeName));
		}
		return defaultValue;
		
	}

	private static ItemType[] createItems(Element itemElementsParent){
		NodeList itemElements = itemElementsParent.getElementsByTagName("item");
		List<ItemType> itemsList = new ArrayList<>();
		for(int i = 0; i < itemElements.getLength(); i++){
			Element itemElement = (Element) itemElements.item(i);
			itemsList.add(ItemType.valueOf(itemElement.getTextContent()));
		}
		return itemsList.toArray(new ItemType[0]);
	}
	
	private static DirectionSpriteSet createSpriteSet(String spriteNamingSystem, String spriteBasePath, String spriteFileType){
		if(spriteNamingSystem.equals("NUMBER")){
			return ResourceLoader.createNumberEncodedDirectionSpriteSet(true, spriteBasePath, 3, spriteFileType);
		}else if(spriteNamingSystem.equals("DIRECTION")){
			return ResourceLoader.createDirectionEncodedDirectionSpriteSet(true,spriteBasePath, 3, spriteFileType);
		}else{
			new Exception("Unknown spriteNamingSystem-value in XML").printStackTrace();
			System.exit(0);
			return null;
		}
	}
	
	private static AbilityPair[] createSubsequentAbilities(Element choicesParent){
		NodeList choiceElements = choicesParent.getElementsByTagName("choice");
		List<AbilityPair> abilitiesList = new ArrayList<>();
		for(int i = 0; i < choiceElements.getLength(); i++){
			Element choiceElement = (Element) choiceElements.item(i);
			abilitiesList.add(createAbilityPair(choiceElement));
		}
		return abilitiesList.toArray(new AbilityPair[0]);
	}
	
	private static AbilityPair createAbilityPair(Element abilitiesParent){
		NodeList abilityElements = abilitiesParent.getElementsByTagName("ability");
		AbilityType firstAbility = AbilityType.valueOf(((Element)abilityElements.item(0)).getTextContent());
		AbilityType secondAbility = AbilityType.valueOf(((Element)abilityElements.item(1)).getTextContent());
		return new AbilityPair(firstAbility, secondAbility);
	}
	
	private static NodeList getElements(String xmlFilePath, String elementName){
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder docBuilder;
			docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse (new File(xmlFilePath));
			doc.normalize();
			return doc.getElementsByTagName(elementName);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		} 
		return null;
	}
	
	private static double readDouble(Element element, String attributeName, int defaultValue) {
		if(element.hasAttribute(attributeName)){
			return Double.parseDouble(element.getAttribute(attributeName));
		}
		return defaultValue;
	}

	private static Attack createAttack(Element type){
		Element attackElement = (Element)type.getElementsByTagName("attack").item(0);
		if(attackElement == null){
			return null;
		}
		SoundWrapper attackSound = ResourceLoader.createSound(attackElement.getAttribute("sound"),
				Float.parseFloat(attackElement.getAttribute("soundVolume")));
		String attackType = attackElement.getAttribute("type");
		Attack attack = null;
		int baseDamage = Integer.parseInt(attackElement.getAttribute("baseDamage"));
		int randomDamage = Integer.parseInt(attackElement.getAttribute("randomDamage"));
		int cooldown = Integer.parseInt(attackElement.getAttribute("cooldown"));
		if(attackType.equals("MELEE")){
			attack = new Attack(baseDamage, randomDamage, cooldown, attackSound);
		}else if(attackType.equals("RANGED") || attackType.equals("RANGED_ACTION")){
			double range = Double.parseDouble(attackElement.getAttribute("range"));
			int projectileSize = Integer.parseInt(attackElement.getAttribute("projectileSize"));
			Color projectileColor;
			if(attackElement.hasAttribute("projectileColor")){
				projectileColor = getColorFromString(attackElement.getAttribute("projectileColor"));
			}else{
				projectileColor = getColorFromStrings(
						attackElement.getAttribute("projectileColorR"),
						attackElement.getAttribute("projectileColorG"),
						attackElement.getAttribute("projectileColorB")
				);
			}
			double projectileSpeed = Double.parseDouble(attackElement.getAttribute("projectileSpeed"));
			SoundWrapper sound = ResourceLoader.createSound(
					attackElement.getAttribute("sound"), 
					Float.parseFloat(attackElement.getAttribute("soundVolume")));
			if(attackType.equals("RANGED")){
				attack = new RangedAttack(baseDamage, randomDamage, cooldown, range, 
					projectileSize, projectileColor, projectileSpeed, sound);
			}else{
				Element effectElement = (Element) attackElement.getElementsByTagName("effect").item(0);
				Effect effect = new EffectFactory().createEffect(effectElement);
				attack = new RangedActionAttack(baseDamage, randomDamage, cooldown, range, 
						projectileSize, projectileColor, projectileSpeed, effect, sound);
			}
		}else{
			new Exception("Unknown attackType-value in XML").printStackTrace();
		}
		return attack;
	}
	

	/*
	 * Arguments should be float numbers written as strings. for instance "0.6", "0" and "1"
	 */
	private static Color getColorFromStrings(String RComponent, String GComponent, String BComponent){
		return new Color(Float.parseFloat(RComponent), Float.parseFloat(GComponent), Float.parseFloat(BComponent));
	}

	private static Color getColorFromString(String colorName){
		try {
			Field field = Class.forName("org.newdawn.slick.Color").getField(colorName);
			return (Color)field.get(null);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
			return null;
		}	
	}
	
	

}
