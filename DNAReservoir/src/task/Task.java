package task;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.json.JsonArray;
import javax.json.JsonObject;

import input.Input;

public class Task {
	public String id;
	public int start, end, length;
	
	protected double[] data;
	
	public Task(JsonObject config) {
		id = config.getString("id");
	}
	
	public double get(int i) { return data[i]; }
	
	public double[] getData(){ return data; }
	
	public Double[] getDataAsDouble() {
		Double[] array = new Double[length];
		for (int i = 0; i < length; i++) array[i] = new Double(data[i]);
		return array;
	}
	
	public void export(String fileName){
		try {
			FileWriter writer = new FileWriter(fileName);
			for (int t = 0; t< length; t++) {
				writer.write(get(t) + "\n");
			}
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	static public Task generateTask(JsonObject taskConfig, HashMap<String, Input> inputs) {
		String type = taskConfig.getString("type");
		
		Task task = null;
		if (type.equals("A")) {
			task = new TaskA(taskConfig, inputs);
		} else if (type.equals("B")) {
			task = new TaskB(taskConfig, inputs);
		} else if (type.equals("NARMA2")) {
			task = new TaskNARMA2(taskConfig, inputs);
		} else if (type.equals("NARMAn")) {
			task = new TaskNARMAn(taskConfig, inputs);
		} else if (type.equals("Controller")) {
			task = new TaskController(taskConfig, inputs);
		} 
		
		return task;
	}
	
	static public HashMap<String, Task> generateTaskMap(JsonArray tasksConfig, HashMap<String, Input> inputs) {		
		HashMap<String, Task> tasks = new HashMap<>();
		for (int i = 0; i < tasksConfig.size(); i++) {
			JsonObject taskConfig = tasksConfig.getJsonObject(i);
			tasks.put(taskConfig.getString("id"), generateTask(taskConfig, inputs));
		}
		return tasks;
	}
}
