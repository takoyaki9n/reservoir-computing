package test;

import util.MyOLSMultipleLinearRegression;

public class LinearRegressionTest {

	static public void run() {
		// estimate weights from heights and waist
		MyOLSMultipleLinearRegression regression = new MyOLSMultipleLinearRegression();
		// weight
		double[] y = new double[] { 50, 60, 65, 65, 70, 75, 80, 85, 90, 95 };
		// height, waist
		double[][] x = new double[10][];
		x[0] = new double[] { 165, 65 };
		x[1] = new double[] { 170, 68 };
		x[2] = new double[] { 172, 70 };
		x[3] = new double[] { 175, 65 };
		x[4] = new double[] { 170, 80 };
		x[5] = new double[] { 172, 85 };
		x[6] = new double[] { 183, 78 };
		x[7] = new double[] { 187, 79 };
		x[8] = new double[] { 180, 95 };
		x[9] = new double[] { 185, 97 };
		regression.newSampleData(y, x);
		 
		System.out.println(regression.calculateAdjustedRSquared() + "\n");
		
		double[] coe = regression.estimateRegressionParameters();
		for (double p : coe) {
		    System.out.println(p);
		}
	    System.out.println();
	    
	    double[] z = regression.calculateEstimatedValues();
	    
	    double zmx = z[0], zmn = z[0];
	    for (int i = 1; i < z.length; i++) {
			System.out.println(y[i] + " " + z[i]);
	    		zmx = Math.max(zmx, z[i]);
	    		zmn = Math.min(zmn, z[i]);
	    }
	    
	    double rss = regression.calculateResidualSumOfSquares();
	    System.out.println(Math.sqrt(rss / z.length) / Math.abs(zmx - zmn));
	    
	    System.out.println(regression.calculateNRMSE());	    
	}
}
