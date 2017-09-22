package sample;

import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class LinerRegressionSample {

	public LinerRegressionSample() {
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		// weight
		double[] y = new double[] { 50, 60, 65, 65, 70, 75, 80, 85, 90, 95 };
		// tall, 
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
	    
	    double[] z = new double[10];
	    for (int i = 0; i < z.length; i++) {
			z[i] = coe[0];
			for (int j = 1; j < coe.length; j++) z[i] += coe[j] * x[i][j - 1];
			System.out.println(y[i] + " " + z[i]);
		}
	    
	    double zmx = z[0], zmn = z[0];
	    for (int i = 1; i < z.length; i++) {
	    		zmx = Math.max(zmx, z[i]);
	    		zmn = Math.min(zmn, z[i]);
	    }
	    
	    double rss = regression.calculateResidualSumOfSquares();
	    double rmse = Math.sqrt(rss / y.length);
	    double nrmse = rmse / Math.abs(zmx - zmn);
	}
}
