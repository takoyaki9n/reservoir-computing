package util;

import java.util.BitSet;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class MyOLSMultipleNonNegativeLinearRegression extends MyOLSMultipleLinearRegression {

	public MyOLSMultipleNonNegativeLinearRegression() { }

	public MyOLSMultipleNonNegativeLinearRegression(double threshold) {
		super(threshold);
	}
	
	@Override
	protected RealVector calculateBeta() {
		if (beta == null) beta = NonNegativeLeastSquares();
		return beta;
	}
	
	private RealVector NonNegativeLeastSquares() {
		double eps = 5.0e-8;
		
		double[][] X = getX().getData();
		double[] y = getY().toArray();
		
		int n = getX().getColumnDimension();
		
		double[] beta = new double[n];
		for (int i = 0; i < beta.length; i++) beta[i] = 0.0;
		double[] w = new double[n];
		updateW(w, X, y, beta);
		
		BitSet r = new BitSet(n);
		r.clear(); r.flip(0, n);
		
		while (true) {
			if (r.isEmpty()) break;
			int jw = 0;
			for (int j = 0; j < w.length; j++) 
				if (w[jw] < w[j]) jw = j;
			if (w[jw] < eps) break;
			
			r.set(jw, false);
			double[] s = new double[n];
			updateS(s, X, y, r);
			while (true) {
				int[] jps = getJps(r, n);
				
				double smn = s[jps[0]];
				for (int k = 0; k < jps.length; k++) smn = Math.min(smn, s[jps[k]]);
				if (smn > 0) break;
				
				int jp = jps[0];
				double alpha = beta[jp] / (beta[jp] - s[jp]);
				for (int k = 0; k < jps.length; k++) {
					jp = jps[k];
					alpha = Math.min(alpha, beta[jp] / (beta[jp] - s[jp]));
				}
				
				for (int j = 0; j < n; j++) beta[j] += alpha * (s[j] - beta[j]);
				
				for (int k = 0; k < jps.length; k++) {
					jp = jps[k];
					if (beta[jp] == 0.0) r.set(jp);
				}
				
				updateS(s, X, y, r);
			}
			
			for (int j = 0; j < n; j++) beta[j] = s[j];
			updateW(w, X, y, beta);
		}
		
		return new ArrayRealVector(beta);
	}
	
	private void updateW(double[] w, double[][] X, double[] y, double[] beta) {
		int m = y.length;
		int n = w.length;
		
		double[] yXb = new double[m];
		for (int i = 0; i < m; i++) {
			yXb[i] = y[i];
			for (int j = 0; j < n; j++) yXb[i] -= X[i][j] * beta[j];
		}
		
		for (int j = 0; j < n; j++) {
			w[j] = 0.0;
			for (int i = 0; i < m; i++) w[j] += X[i][j] * yXb[i];
		}
	}
	
	private void updateS(double[] s, double[][] X, double[] y, BitSet r) {
		int m = y.length;
		int n = s.length;
		int[] jps = getJps(r, n);
		
		double[][] XpArray = new double[m][];
		for (int i = 0; i < m; i++) {
			XpArray[i] = new double[n - r.cardinality()];
			for (int k = 0; k < jps.length; k++) XpArray[i][k] = X[i][jps[k]];
		}
		RealMatrix Xp = new Array2DRowRealMatrix(XpArray);
		
		RealMatrix tXpXp = Xp.transpose().multiply(Xp);
		RealMatrix itXpXp = new LUDecomposition(tXpXp).getSolver().getInverse();
		RealMatrix Z = itXpXp.multiply(Xp.transpose());
		
		for (int j = 0; j < n; j++) s[j] = 0.0;
		for (int k = 0; k < jps.length; k++) {
			s[jps[k]] = 0.0;
			for (int i = 0; i < m; i++) s[jps[k]] += Z.getEntry(k, i) * y[i];			
		}
	}
	
	private int[] getJps(BitSet r, int n) {
		int[] jps = new int[n - r.cardinality()];
		int k = 0;
		for (int j = 0; j < n; j++) 
			if (!r.get(j)) jps[k++] = j;
		return jps;
	}
}
