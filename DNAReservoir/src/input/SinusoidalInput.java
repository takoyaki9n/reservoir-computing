package input;

import javax.json.JsonObject;

public class SinusoidalInput extends Input {
	private int n;
	private double A, T, fmin, fmax;
	private double[] f;

	public SinusoidalInput(JsonObject config) {
		super(config);

		n = config.getInt("n");
		A = config.getJsonNumber("A").doubleValue();
		T = config.getJsonNumber("T").doubleValue();
		fmin = config.getJsonNumber("fmin").doubleValue();
		fmax = config.getJsonNumber("fmax").doubleValue();

		f = new double[n];
		for (int i = 0; i < n; i++) 
			f[i] = (fmax - fmin) * Math.random() + fmin;
		
		initializeData();
	}
	
	private void initializeData() {
		for (int t = 0; t < end; t++) {
			double v = 0.0;
			if (start <= t && t < end) {
				v = 1.0;
				for (int i = 0; i < n; i++) 
					v *= Math.cos(2.0 * Math.PI * f[i] * (t - start) / T);
				v = 0.5 * A * (1.0 - v);
			}
			data[t] = v;
		}		
	}

}
