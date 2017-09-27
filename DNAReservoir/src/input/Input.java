package input;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.json.JsonArray;
import javax.json.JsonObject;

import util.SimulationManager;

public class Input {
	public String id;
	public int start, end, length;
	
	public File file;
	
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
	
	public void export(String fileName){
		file = new File(fileName);
		try {
			FileWriter writer = new FileWriter(file);
			for (int t = 0; t< length; t++) {
				writer.write(get(t) + "\n");
			}
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
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
