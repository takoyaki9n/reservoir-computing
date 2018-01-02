package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class MyNNLS extends MyOLSMultipleLinearRegression {

	public MyNNLS() {}

	public MyNNLS(double threshold) { super(threshold); }
	
	@Override
	protected RealVector calculateBeta() {
		if (beta == null) beta = ScipyNNLS();
		return beta;
	}
	
	private RealVector ScipyNNLS() {
		RealMatrix X = getX();
		RealVector y = getY();
		
		int m = X.getRowDimension(), n = X.getColumnDimension();
		
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

			RealVector beta = new ArrayRealVector(n);
			for (int i = 0; i < n; i++) 
				beta.setEntry(i, Double.parseDouble(reader.readLine()));

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
	
	private static String createTmpFile(RealMatrix X, RealVector y) {
		try {
			File file = File.createTempFile("tmp", ".dat");
			FileWriter writer = new FileWriter(file);

			int m = X.getRowDimension();
			int n = X.getColumnDimension();
			writer.write(m + "\n");
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) writer.write(X.getEntry(i, j) + " ");
				writer.write("\n");
			}
			for (int i = 0; i < m; i++) writer.write(y.getEntry(i) + " ");
			writer.write("\n");

			writer.close();
			return file.getPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
