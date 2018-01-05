package test;

import util.MyOLSMultipleNonNegativeLinearRegression;

public class NonNegativeLinearRegressionTest {
	public static void main(String[] args) {			
		int n = 10, m = 3000;
		ExampleInMatLabDoc();
		AllPositiveBetaNoiseInY(n, m);		
		SomeNegativesInBeta(n, m);
		NoCorrelation(n,m);
	}
	
	private static void ExampleInMatLabDoc() {
		// https://jp.mathworks.com/help/matlab/ref/lsqnonneg.html
		double[] y = new double[] { 0.8587, 0.1781, 0.0747, 0.8405 };
		double[][] x = new double[4][];
		x[0] = new double[] { 0.0372, 0.2869 };
		x[1] = new double[] { 0.6861, 0.7071 };
		x[2] = new double[] { 0.6233, 0.6245 };
		x[3] = new double[] { 0.6344, 0.6170 };
		double[] b = new double[] { 0.0, 0.6929 };
		test(x, y, b, true);
	}
	
	private static void AllPositiveBetaNoiseInY(int n, int m) {
		double[] b = new double[n];
		for (int i = 0; i < n; i++) b[i] = Math.random() * 100.0;		// random value in [0:100]
		double[] y = new double[m];
		double[][] x = new double[m][];
		for (int i = 0; i < m; i++) {
			x[i] = new double[n - 1];
			y[i] = b[0];
			for (int j = 0; j < n - 1; j++) {
				x[i][j] = (2.0 * Math.random() - 1.0) * 100.0; // random value in [-100:100]
				y[i] += x[i][j] * b[j + 1];
			}
			y[i] *= 1.0 + (2.0 * Math.random() - 1.0) * 0.5; // add noise
		}
		test(x, y, b, false);
	}
	
	private static void SomeNegativesInBeta(int n, int m) {
		double[] b = new double[n];
		for (int i = 0; i < n; i++) b[i] = (2.0 * Math.random() - 1.0) * 100.0; // random value in [-100:100]
		double[] y = new double[m];
		double[][] x = new double[m][];
		for (int i = 0; i < m; i++) {
			x[i] = new double[n - 1];
			y[i] = b[0];
			for (int j = 0; j < n - 1; j++) {
				x[i][j] = (2.0 * Math.random() - 1.0) * 100.0; // random value in [-100:100]
				y[i] += x[i][j] * b[j + 1];
			}
		}
		test(x, y, b, false);
	}

	private static void NoCorrelation(int n, int m) {
		double[] y = new double[m];
		double[][] x = new double[m][];
		for (int i = 0; i < m; i++) {
			x[i] = new double[n - 1];
			for (int j = 0; j < n - 1; j++) {
				x[i][j] = (2.0 * Math.random() - 1.0) * 100.0; // random value in [-100:100]
			}
			y[i] = (2.0 * Math.random() - 1.0) * 100.0;
		}
		double[] b = new double[n];
		for (int i = 0; i < n; i++) b[i] = 0;
		test(x, y, b, false);

	}

	private static void test(double[][] x, double[] y, double[] b, boolean noIntercept) {
		MyOLSMultipleNonNegativeLinearRegression regression = new MyOLSMultipleNonNegativeLinearRegression();
		regression.setNoIntercept(noIntercept);
		regression.newSampleData(y, x);
		
		double[] beta = regression.estimateRegressionParameters();
		System.out.println("true beta\tbeta");
		for (int i = 0; i < beta.length; i++) System.out.println(b[i] + "\t" + beta[i]);
		
		double[] z = regression.calculateEstimatedValues();
//		System.out.println("y z");
//		for (int i = 0; i < y.length; i++) System.out.println(y[i] + " " + z[i]);
		System.out.println("NRMSE \n" + regression.calculateNRMSE());
		System.out.println();
	}
	

}
