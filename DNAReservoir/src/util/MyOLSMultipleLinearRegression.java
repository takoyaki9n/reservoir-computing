package util;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class MyOLSMultipleLinearRegression extends OLSMultipleLinearRegression {
	protected RealVector beta = null;

	public MyOLSMultipleLinearRegression() { }

	public MyOLSMultipleLinearRegression(double threshold) {
		super(threshold);
	}
	
	@Override
	public void newSampleData(double[] y, double[][] x) throws MathIllegalArgumentException {
		addPerturbation(x);
		super.newSampleData(y, x);
	}
	
	private void addPerturbation(double[][] x) {
		double eps = 10.e-8;
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[i].length; j++) 
				x[i][j] = Math.max(0.0, x[i][j] + (2.0 * Math.random() - 1.0) * eps);
		}
	}
	
	@Override
	protected RealVector calculateBeta() {
		if (beta == null) beta = super.calculateBeta();
		return beta;
	}
	
	public double[] calculateEstimatedValues(RealMatrix xMatrix) {
        RealVector b = calculateBeta();
        return xMatrix.operate(b).toArray();
	}
	
	public double[] calculateEstimatedValues() {
		return calculateEstimatedValues(getX());
	}
	
	public double[] calculateEstimatedValues(double[][] x) {
        RealMatrix xMatrix = new Array2DRowRealMatrix(x);
        return calculateEstimatedValues(xMatrix);
	}
	
	public double calculateNRMSE(RealVector yVector, RealMatrix xMatrix) {		
		int n = xMatrix.getRowDimension();
		
		double[] z = calculateEstimatedValues(xMatrix);
		double zmn = z[0], zmx = z[0];
		for (int i = 0; i < n; i++) {
			zmn = Math.min(zmn, z[i]);
			zmx = Math.max(zmx, z[i]);
		}
		
		RealVector residual = yVector.subtract(new ArrayRealVector(z));
	    double rss = residual.dotProduct(residual);
	    
	    return Math.sqrt(rss / n) / Math.abs(zmx - zmn);
	}
	
	public double calculateNRMSE() {
		return calculateNRMSE(getY(), getX());
	}
	
	public double calculateNRMSE(double[] y, double[][] x) {		
		RealVector yVector = new ArrayRealVector(y); 
        RealMatrix xMatrix = new Array2DRowRealMatrix(x);
        return calculateNRMSE(yVector, xMatrix);
	}
}
