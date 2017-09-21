package task;

import java.util.ArrayList;

import javax.json.JsonObject;

import input.Input;
import input.RandomInput;
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
	
	static Task generateTask(Input input) {
		//TODO: implement
		return null;
	}
}
