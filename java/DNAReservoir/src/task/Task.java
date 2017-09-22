package task;

import java.util.ArrayList;
import java.util.HashMap;

import javax.json.JsonArray;
import javax.json.JsonObject;

import input.Input;

public class Task {
	protected ArrayList<Double> data;
	
	public int start, end, length;
	
	public double get(int i) { return data.get(i); }
	
	public ArrayList<Double> getData(){ return data; }
	
	static private Task generateTask(JsonObject taskConfig, HashMap<String, Input> inputs) {
		String type = taskConfig.getString("type");
		
		if (type.equals("A")) {
			return new TaskA(taskConfig, inputs);
		} else if (type.equals("B")) {
			return new TaskB(taskConfig, inputs);
		}
		
		return null;
	}
	
	static public ArrayList<Task> generateTaskArray(JsonArray tasksConfig, HashMap<String, Input> inputs) {		
		ArrayList<Task> tasks = new ArrayList<>();
		for (int i = 0; i < tasksConfig.size(); i++) {
			JsonObject taskConfig = tasksConfig.getJsonObject(i);
			tasks.add(generateTask(taskConfig, inputs));
		}
		return tasks;
	}
}
