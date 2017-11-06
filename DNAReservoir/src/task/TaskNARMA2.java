package task;

import java.util.HashMap;

import javax.json.JsonObject;

import input.Input;

public class TaskNARMA2 extends Task {
	protected Input input;
	
	private double alpha, beta, gamma, delta, A;

	public TaskNARMA2(JsonObject config, HashMap<String, Input> inputs) {
		super(config);
		
		input = inputs.get(config.getString("input"));
		
		alpha = config.getJsonNumber("alpha").doubleValue();
		beta = config.getJsonNumber("beta").doubleValue();
		gamma = config.getJsonNumber("gamma").doubleValue();
		delta = config.getJsonNumber("delta").doubleValue();
		A = config.getJsonNumber("A").doubleValue();
		
		start = input.start + 2;
		end = input.end;
		length = input.length;
		
		initializeData();
	}
	
	private void initializeData() {
		data = new double[length];
		for (int t = 0; t < length; t++) {
			double v = 0.0;
			if (start <= t && t < end) {
				double ipt = input.get(t - 1) - A;
				v = alpha * data[t - 1]
				 	+ beta * data[t - 1] * data[t - 2]
					+ gamma * ipt * ipt * ipt
					+ delta;
			}
			data[t] = v;
		}		
	}

}
