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
		
		RealMatrix X = getX();
		RealVector y = getY();
		
		int n = getX().getColumnDimension();
		
		RealVector beta = new ArrayRealVector(n, 0.0);
		RealVector w = X.transpose().operate(y.subtract(X.operate(beta))); // tX * (y - X * beta)
		
		BitSet p = new BitSet(n);
		p.clear();
		BitSet r = new BitSet(n);
		r.clear(); r.flip(0, n);
		
		double wmxp = -1.0;
		while (true) {
			if (r.isEmpty()) break;
			int jw = 0;
			for (int j = 0; j < w.getDimension(); j++) 
				if (w.getEntry(jw) < w.getEntry(j)) jw = j;
			double wmx = w.getEntry(jw);
			if (wmx <= eps) break;
			if (Math.abs(wmx - wmxp) <= eps * 1.0e-3) {
				System.err.println("w_max does not converge: " + wmx);
				break;
			}
			wmxp = wmx;
			p.set(jw); r.set(jw, false);
			
			RealVector s =  updateS(X, y, p);
			double smnp = 1.0;
			while (true) {
				int[] jps = getJps(p, n);
				
				double smn = s.getEntry(jps[0]);
				for (int k = 0; k < jps.length; k++) smn = Math.min(smn, s.getEntry(jps[k]));
				if (smn > 0) break;
				if (Math.abs(smn - smnp) <= eps * 1.0e-3) {
					System.err.println("s_min does not converge: " + smn);
					break;
				}
				smnp = smn;
				
				double alpha = getAlpha(beta, s, jps[0]);
				for (int k = 0; k < jps.length; k++) alpha = Math.min(alpha, getAlpha(beta, s, jps[k]));
				alpha = -alpha; // alpha = -min{betaj / (betaj - sj) | j in p}
				
				beta = beta.add(s.subtract(beta).mapMultiply(alpha)); // beta + (s - beta) * alpha
				
				for (int k = 0; k < jps.length; k++) {
					if (beta.getEntry(jps[k]) == 0.0) { p.set(jps[k], false); r.set(jps[k]); }
				}
				
				s = updateS(X, y, p);
			}
			
			beta = s.copy();
			w = X.transpose().operate(y.subtract(X.operate(beta))); // tX * (y - X * beta)
		}
		
		return new ArrayRealVector(beta);
	}
	
	private RealVector updateS(RealMatrix X, RealVector y, BitSet p) {
		int m = X.getRowDimension();
		int n = X.getColumnDimension();
		int np = p.cardinality();
		int[] jps = getJps(p, n);
		
		RealMatrix Xp = new Array2DRowRealMatrix(m, np);
		for (int i = 0; i < m; i++) 
			for (int k = 0; k < np; k++) 
				Xp.setEntry(i, k, X.getEntry(i, jps[k]));
		
		RealMatrix tXpXp = Xp.transpose().multiply(Xp);
		RealMatrix itXpXp = new LUDecomposition(tXpXp).getSolver().getInverse();
		RealVector sp = itXpXp.multiply(Xp.transpose()).operate(y); // s = ((tXp * Xp)^-1 * tXp) * y
		
		RealVector s = new ArrayRealVector(n, 0.0);
		for (int k = 0; k < np; k++) s.setEntry(jps[k], sp.getEntry(k));
		return s;
	}
	
	private int[] getJps(BitSet p, int n) {
		int[] jps = new int[p.cardinality()];
		int k = 0;
		for (int j = 0; j < n; j++) 
			if (p.get(j)) jps[k++] = j;
		return jps;
	}
	
	private double getAlpha(RealVector beta, RealVector s, int jp) {
		return beta.getEntry(jp) / (beta.getEntry(jp) - s.getEntry(jp));
	}
}
