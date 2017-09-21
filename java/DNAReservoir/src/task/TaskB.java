package task;

import javax.json.JsonObject;

import input.Input;

public class TaskB extends Task {
	private int interval;

	public TaskB(Input input, JsonObject config) {
		super(input);
		interval = config.getInt("interval");
		start = (int) (input.start + interval * 1.5);
		end = input.end;
		
		initializeData();
	}
	
	private void initializeData() {
		for (int t = 0; t < length; t++) {
			double v = 0.0;
			if (start <= t && t < end) {
				int t1 = t - interval;
				int t2 = (int) (t - interval * 1.5);
				v = input.get(t1) + 0.5 * input.get(t2);
			}
			data.add(v);
		}		
	}
}
