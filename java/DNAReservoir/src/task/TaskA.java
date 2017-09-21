package task;

import java.util.Random;

import input.Input;

public class TaskA extends Task {
	
	public TaskA(Input input) {
		super(input);
		start = input.start + 3;
		end = input.end;
		
		initializeData();
	}
	
	private void initializeData() {
		double v = 0.0;
		for (int t = 0; t < length; t++) {
			if (start <= t && t < end) {
				v = input.get(t - 1) + 2.0 * input.get(t - 2);
			} else {
				v = 0.0;
			}
			data.add(v);
		}		
	}
}
