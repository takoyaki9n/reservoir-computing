package input;

import java.util.ArrayList;

import javax.json.JsonArray;
import javax.json.JsonObject;

import util.SimulationManager;

public class Input {
	public String id;
	public int start, end, length;
	
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
	
	static public Input generateInput(JsonObject inputConfig) {
		String type = inputConfig.getString("type");
		
		if (type.equals("random")) {			
			return new RandomInput(inputConfig);
		}
		
		return null;
	}
	
	static public ArrayList<Input> generateInputArray(JsonArray inputsConfig) {
		ArrayList<Input> inputs = new ArrayList<>();
		for (int i = 0; i < inputsConfig.size(); i++) {
			JsonObject inputConfig = inputsConfig.getJsonObject(i);
			inputs.add(generateInput(inputConfig));
		}
		return inputs;
	}
	
}
