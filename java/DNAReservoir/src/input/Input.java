package input;

import java.util.ArrayList;

import javax.json.JsonObject;

import main.Main;
import main.SimulationConfig;

public class Input {
	protected ArrayList<Double> data;

	protected int inputLength;
	public int start, end;
	
	public Input(JsonObject config) {
		start = config.getInt("start");
		end = config.containsKey("end")? config.getInt("end"): Main.config.simulationTime;
		
		inputLength = Math.max(end, Main.config.simulationTime);
		data = new ArrayList<Double>(inputLength);
	}
	
	static public Input generateInput(SimulationConfig config) {
		JsonObject inputConfig = config.json.getJsonObject("input");
		
		if (inputConfig.getString("type").equals("random")) {			
			return new RandomInput(inputConfig);
		}
		
		return new Input(inputConfig);
	}
	
	public double get(int i) {
		return data.get(i);
	}
	
	public ArrayList<Double> getData(){
		return data;
	}
}
