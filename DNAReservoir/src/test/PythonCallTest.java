package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class PythonCallTest {
	public static void main(String[] args) {
		int n = 6, m = 3000;
		ExampleInMatLabDoc();
		AllPositiveBetaNoiseInY(n, m);
		SomeNegativesInBeta(n, m);
		NoCorrelation(n, m);
	}

	private static void test(double[][] X, double[] y, double[] b) {
		double[] beta = CallScipyNNLS(X, y);
		System.out.println("true beta\tbeta");
		for (int i = 0; i < beta.length; i++)
			System.out.println(b[i] + "\t" + beta[i]);
		System.out.println();
	}

	private static double[] CallScipyNNLS(double[][] X, double[] y) {
		String code = "import numpy as np\n" + 
				"from scipy.optimize import nnls\n" + 
				"import sys\n" + 
				"f = open(sys.argv[1], 'r')\n" + 
				"m = int(f.readline())\n" + 
				"A = np.array([[float(x) for x in f.readline().rstrip().split(' ')] for _ in range(m)])\n" + 
				"b = np.array([float(x) for x in f.readline().rstrip().split(' ')])\n" + 
				"f.close()\n" + 
				"(x, rnorm) = nnls(A,b)\n" + 
				"print('\\n'.join([str(d) for d in x]))";
		code = code.replace('\n', ';');
		
		try {
			ProcessBuilder builder = new ProcessBuilder("python", "-c", code, createTmpFile(X, y));
			Process process = builder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader ereader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			process.waitFor();

			for (String line = ereader.readLine(); line != null; line = ereader.readLine())
				System.err.println(line);

			double[] beta = new double[X[0].length];
			for (int i = 0; i < beta.length; i++)
				beta[i] = Double.parseDouble(reader.readLine());

			reader.close();
			process.destroy();
			return beta;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String createTmpFile(double[][] X, double[] y) {
		try {
			File file = File.createTempFile("tmp", ".dat");
			FileWriter writer = new FileWriter(file);

			int m = X.length;
			int n = X[0].length;
			writer.write(X.length + "\n");
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++)
					writer.write(X[i][j] + " ");
				writer.write("\n");
			}
			for (int i = 0; i < m; i++)
				writer.write(y[i] + " ");
			writer.write("\n");

			writer.close();
			return file.getPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
		for (int i = 0; i < n; i++)
			b[i] = Math.random() * 100.0; // random value in [0:100]
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
		for (int i = 0; i < n; i++)
			b[i] = (2.0 * Math.random() - 1.0) * 100.0; // random value in [-100:100]
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
		for (int i = 0; i < n; i++)
			b[i] = 0;
		test(x, y, b);
	}
}
