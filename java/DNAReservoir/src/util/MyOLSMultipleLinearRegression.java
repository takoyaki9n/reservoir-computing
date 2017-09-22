package util;
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
}
