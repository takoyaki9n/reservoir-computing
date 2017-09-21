package input;

import java.util.ArrayList;

import javax.json.JsonObject;

import main.SimulationConfig;

public class Input {
	private ArrayList<Double> data;
	
	static public Input generateInput(SimulationConfig config) {
		JsonObject inputConfig = config.json.getJsonObject("input");
		
		if (inputConfig.getString("type").equals("random")) {			
			return new RandomInput(inputConfig);
		}
		
		return new Input();
	}
	
	public double get(int i) {
		return data.get(i);
	}
	
	public ArrayList<Double> getData(){
		return data;
	}
}
