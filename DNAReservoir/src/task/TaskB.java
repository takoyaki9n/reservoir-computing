package task;

import java.util.HashMap;

import javax.json.JsonObject;

import input.Input;

public class TaskB extends Task {
	protected Input input;

	private int interval;

	public TaskB(JsonObject config, HashMap<String, Input> inputs) {
		super(config);
		
		input = inputs.get(config.getString("input"));
		
		interval = config.getInt("interval");
		start = (int) (input.start + interval * 1.5);
		end = input.end;
		length = input.end;
		
		initializeData();
	}
	
	private void initializeData() {
		data = new double[length];
		
		for (int t = 0; t < length; t++) {
			double v = 0.0;
			if (start <= t && t < end) {
				int t1 = t - interval;
				int t2 = (int) (t - interval * 1.5);
				v = input.get(t1) + 0.5 * input.get(t2);
			}
			data[t] = v;
		}		
	}
}
