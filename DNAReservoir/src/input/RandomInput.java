package input;

import java.util.Random;

import javax.json.JsonObject;

public class RandomInput extends Input {
	private int interval;
	private double min, max;
	
	public RandomInput(JsonObject config) {
		super(config);
		interval = config.getInt("interval");
		min = config.getJsonNumber("min").doubleValue();
		max = config.getJsonNumber("max").doubleValue();
		
		initializeData();
	}
	
	private void initializeData() {
		double v = 0.0;
		for (int t = 0; t < length; t++) {
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
