package task;

import java.util.HashMap;

import javax.json.JsonObject;

import input.Input;

public class TaskA extends Task {
	protected Input input;

	public TaskA(JsonObject config, HashMap<String, Input> inputs) {
		super(config);
		
		input = inputs.get(config.getString("input"));
		
		start = input.start + 2;
		end = input.end;
		length = input.end;
				
		initializeData();
	}
	
	private void initializeData() {
		data = new double[length];
		for (int t = 0; t < length; t++) {
			double v = 0.0;
			if (start <= t && t < end) {
				v = input.get(t - 1) + 2.0 * input.get(t - 2);
			}
			data[t] = v;
		}		
	}
}
