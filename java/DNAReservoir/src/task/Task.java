package task;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.json.JsonArray;
import javax.json.JsonObject;

import input.Input;

public class Task {
	public int start, end, length;
	
	//TOTO: to double[]
	protected ArrayList<Double> data;
	
	public double get(int i) { return data.get(i); }
	
	public ArrayList<Double> getData(){ return data; }
	
	public double[] getDataAsArray() {
		double[] array = new double[length];
		for (int i = 0; i < length; i++) {
			array[i] = data.get(i).doubleValue();
		}
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

	static private Task generateTask(JsonObject taskConfig, HashMap<String, Input> inputs) {
		String type = taskConfig.getString("type");
		
		if (type.equals("A")) {
			return new TaskA(taskConfig, inputs);
		} else if (type.equals("B")) {
			return new TaskB(taskConfig, inputs);
		}
		
		return null;
	}
	
	static public HashMap<String, Task> generateTaskMap(JsonArray tasksConfig, HashMap<String, Input> inputs) {		
		HashMap<String, Task> tasks = new HashMap<>();
		for (int i = 0; i < tasksConfig.size(); i++) {
			JsonObject taskConfig = tasksConfig.getJsonObject(i);
			tasks.put(taskConfig.getString("type"), generateTask(taskConfig, inputs));
		}
		return tasks;
	}
}
