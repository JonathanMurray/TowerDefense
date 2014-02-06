package xmlLoading;

import game.actions.Action;
import game.actions.AffectTargets;
import game.actions.CompositeAction;
import game.actions.CompositeActionUntilFail;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class ActionFactory extends XML_Factory{
	enum ActionId{
		AFFECT_TARGETS,
		COMPOSITE,
		COMPOSITE_UNTIL_FAIL
	}
	
	public Action createAction(Element element){
		
		ElementData data = getData(element);
		currentAttributesMap = data.attributes;
		ActionId id = ActionId.valueOf(data.type);
		switch(id){
		case AFFECT_TARGETS:
			return new AffectTargets(createSubTargetPicker(data.childElements), createSubEffect(data.childElements));
		case COMPOSITE:
			return new CompositeAction(createSubActions(data.childElements));
		case COMPOSITE_UNTIL_FAIL:
			return new CompositeActionUntilFail(createSubActions(data.childElements));
		
		}
		throw new IllegalArgumentException();
	}
}
