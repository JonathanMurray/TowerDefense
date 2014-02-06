package game.actions;

import java.util.HashMap;
import java.util.Map;

/**
 * The context of the execution of an effect.
 * (Different context depending on in what scenario the effect is executed)
 * @author jonathan
 *
 */
public class Parameters {
	
	
	
	private Map<ParameterName, Object> map;
	
	public Parameters(){
		map = new HashMap<ParameterName, Object>();
	}
	
	/**
	 * for instance {"range":3}
	 * @param keysAndValues
	 */
	public Parameters(Map<ParameterName, Object> variableMap){
		this.map = variableMap;
	}
	
	public int get(ParameterName variableName, int defaultValue){
		if(map.containsKey(variableName)){
			if(map.get(variableName) instanceof Integer || map.get(variableName) instanceof Float || map.get(variableName) instanceof Double){
				return Math.round(((Number)map.get(variableName)).floatValue());
			}
			throw new IllegalArgumentException("Variable " + variableName + ", value=" + map.get(variableName) + 
					". Can't be cast to int");
		}
		return defaultValue;
	}
	
	public double getDouble(ParameterName variableName, double defaultValue){
		if(map.containsKey(variableName)){
			if(map.get(variableName) instanceof Double){
				return (double)map.get(variableName);
			}
			throw new IllegalArgumentException("Variable " + variableName + ", value=" + map.get(variableName) + 
					". Can't be cast to double");
		}
		return defaultValue;
	}
	
	public String getString(ParameterName variableName, String defaultValue){
		if(map.containsKey(variableName)){
			if(map.get(variableName) instanceof String){
				return (String)map.get(variableName);
			}
			throw new IllegalArgumentException("Variable " + variableName + ", value=" + map.get(variableName) + 
					". Can't be cast to String");
		}
		return defaultValue;
	}
	
	public Boolean getBoolean(ParameterName variableName, Boolean defaultValue){
		if(map.containsKey(variableName)){
			if(map.get(variableName) instanceof Boolean){
				return (Boolean)map.get(variableName);
			}
			throw new IllegalArgumentException("Variable " + variableName + ", value=" + map.get(variableName) + 
					". Can't be cast to BOolean");
		}
		return defaultValue;
	}
}
