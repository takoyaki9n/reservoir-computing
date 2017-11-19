package evo;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import erne.AbstractFitnessFunction;
import erne.AbstractFitnessResult;
import input.Input;
import model.OligoSystemComplex;
import model.chemicals.SequenceVertex;
import model.input.ExternalInput;
import reactionnetwork.Node;
import reactionnetwork.ReactionNetwork;
import task.Task;
import util.MyOLSMultipleLinearRegression;

public class ReservoirFitnessFunction extends AbstractFitnessFunction {

	private static final long serialVersionUID = 1L;
	private String inputNodeName = "a";
	
	private String configString;
	private ArrayList<String> names;

	public ReservoirFitnessFunction(String configString) {
		this.configString = configString;
	}

	@Override
	public AbstractFitnessResult evaluate(ReactionNetwork network) {
		try {
			for (Node node : network.nodes) {
				if (node.type == Node.INHIBITING_SEQUENCE) {
					Node from ;
					Node to;
					if(node.name.contains("T")){
						String[] names = node.name.substring(1).split("T");
						from = network.getNodeByName(names[0]); // TODO: warning, very implementation dependent
						to = network.getNodeByName(names[1]);
					} else {
					from = network.getNodeByName(""+node.name.charAt(1)); // TODO: warning, very implementation dependent
					to = network.getNodeByName(""+node.name.charAt(2));
					}
					node.parameter = (double) 1 / 100 * Math.exp((Math.log(from.parameter) + Math.log(to.parameter)) / 2);
				}
			}

			JsonReader jsonReader = Json.createReader(new StringReader(configString));
			JsonObject config = jsonReader.readObject();  
			
			OligoSystemComplex oligoSystem = new OligoSystemComplex(network);
			
			// train
			MyOLSMultipleLinearRegression regression = train(oligoSystem, config);
			// test
			return test(oligoSystem, regression, config);
						
		} catch (Exception e) {
			e.printStackTrace();
			return minFitness();
		}	
	}
	
	private MyOLSMultipleLinearRegression train(OligoSystemComplex oligoSystem, JsonObject config) {
		HashMap<String, Input> inputs = Input.generateInputMap(config.getJsonArray("inputs"));
		for (Input input : inputs.values()) {
			attachInput(oligoSystem, input); // only one element actually.
		}
		Task task = Task.generateTask(config.getJsonObject("task"), inputs);
		
		Map<String, double[]> timeSeries = oligoSystem.calculateTimeSeries(erne.Constants.maxEvalClockTime);
		names = new ArrayList<>(timeSeries.keySet());
		
		double[][] result = trimTimeSeries(timeSeries, names, task);				
		double[] taskData = Arrays.copyOfRange(task.getData(), task.start, task.end);
		
		MyOLSMultipleLinearRegression regression = new MyOLSMultipleLinearRegression();
		regression.setNoIntercept(true);
		regression.newSampleData(taskData, result);
		
		return regression;
	}
	
	private ReservoirFitnessResult test(OligoSystemComplex oligoSystem, MyOLSMultipleLinearRegression regression, JsonObject config) {
		HashMap<String, Input> inputs = Input.generateInputMap(config.getJsonArray("inputs"));
		for (Input input : inputs.values()) {
			attachInput(oligoSystem, input); // only one element actually.
		}
		Task task = Task.generateTask(config.getJsonObject("task"), inputs);
		
		Map<String, double[]> timeSeries = oligoSystem.calculateTimeSeries(erne.Constants.maxEvalClockTime);
		
		double[][] result = trimTimeSeries(timeSeries, names, task);				
		double[] taskData = Arrays.copyOfRange(task.getData(), task.start, task.end);
		
		double[] estimated = regression.calculateEstimatedValues(result);
		
		return new ReservoirFitnessResult(taskData, estimated);
	}
	
	private double[][] trimTimeSeries(Map<String, double[]> timeSeries, ArrayList<String> names, Task task) {
		double[][] result = new double[task.end - task.start][];
		for (int t = task.start; t < task.end; t++) {
			int j = t - task.start;
			result[j] = new double[timeSeries.size()];
			for (int k = 0; k < names.size(); k++) result[j][k] = timeSeries.get(names.get(k))[t];
		}
		return result;
	}
	
	private void attachInput(OligoSystemComplex oligoSystem, Input input) {
		SequenceVertex vertex = oligoSystem.getEquiv().get(inputNodeName);
		try {
			File tmp = Files.createTempFile("input", ".dat").toFile();
			input.export(tmp.getPath());
			vertex.inputs.clear();
			vertex.inputs.add(new ExternalInput(input.getDataAsDouble(), tmp));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public AbstractFitnessResult minFitness() {
		return new ReservoirFitnessResult(true);
	}

}
