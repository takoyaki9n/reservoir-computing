package test;

import org.apache.spark.mllib.optimization.NNLS;

public class NNLSTest {
	public static void main(String[] args) {
		int n = 6, m = 300;
		ExampleInMatLabDoc();
		AllPositiveBetaNoiseInY(n, m);
		SomeNegativesInBeta(n, m);
		NoCorrelation(n, m);
	}
	
	private static void test(double[][] X, double[] y, double[] b) {		
		int m = X.length; int n = X[0].length;
		
		double[] Xty = new double[n];
		for (int i = 0; i < n; i++) {
			Xty[i] = 0.0;
			for (int j = 0; j < m; j++) Xty[i] += X[j][i] * y[j];
		}
		double[] XtX = new double[n * n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				XtX[n * i + j] = 0.0;
				for (int k = 0; k < m; k++) XtX[n * i + j] += X[k][i] * X[k][j];
			}
		}
		
		double[] beta = NNLS.solve(XtX, Xty, NNLS.createWorkspace(n));
		System.out.println("\ntrue beta\tbeta");
		for (int i = 0; i < beta.length; i++) System.out.println(b[i] + "\t" + beta[i]);

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
		test(x, y, b);
	}

	private static void AllPositiveBetaNoiseInY(int n, int m) {
		double[] b = new double[n];
		for (int i = 0; i < n; i++) b[i] = Math.random() * 100.0;		// random value in [0:100]
		double[] y = new double[m];
		double[][] x = new double[m][];
		for (int i = 0; i < m; i++) {
			x[i] = new double[n];
			x[i][0] = 1.0;
			y[i] = b[0];
			for (int j = 1; j < n; j++) {
				x[i][j] = (2.0 * Math.random() - 1.0) * 100.0; // random value in [-100:100]
				y[i] += x[i][j] * b[j];
			}
			y[i] *= 1.0 + (2.0 * Math.random() - 1.0) * 0.1; // add noise
		}
		test(x, y, b);
	}
	
	private static void SomeNegativesInBeta(int n, int m) {
		double[] b = new double[n];
		for (int i = 0; i < n; i++) b[i] = (2.0 * Math.random() - 1.0) * 100.0; // random value in [-100:100]
		double[] y = new double[m];
		double[][] x = new double[m][];
		for (int i = 0; i < m; i++) {
			x[i] = new double[n];
			x[i][0] = 1.0;
			y[i] = b[0];
			for (int j = 1; j < n; j++) {
				x[i][j] = (2.0 * Math.random() - 1.0) * 100.0; // random value in [-100:100]
				y[i] += x[i][j] * b[j];
			}
		}
		test(x, y, b);
	}
	
	private static void NoCorrelation(int n, int m) {
		double[] y = new double[m];
		double[][] x = new double[m][];
		for (int i = 0; i < m; i++) {
			x[i] = new double[n];
			x[i][0] = 1.0;
			for (int j = 1; j < n; j++) 
				x[i][j] = (2.0 * Math.random() - 1.0) * 100.0; // random value in [-100:100]
			y[i] = (2.0 * Math.random() - 1.0) * 100.0;
		}
		double[] b = new double[n];
		for (int i = 0; i < n; i++) b[i] = 0;
		test(x, y, b);
	}
}
