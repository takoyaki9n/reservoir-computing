package task;

import java.util.HashMap;

import javax.json.JsonObject;

import input.Input;

public class TaskNARMAn extends Task {
	protected Input input;
	
	private int n;
	private double alpha, beta, gamma, delta, A;

	public TaskNARMAn(JsonObject config, HashMap<String, Input> inputs) {
		super(config);
		
		input = inputs.get(config.getString("input"));
		
		n = config.getInt("n");
		alpha = config.getJsonNumber("alpha").doubleValue();
		beta = config.getJsonNumber("beta").doubleValue();
		gamma = config.getJsonNumber("gamma").doubleValue();
		delta = config.getJsonNumber("delta").doubleValue();
		A = config.getJsonNumber("A").doubleValue();

		start = input.start + n;
		end = input.end;
		length = input.length;
		
		initializeData();
	}
	
	private void initializeData() {
		data = new double[length];
		for (int t = 0; t < length; t++) {
			double v = 0.0;
			if (start <= t && t < end) {
				v = alpha * data[t - 1];
				double u = 0.0;
				for (int i = 0; i < n; i++) u += data[t - i - 1];
				v += beta * data[t - 1] * u;
				v += gamma * (input.get(t - n) - A) * (input.get(t - 1) - A);
				v += delta;
			}
			data[t] = v;
		}		
	}
}
