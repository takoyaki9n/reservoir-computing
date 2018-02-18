package evo;

import java.util.Map;

import erne.AbstractFitnessResult;
import freemarker.ext.rhino.RhinoScriptableModel;

public class ReservoirFitnessResult extends AbstractFitnessResult {

	private static final long serialVersionUID = 1L;
	
	private double fitness = 0.0;

	public Map<String, double[]> timeSeries;
	public double[] targetOutput;
	public double[] actualOutput;

	public ReservoirFitnessResult(double fitness) {
		this.fitness = fitness;
	}
	
	public ReservoirFitnessResult(Map<String, double[]> timeSeries, double[] targetOutput, double[] actualOutput) {
		this.timeSeries = timeSeries;
		this.targetOutput = targetOutput;
		this.actualOutput = actualOutput;
		this.fitness = calculateFitness();
	}
	
	private double calculateFitness() {
		int n = actualOutput.length;
		
	    double rss = 0.0;
		double zmn = actualOutput[0], zmx = actualOutput[0];
	    for (int i = 0; i < n; i++) {
			double res = actualOutput[i] - targetOutput[i];
			rss += res * res;
			zmn = Math.min(zmn, actualOutput[i]);
			zmx = Math.max(zmx, actualOutput[i]);
		}
	    
//	    fitness = Math.abs(zmx - zmn) / Math.sqrt(rss / n);
	    
	    fitness = Math.sqrt(n / rss);
	    
	    return fitness;
	}
	
	@Override
	public double getFitness() {
		return fitness;
	}
}
