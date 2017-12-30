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
		double eps = 5.0e-5;
		
		RealMatrix X = getX();
		RealVector y = getY();
		
		int n = getX().getColumnDimension();
		
		RealVector beta = new ArrayRealVector(n, 0.0);
		RealVector w = X.transpose().operate(y.subtract(X.operate(beta)));
		
		BitSet r = new BitSet(n);
		r.clear(); r.flip(0, n);
		
		while (true) {
			if (r.isEmpty()) break;
			int jw = 0;
			for (int j = 0; j < w.getDimension(); j++) 
				if (w.getEntry(jw) < w.getEntry(j)) jw = j;
			if (w.getEntry(jw) < eps) break;
			
			r.set(jw, false);
			
			RealVector s =  updateS(X, y, r);
			while (true) {
				int[] jps = getJps(r, n);
				
				double smn = s.getEntry(jps[0]);
				for (int k = 0; k < jps.length; k++) smn = Math.min(smn, s.getEntry(jps[k]));
				if (smn > 0) break;
				
				double alpha = getAlpha(beta, s, jps[0]);
				for (int k = 0; k < jps.length; k++) alpha = Math.min(alpha, getAlpha(beta, s, jps[k]));				
				
				beta = s.subtract(beta).mapMultiply(alpha);
				
				for (int k = 0; k < jps.length; k++) 
					if (beta.getEntry(jps[k]) == 0.0) r.set(jps[k]);
				
				s = updateS(X, y, r);
			}
			
			beta = s.copy();
			w = X.transpose().operate(y.subtract(X.operate(beta)));
		}
		
		return new ArrayRealVector(beta);
	}
	
	private RealVector updateS(RealMatrix X, RealVector y, BitSet r) {
		int m = X.getRowDimension();
		int n = X.getColumnDimension();
		int np = n - r.cardinality();
		int[] jps = getJps(r, n);
		
		RealMatrix Xp = new Array2DRowRealMatrix(m, np);
		for (int i = 0; i < m; i++) 
			for (int k = 0; k < jps.length; k++) 
				Xp.setEntry(i, k, X.getEntry(i, jps[k]));
		
		RealMatrix tXpXp = Xp.transpose().multiply(Xp);
		RealMatrix itXpXp = new LUDecomposition(tXpXp).getSolver().getInverse();
		RealVector sp = itXpXp.multiply(Xp.transpose()).operate(y);
		
		RealVector s = new ArrayRealVector(n, 0.0);
		for (int k = 0; k < jps.length; k++) s.setEntry(jps[k], sp.getEntry(k));
		return s;
	}
	
	private int[] getJps(BitSet r, int n) {
		int[] jps = new int[n - r.cardinality()];
		int k = 0;
		for (int j = 0; j < n; j++) 
			if (!r.get(j)) jps[k++] = j;
		return jps;
	}
	
	private double getAlpha(RealVector beta, RealVector s, int jp) {
		return beta.getEntry(jp) / (beta.getEntry(jp) - s.getEntry(jp));
	}
}
