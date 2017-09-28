package input;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.json.JsonArray;
import javax.json.JsonObject;

import util.SimulationManager;

public class Input {
	public String id;
	public int start, end, length;
	
	public File file;
	
	//TOTO: to double[]
	protected double[] data;

	public Input(JsonObject config) {
		id = config.getString("id");
		start = config.getInt("start");
		end = config.containsKey("end")? config.getInt("end"): SimulationManager.simulationTime;
		
		length = Math.max(end, SimulationManager.simulationTime);
		data = new double[length];
	}
	
	public double get(int i) { return data[i]; }
	
	public double[] getData(){ return data; }
	
	public Double[] getDataAsDouble(){ 
		Double[] array = new Double[length];
		for (int i = 0; i < array.length; i++) array[i] = new Double(data[i]);
		return array; 
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
		
		Input input = null;
		if (type.equals("random")) {			
			input =new RandomInput(inputConfig);
		}
		
		input.export(SimulationManager.caseDir + "/input_" + input.id + ".dat");

		return input;
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
