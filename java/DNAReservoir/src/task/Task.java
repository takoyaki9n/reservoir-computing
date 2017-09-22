package task;

import java.util.ArrayList;

import javax.json.JsonArray;
import javax.json.JsonObject;

import input.Input;

public class Task {
	protected ArrayList<Double> data;
	protected Input input;
	
	public int start, end, length;

	public Task(Input input) {
		this.input = input;
		length = input.length;
		data = new ArrayList<Double>(length);
	}
	
	public double get(int i) {
		return data.get(i);
	}
	
	public ArrayList<Double> getData(){
		return data;
	}
	
	static private Task generateTask(Input input, JsonObject taskConfig) {
		String type = taskConfig.getString("type");
		
		if (type.equals("A")) {
			return new TaskA(input);
		} else if (type.equals("B")) {
			return new TaskB(input, taskConfig);
		}
		
		return null;
	}
	
	static public ArrayList<Task> generateTaskArray(Input input, JsonArray tasksConfig) {		
		ArrayList<Task> tasks = new ArrayList<>();
		for (int i = 0; i < tasksConfig.size(); i++) {
			JsonObject taskConfig = tasksConfig.getJsonObject(i);
			tasks.add(generateTask(input, taskConfig));
		}
		return tasks;
	}
}
