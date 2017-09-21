package input;

import java.util.ArrayList;

import javax.json.JsonObject;

import main.Main;
import main.SimulationConfig;

public class Input {
	protected ArrayList<Double> data;

	public int start, end, length;
	
	public Input(JsonObject config) {
		start = config.getInt("start");
		end = config.containsKey("end")? config.getInt("end"): Main.config.simulationTime;
		
		length = Math.max(end, Main.config.simulationTime);
		data = new ArrayList<Double>(length);
	}
	
	public double get(int i) {
		return data.get(i);
	}
	
	public ArrayList<Double> getData(){
		return data;
	}
	
	static public Input generateInput(SimulationConfig config) {
		JsonObject inputConfig = config.json.getJsonObject("input");
		String type = inputConfig.getString("type");
		
		if (type.equals("random")) {			
			return new RandomInput(inputConfig);
		}
		
		return new Input(inputConfig);
	}
}
