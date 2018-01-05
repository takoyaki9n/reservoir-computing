package task;

import java.util.HashMap;

import javax.json.JsonObject;

import input.Input;

public class TaskController extends Task {
	protected Input input;
	
	private double alpha;
	private int tau;

	public TaskController(JsonObject config, HashMap<String, Input> inputs) {
		super(config);
		input = inputs.get(config.getString("input"));
		
		start = input.start;
		end = input.end;
		length = input.end;
		
		alpha = config.getJsonNumber("alpha").doubleValue();
		tau = config.getInt("tau");
		
		initializeData();
	}

private void initializeData() {
	data = new double[length];
	
	double r = 0.0;
	int c = 0, q = 0;
	for (int t = 0; t < length; t++) {
		if (t == start) 
			r = input.get(t);
		else if (start < t && t < end) {
			if (q == 0) {
				if (input.get(t) + alpha <= r) {
					q = 1;
					c = 0;
				}
			} else {
				if (c >= tau) {
					q = 0;
					r = input.get(t);
				}
				c++;
			}
		}
		data[t] = (q == 0)? 0.0: 1.0;
	}		
}

}
