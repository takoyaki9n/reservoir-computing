package test;

import org.apache.commons.math3.special.BesselJ;

import com.google.common.annotations.Beta;

import util.MyOLSMultipleNonNegativeLinearRegression;

public class NonNegativeLinearRegressionTest {
	public static void main(String[] args) {	
		double[] y = new double[] { 0.8587, 0.1781, 0.0747, 0.8405 };
		double[][] x = new double[4][];
		x[0] = new double[] { 0.0372, 0.2869 };
		x[1] = new double[] { 0.6861, 0.7071 };
		x[2] = new double[] { 0.6233, 0.6245 };
		x[3] = new double[] { 0.6344, 0.6170 };
		test(x, y, true);
		
		int n = 10, m = 3000;
		// all positive, noise
		double[] b = new double[n];
		for (int i = 0; i < n; i++) b[i] = Math.random() * 100.0;		
		y = new double[m];
		x = new double[m][];
		for (int i = 0; i < m; i++) {
			x[i] = new double[n - 1];
			y[i] = b[0];
			for (int j = 0; j < n - 1; j++) {
				x[i][j] = (2.0 * Math.random() - 1.0) * 100.0;
				y[i] += x[i][j] * b[j + 1];
			}
			y[i] *= 1.0 + (2.0 * Math.random() - 1.0) * 0.5;
		}
		System.out.println("\ntrue beta1");
		for (double p : b) System.out.println(p);
		test(x, y, false);
		
		// single negative, no noise
		b = new double[n];
		for (int i = 0; i < n - 1; i++) b[i] = Math.random() * 100.0;
		b[n - 1] = -Math.random() * 5.0;
		y = new double[m];
		x = new double[m][];
		for (int i = 0; i < m; i++) {
			x[i] = new double[n - 1];
			y[i] = b[0];
			for (int j = 0; j < n - 1; j++) {
				x[i][j] = (2.0 * Math.random() - 1.0) * 100.0;
				y[i] += x[i][j] * b[j + 1];
			}
//			y[i] *= 1.0 + (2.0 * Math.random() - 1.0) * 1.0e-3;
		}
		System.out.println("\ntrue beta2");
		for (double p : b) System.out.println(p);
		test(x, y, false);
		
		// random beta, no noise
		b = new double[n];
		for (int i = 0; i < n; i++) b[i] = Math.random() * 200.0 - 100.0;
		y = new double[m];
		x = new double[m][];
		for (int i = 0; i < m; i++) {
			x[i] = new double[n - 1];
			y[i] = b[0];
			for (int j = 0; j < n - 1; j++) {
				x[i][j] = (2.0 * Math.random() - 1.0) * 100.0;
				y[i] += x[i][j] * b[j + 1];
			}
//			y[i] *= 1.0 + (2.0 * Math.random() - 1.0) * 1.0e-3;
		}
		System.out.println("\ntrue beta3");
		for (double p : b) System.out.println(p);
		test(x, y, false);
		
		// random x y
		y = new double[m];
		x = new double[m][];
		for (int i = 0; i < m; i++) {
			x[i] = new double[n - 1];
			y[i] = b[0];
			for (int j = 0; j < n - 1; j++) {
				x[i][j] = (2.0 * Math.random() - 1.0) * 100.0;
				y[i] += (2.0 * Math.random() - 1.0) * 100.0;
			}
//			y[i] *= 1.0 + (2.0 * Math.random() - 1.0) * 1.0e-3;
		}
		System.out.println();
		test(x, y, false);
	}
	
	private static void test(double[][] x, double[] y, boolean noIntercept) {
		MyOLSMultipleNonNegativeLinearRegression regression = new MyOLSMultipleNonNegativeLinearRegression();
		regression.setNoIntercept(noIntercept);
		regression.newSampleData(y, x);
		
		double[] beta = regression.estimateRegressionParameters();
		System.out.println("beta");
		for (double p : beta) System.out.println(p);
		
		double[] z = regression.calculateEstimatedValues();
//		System.out.println("y z");
//		for (int i = 0; i < y.length; i++) System.out.println(y[i] + " " + z[i]);
		System.out.println("NRMSE \n" + regression.calculateNRMSE());
	}
}
