package task;

import input.Input;

public class TaskA extends Task {
	
	public TaskA(Input input) {
		super(input);
		start = input.start + 2;
		end = input.end;
		
		initializeData();
	}
	
	private void initializeData() {
		for (int t = 0; t < length; t++) {
			double v = 0.0;
			if (start <= t && t < end) {
				v = input.get(t - 1) + 2.0 * input.get(t - 2);
			}
			data.add(v);
		}		
	}
}
