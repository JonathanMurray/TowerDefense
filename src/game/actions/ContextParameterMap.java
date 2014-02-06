package game.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ContextParameterMap {
	
	private Map<ContextVariable, ParameterName> parameters;
	private Map<ContextVariable, Double> valueMultipliers;
	private Map<ContextVariable, Double> valueAdders;
	
	public ContextParameterMap(){
		parameters = new HashMap<>();
		valueMultipliers = new HashMap<>();
		valueAdders = new HashMap<>();
	}
	
	public void addMapping(ContextVariable contextVariable, ParameterName parameter, double valueMultiplier, double valueAdder){
		parameters.put(contextVariable, parameter);
		valueMultipliers.put(contextVariable, valueMultiplier);
		valueAdders.put(contextVariable, valueAdder);
	}
	
	public Parameters getParameters(ContextVariable contextVariableName, double contextVariableValue){
		HashMap map = new HashMap<>();
		map.put(contextVariableName, contextVariableValue);
		return getParameters(map);
	}
	
	public Parameters getParameters(Map<ContextVariable, Double> context){
		Map<ParameterName, Object> parametersMap = new HashMap<>();
		for(Entry<ContextVariable, Double> entry : context.entrySet()){
			ContextVariable contextVariable = entry.getKey();
			double contextVariableValue = entry.getValue();
			ParameterName parameterName = parameters.get(contextVariable);
			if(valueMultipliers.containsKey(contextVariable)){
				contextVariableValue *= valueMultipliers.get(contextVariable);
			}
			if(valueAdders.containsKey(contextVariable)){
				contextVariableValue += valueAdders.get(contextVariable);
			}
			parametersMap.put(parameterName, contextVariableValue);
		}
		return new Parameters(parametersMap);
	}
	
	public String toString(){
		return parameters.toString();
	}
}
