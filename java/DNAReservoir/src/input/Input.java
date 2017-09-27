package input;

import java.util.ArrayList;
import java.util.HashMap;

import javax.json.JsonArray;
import javax.json.JsonObject;

import util.SimulationManager;

public class Input {
	public String id;
	public int start, end, length;
	
	//TOTO: to double[]
	protected ArrayList<Double> data;

	public Input(JsonObject config) {
		id = config.getString("id");
		start = config.getInt("start");
		end = config.containsKey("end")? config.getInt("end"): SimulationManager.simulationTime;
		
		length = Math.max(end, SimulationManager.simulationTime);
		data = new ArrayList<Double>(length);
	}
	
	public double get(int i) { return data.get(i); }
	
	public ArrayList<Double> getData(){ return data; }
	
	public Double[] getDataAsArray() {
		Double[] array = new Double[length];
		return data.toArray(array);
	}
	
	static private Input generateInput(JsonObject inputConfig) {
		String type = inputConfig.getString("type");
		
		if (type.equals("random")) {			
			return new RandomInput(inputConfig);
		}
		
		return null;
	}
	
	static public HashMap<String, Input> generateInputMap(JsonArray inputsConfig) {
		HashMap<String, Input> inputs = new HashMap<>();
		for (int i = 0; i < inputsConfig.size(); i++) {
			JsonObject inputConfig = inputsConfig.getJsonObject(i);
			inputs.put(inputConfig.getString("id"), generateInput(inputConfig));
		}
		return inputs;
	}
	
}
