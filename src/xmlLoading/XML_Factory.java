package xmlLoading;

import game.Game.Team;
import game.ResourceLoader;
import game.actions.Action;
import game.actions.ContextParameterMap;
import game.actions.ContextVariable;
import game.actions.ParameterName;
import game.actions.effects.Effect;
import game.actions.targetPickers.TargetPicker;
import game.buffs.Buff;
import game.objects.EntityAttribute;

import java.util.ArrayList;
import java.util.List;


import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class XML_Factory {
	
	class ElementData{
		String type;
		NamedNodeMap attributes;
		List<Element> childElements;
	}
	
	ElementData getData(Element e){
		ElementData d = new ElementData();
		d.type = e.getAttribute("type");
		d.attributes = e.getAttributes();
		d.childElements = getChildElements(e);
		return d;
	}
	
	Buff createSubBuff(List<Element> childElements){
		for(Element e : childElements){
			if(e.getTagName().equals("buff")){
				return new BuffFactory().createBuff(e);
			}
		}
		throw new IllegalArgumentException();
	}
	
	
	
	Buff[] createSubBuffs(List<Element> childElements){
		List<Buff> result = new ArrayList<>();
		for(Element e : childElements){
			if(e.getTagName().equals("buff")){
				result.add(new BuffFactory().createBuff(e));
			}
		}
		return result.toArray(new Buff[0]);
	}
	
	Action createSubAction(List<Element> childElements){
		for(Element e : childElements){
			if(e.getTagName().equals("action")){
				return new ActionFactory().createAction(e);
			}
		}
		throw new IllegalArgumentException();
	}
	
	Action[] createSubActions(List<Element> childElements){
		List<Action> result = new ArrayList<>();
		for(Element e : childElements){
			if(e.getTagName().equals("action")){
				result.add(new ActionFactory().createAction(e));
			}
		}
		return result.toArray(new Action[0]);
	}
	
	Effect createSubEffect(List<Element> childElements){
		for(Element element : childElements){
			if(element.getTagName().equals("effect")){
				List<Element> childELements = getChildElements(element);
				NamedNodeMap attributes = element.getAttributes();
				return new EffectFactory().createEffect(element);
			}
		}
		throw new IllegalArgumentException("");
	}
	
	Effect[] createSubEffects(List<Element> childElements){
		List<Effect> result = new ArrayList<>();
		for(Element e : childElements){
			if(e.getTagName().equals("effect")){
				result.add(new EffectFactory().createEffect(e));
			}
		}
		return result.toArray(new Effect[0]);
	}
	
	
	TargetPicker createSubTargetPicker(List<Element> childElements){
		for(Element e : childElements){
			if(e.getTagName().equals("target")){
				return new TargetPickerFactory().createTargetPicker(e.getAttribute("type"), e.getAttributes(), getChildElements(e));
			}
		}
		throw new IllegalArgumentException();
	}
	
	NamedNodeMap currentAttributesMap;
	
	
	
	String readStr(String attributeName){
		if(currentAttributesMap.getNamedItem(attributeName) != null){
			return currentAttributesMap.getNamedItem(attributeName).getTextContent();
		}
		throw new IllegalArgumentException("no attribute with name: " + attributeName);
	}
	
	String readStr(String attributeName, String defaultValue){
		if(currentAttributesMap.getNamedItem(attributeName) != null){
			return currentAttributesMap.getNamedItem(attributeName).getTextContent();
		}
		return defaultValue;
	}
	
	int read(String attributeName){
		if(currentAttributesMap.getNamedItem(attributeName) != null){
			return Integer.parseInt(currentAttributesMap.getNamedItem(attributeName).getTextContent());
		}
		throw new IllegalArgumentException("No attribute with name: " + attributeName);
	}
	
	int read(String attributeName, int defaultValue){
		if(currentAttributesMap.getNamedItem(attributeName) != null){
			return Integer.parseInt(currentAttributesMap.getNamedItem(attributeName).getTextContent());
		}
		return defaultValue;
	}
	
	double readDouble(String attributeName){
		return Double.parseDouble(currentAttributesMap.getNamedItem(attributeName).getTextContent());
	}
	
	double readDouble(String attributeName, double defaultValue){
		if(currentAttributesMap.getNamedItem(attributeName) != null){
			return Double.parseDouble(currentAttributesMap.getNamedItem(attributeName).getTextContent());
		}
		return defaultValue;
	}
	
	Team readTeam(String attributeName){
		return Team.valueOf(currentAttributesMap.getNamedItem(attributeName).getTextContent());
	}
	
	EntityAttribute readEntityAttr(String attributeName){
		return EntityAttribute.valueOf(currentAttributesMap.getNamedItem(attributeName).getTextContent());
	}
	
	Boolean readBoolean(String attributeName){
		return Boolean.parseBoolean(currentAttributesMap.getNamedItem(attributeName).getTextContent());
	}
	
	Boolean readBoolean(String attributeName, boolean defaultValue){
		if(currentAttributesMap.getNamedItem(attributeName) == null){
			return defaultValue;
		}
		return readBoolean(attributeName);
	}
	
	public Animation tryCreateAnimation(Element animationElementsParent){
		Element animationElement = (Element) animationElementsParent.getElementsByTagName("animation").item(0);
		if(animationElement == null){
			return null;
		}
		return createAnimation(animationElement);
	}
	
	static Animation createAnimation(List<Element> elements){
		for(Element e : elements){
			if(e.getTagName().equals("animation")){
				return createAnimation(e);
			}
		}
		return null;
	}
	
	
	
	public static Animation createAnimation(Element animationElement){
		int width = read(animationElement, "width", -1);
		int height = read(animationElement, "height", -1);
		int duration = read(animationElement, "duration", -1);
		boolean scaleToTile = read(animationElement, "scaleToTile", true);
		boolean pingPong = read(animationElement, "pingPong", false);
		boolean blinking = read(animationElement, "blinking", false);
		NodeList imageElements = animationElement.getElementsByTagName("image");
		ArrayList<String> imageRefsList = new ArrayList<>();
		for(int i = 0; i < imageElements.getLength(); i++){
			imageRefsList.add(imageElements.item(i).getTextContent());
		}
		String[] imageRefs = imageRefsList.toArray(new String[0]);
		
		if(width > 0){
			if(duration > 0){
				if(blinking){
					return ResourceLoader.createBlinkingAnimation(
							ResourceLoader.createScaledImage(imageRefs[0], width, height), 
							duration);
				}
				return ResourceLoader.createScaledAnimation(pingPong, width, height, duration, imageRefs);
				
			}
			if(blinking){
				return ResourceLoader.createBlinkingAnimation(
						ResourceLoader.createScaledImage(imageRefs[0], width, height));
			}
			return ResourceLoader.createScaledAnimation(pingPong, width, height, imageRefs);
		}else if(scaleToTile){
			if(duration > 0){
				return ResourceLoader.createTileScaledAnimation(pingPong, duration, imageRefs);
			}
			return ResourceLoader.createTileScaledAnimation(pingPong, imageRefs);
		}
		if(duration > 0){
			return ResourceLoader.createAnimation(pingPong, duration, imageRefs);
		}
		return ResourceLoader.createAnimation(pingPong, imageRefs);
	}
	
	public static ContextParameterMap createContextParameterMap(List<Element> elements){
		for(Element e : elements){
			if(e.getTagName().equals("contextParameterMap")){
				return createContextParameterMap(e);
			}
		}
		return null;
	}
	
	public static ContextParameterMap createContextParameterMap(Element contextParameterMapElement){
		ContextParameterMap map = new ContextParameterMap();
		NodeList mappingElements = contextParameterMapElement.getElementsByTagName("mapping");
		for(int i = 0; i < mappingElements.getLength(); i++){
			Element mappingElement = (Element) mappingElements.item(i);
			ContextVariable contextVariable = ContextVariable.valueOf(mappingElement.getAttribute("contextVariable"));
			ParameterName parameter = ParameterName.valueOf(mappingElement.getAttribute("parameter"));
			double valueMultiplier = 1;
			if(mappingElement.hasAttribute("valueMultiplier")){
				valueMultiplier = Double.parseDouble(mappingElement.getAttribute("valueMultiplier"));
			}
			double valueAdder = 0;
			if(mappingElement.hasAttribute("valueAdder")){
				valueAdder= Double.parseDouble(mappingElement.getAttribute("valueAdder"));
			}
			map.addMapping(contextVariable, parameter, valueMultiplier, valueAdder);
		}
		return map;
	}
	
	
	private static int read(Element element, String attributeName, int defaultValue){
		if(element.hasAttribute(attributeName)){
			return Integer.parseInt(element.getAttribute(attributeName));
		}
		return defaultValue;
	}
	
	private static boolean read(Element element, String attributeName, boolean defaultValue){
		if(element.hasAttribute(attributeName)){
			return Boolean.parseBoolean(element.getAttribute(attributeName));
		}
		return defaultValue;
	}
	
	private static double readDouble(Element element, String attributeName, double defaultValue){
		if(element.hasAttribute(attributeName)){
			return Double.parseDouble(element.getAttribute(attributeName));
		}
		return defaultValue;
	}
	
	private static float readFloat(Element element, String attributeName, float defaultValue){
		if(element.hasAttribute(attributeName)){
			return Float.parseFloat(element.getAttribute(attributeName));
		}
		return defaultValue;
	}
	
	static List<Element> getChildElements(Element e){
		List<Element> elements = new ArrayList<>();
		NodeList el = e.getChildNodes();
		for(int i = 0; i < el.getLength(); i++){
			if(el.item(i).getNodeType() == Node.ELEMENT_NODE){
				elements.add((Element) el.item(i));
			}
		}
		return elements;
		
	}
}
