package test;

import util.MyOLSMultipleNonNegativeLinearRegression;

public class NonNegativeLinearRegressionTest {
	public static void main(String[] args) {	
		MyOLSMultipleNonNegativeLinearRegression regression = new MyOLSMultipleNonNegativeLinearRegression();
		
		double[] y = new double[] { 0.8587, 0.1781, 0.0747, 0.8405 };

		double[][] x = new double[4][];
		x[0] = new double[] { 0.0372, 0.2869 };
		x[1] = new double[] { 0.6861, 0.7071 };
		x[2] = new double[] { 0.6233, 0.6245 };
		x[3] = new double[] { 0.6344, 0.6170 };
		regression.setNoIntercept(true);
		regression.newSampleData(y, x);

		double[] beta = regression.estimateRegressionParameters();
		for (double p : beta) {
		    System.out.println(p);
		}
	}
}
