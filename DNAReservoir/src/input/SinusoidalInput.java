package input;

import javax.json.JsonObject;

/*
 * I(t) = A * (1 - cos(2πf1t/T)*cos(2πf2t/T)*cos(2πf3t/T))
 */
public class SinusoidalInput extends Input {
	private double A, f1, f2, f3, T;

	public SinusoidalInput(JsonObject config) {
		super(config);
		
		A = config.getJsonNumber("A").doubleValue();
		f1 = config.getJsonNumber("f1").doubleValue();
		f2 = config.getJsonNumber("f2").doubleValue();
		f3 = config.getJsonNumber("f3").doubleValue();
		T = config.getJsonNumber("T").doubleValue();
		
		initializeData();
	}
	
	private void initializeData() {
		for (int t = 0; t < length; t++) {
			double v = 0.0;
			if (start <= t && t < end) {
				v = A * (1.0
						+ Math.sin(2.0 * Math.PI * f1 * t / T)
						* Math.sin(2.0 * Math.PI * f2 * t / T)
						* Math.sin(2.0 * Math.PI * f3 * t / T));
			}
			data[t] = v;
		}		
	}

}
