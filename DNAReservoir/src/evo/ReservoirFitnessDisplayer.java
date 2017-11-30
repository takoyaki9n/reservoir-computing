package evo;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import erne.AbstractFitnessResult;
import erne.FitnessDisplayer;
import model.PlotFactory;
import use.math.gaussian.GaussianFitnessFunction;

public class ReservoirFitnessDisplayer implements FitnessDisplayer {
	
	private static final long serialVersionUID = 1L;

	public ReservoirFitnessDisplayer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public JPanel drawVisualization(AbstractFitnessResult fitness) {
		ReservoirFitnessResult fitnessResult = (ReservoirFitnessResult) fitness;
		if (fitnessResult.getFitness() != 0.0) {
			Map<String, double[]> timeSeries = new HashMap<String, double[]>();
			timeSeries.put("Fitted", fitnessResult.actualOutput);
			timeSeries.put("Target", fitnessResult.targetOutput);
			double[] xData = new double[fitnessResult.actualOutput.length];
			for (int k = 0; k < xData.length; k++) xData[k] = k;
			return new PlotFactory().createTimeSeriesPanel(timeSeries, xData, false, "Time [min]", "");
		}
		return new JPanel();		
	}

}
