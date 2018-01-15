package task;

import java.util.HashMap;

import javax.json.JsonObject;

import input.Input;

public class TaskController extends Task {
	protected Input input;
	
	private double alpha;
	private int delta;

	public TaskController(JsonObject config, HashMap<String, Input> inputs) {
		super(config);
		input = inputs.get(config.getString("input"));
		
		start = input.start;
		end = input.end;
		length = input.end;
		
		alpha = config.getJsonNumber("alpha").doubleValue();
		delta = config.getInt("delta");
		
		initializeData();
	}

	private void initializeData() {
		data = new double[length];
		
		double m = input.get(start);
		int q = 0, u = 0;
		for (int t = 0; t < length; t++) {
			data[t] = (q == 0)? 0.0: 1.0;
			if (start <= t && t < end) {
				if (q == 0) {
					if (input.get(t) + alpha < m) {
						q = 1; u = t;
					}
				} else {
					if (t - u >= delta) {
						q = 0; m = input.get(t);
					}
				}
			}
		}		
	}
}
