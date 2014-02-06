package xmlLoading;

import java.util.List;

import javax.jws.Oneway;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import xmlLoading.EffectFactory.EffectId;
import game.actions.targetPickers.FilterBelowHealthThreshold;
import game.actions.targetPickers.LimitNumberOfTargets;
import game.actions.targetPickers.TargetAdjacentEntities;
import game.actions.targetPickers.TargetEntitiesInRange;
import game.actions.targetPickers.TargetLineAhead;
import game.actions.targetPickers.TargetOneInFront;
import game.actions.targetPickers.TargetPicker;
import game.actions.targetPickers.TargetRectangleAhead;
import game.actions.targetPickers.TargetSelf;

public class TargetPickerFactory extends XML_Factory {
	
	enum TargetPickerId{
		ADJACENT,
		ONE_IN_FRONT,
		IN_RANGE,
		LINE_AHEAD,
		RECTANGLE_AHEAD,
		SELF,
		FILTER_BELOW_HEALTH
	}


	public TargetPicker createTargetPicker(String targetPickerId, NamedNodeMap attributes, List<Element> childElements){
		currentAttributesMap = attributes;
		TargetPickerId id = TargetPickerId.valueOf(targetPickerId);
		TargetPicker targetPicker;
		switch(id){
		case ADJACENT:
			targetPicker =  new TargetAdjacentEntities(createAnimation(childElements), readTeam("targetTeam"));
			break;
		case ONE_IN_FRONT:
			targetPicker =  new TargetOneInFront(createAnimation(childElements), readTeam("targetTeam"));
			break;
		case IN_RANGE:
			targetPicker = new TargetEntitiesInRange(readDouble("range"), createAnimation(childElements), readTeam("targetTeam"), readBoolean("includeActor", false));
			break;
		case LINE_AHEAD:
			targetPicker = new TargetLineAhead(read("distance"), read("skipNFirstSquares"), createAnimation(childElements), readTeam("targetTeam"));
			break;
		case RECTANGLE_AHEAD:
			targetPicker = new TargetRectangleAhead(read("skipNFirstSquares", 0), read("width"), read("length"), createAnimation(childElements), readTeam("targetTeam"));
			break;
		case SELF:
			targetPicker = new TargetSelf();
			break;
		case FILTER_BELOW_HEALTH:
			targetPicker = new FilterBelowHealthThreshold(createSubTargetPicker(childElements), read("healthThresholdPercent"));
			break;
		default:
				throw new IllegalArgumentException();
		}
		
		if(read("maxNumTargets", 0) > 0){
			return new LimitNumberOfTargets(targetPicker, read("maxNumTargets"));
		}
		return targetPicker;
		
	}
}
