package input;

import java.util.ArrayList;
import java.util.Random;

import javax.json.JsonObject;

import main.Main;

public class RandomInput extends Input {
	private int start, end, interval;
	private double min, max;
	
	public RandomInput(JsonObject config) {
		start = config.getInt("start");
		end = config.containsKey("end")? config.getInt("end"): Main.config.simulationTime;
		interval = config.getInt("interval");
		min = config.getJsonNumber("min").doubleValue();
		max = config.getJsonNumber("max").doubleValue();
		
		initializeData();
	}
	
	private void initializeData() {
		int inputLength = Math.max(end, Main.config.simulationTime);
		data = new ArrayList<>();
		double v = 0.0;
		for (int t = 0; t < inputLength; t++) {
			if (t < end) {
				if (t >= start && (t - start) % interval == 0) {
					double rnd = new Random().nextDouble();
					v = (max - min) * rnd + min;
				}
			} else {
				v = 0.0;				
			}
			data.add(v);
		}		
	}
}
