package util;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class MyOLSMultipleLinearRegression extends OLSMultipleLinearRegression {

	public MyOLSMultipleLinearRegression() {
	}

	public MyOLSMultipleLinearRegression(double threshold) {
		super(threshold);
	}
	
	public double[] calculateEstimatedValues() {
        RealVector b = calculateBeta();
        return getX().operate(b).toArray();
	}
	
	public double[] calculateEstimatedValues(double[][] x) {
        RealMatrix xMatrix = new Array2DRowRealMatrix(x);
        RealVector b = calculateBeta();
        return xMatrix.operate(b).toArray();
	}
	
	public double calculateNRMSE() {
		double[] z = calculateEstimatedValues();
		double zmn = z[0], zmx = z[0];
		for (int i = 0; i < z.length; i++) {
			zmn = Math.min(zmn, z[i]);
			zmx = Math.max(zmx, z[i]);
		}
		
		double n = getX().getRowDimension();
	    double rss = calculateResidualSumOfSquares();
	    
	    return Math.sqrt(rss / n) / Math.abs(zmx - zmn);
	}
	
	public double calculateNRMSE(double[] y, double[][] x) {		
		// TODO: redundant
		double n = getX().getRowDimension();
		
		double[] z = calculateEstimatedValues(x);
		double zmn = z[0], zmx = z[0];
		for (int i = 0; i < n; i++) {
			zmn = Math.min(zmn, z[i]);
			zmx = Math.max(zmx, z[i]);
		}
		
		RealVector yVector = new ArrayRealVector(y); 
		RealVector residual = yVector.subtract(new ArrayRealVector(z));
	    double rss = residual.dotProduct(residual);
	    
	    return Math.sqrt(rss / n) / Math.abs(zmx - zmn);
	}
}
