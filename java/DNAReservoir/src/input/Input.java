package input;

import java.util.ArrayList;

import javax.json.JsonObject;

import main.SimulationConfig;

public class Input {
	public ArrayList<Double> data;
	
	static public Input generateInput(SimulationConfig config) {
		JsonObject inputConfig = config.json.getJsonObject("input");
		
		if (inputConfig.getString("type").equals("random")) {			
			return new RandomInput(inputConfig);
		}
		
		return new Input();
	}
}
