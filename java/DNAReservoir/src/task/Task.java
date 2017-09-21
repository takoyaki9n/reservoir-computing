package task;

import java.util.ArrayList;

import javax.json.JsonArray;

import input.Input;
import main.SimulationConfig;

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
	
	static public ArrayList<Task> generateTasks(SimulationConfig config, Input input) {
		JsonArray taskConfigs = config.json.getJsonArray("tasks");
		
		ArrayList<Task> tasks = new ArrayList<>();
		for (int i = 0; i < taskConfigs.size(); i++) {
			String type = taskConfigs.getJsonObject(i).getString("type");
			if (type.equals("A")) {
				tasks.add(new TaskA(input));
			}
		}
		
		return tasks;
	}
}
