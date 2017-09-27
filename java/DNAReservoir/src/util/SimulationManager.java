package util;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import graph.MyOligoGraph;
import input.Input;
import model.Constants;
import model.OligoSystem;
import task.Task;

public class SimulationManager {
	public static int repeat, simulationTime;
	public static JsonObject config;
	
	public static String caseDir;
	
	public static void loadConfig(String configFileName) {
		File configFile = new File(configFileName);
		try (JsonReader reader = Json.createReader(new FileReader(configFile))) {
			config = reader.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		repeat = config.getInt("repeat");
		simulationTime = config.getInt("simulation_time");
		Constants.numberOfPoints = simulationTime;
		
		caseDir = configFile.getParent();
	}
	
	public static void run() {
		MyOligoGraph graph = MyOligoGraph.generateGraph(config.getJsonObject("graph"));
		graph.export(caseDir + "/graph.graph");
		
		JsonArray tasksConfig = config.getJsonArray("tasks");
		
		HashMap<String, Double[]> nrmses = new HashMap<>();		
		for (int i = 0; i < tasksConfig.size(); i++) 
			nrmses.put(tasksConfig.getJsonObject(i).getString("type"), new Double[repeat]);
		
		for (int r = 0; r < repeat; r++) {
			System.out.println("repeat: " + r);			
			HashMap<String, MyOLSMultipleLinearRegression> regressions = train(graph);
			HashMap<String, Double> nrmse = test(graph, regressions);
			for (String taskType : nrmse.keySet()) 
				nrmses.get(taskType)[r] = nrmse.get(taskType);
		}
		
		HashMap<String, Double> averages = new HashMap<>();		
		HashMap<String, Double> sigmas = new HashMap<>();		
		for (String taskType : nrmses.keySet()) {
			Double[] nrmseArray = nrmses.get(taskType);
			double average = 0.0;
			for (int i = 0; i < repeat; i++) 
				average += nrmseArray[i];
			average = average / repeat;
			double sigma = 0.0;
			for (int i = 0; i < repeat; i++) {
				double diff = nrmseArray[i] - average;
				sigma += diff * diff;
			}
			sigma = Math.sqrt(sigma / repeat);
			averages.put(taskType, average);
			sigmas.put(taskType, sigma);
		}
		
		for (String taskType : nrmses.keySet()) {
			System.out.println(taskType + ": " + averages.get(taskType) + "±" + sigmas.get(taskType));
		}
	}
	
	private static HashMap<String, MyOLSMultipleLinearRegression> train(MyOligoGraph graph) {
		HashMap<String, MyOLSMultipleLinearRegression> resressions = new HashMap<>();
		
		HashMap<String, Input> inputs = Input.generateInputMap(config.getJsonArray("inputs"));
		HashMap<String, Task> tasks = Task.generateTaskMap(config.getJsonArray("tasks"), inputs);
		graph.attachInputs(config.getJsonObject("graph"), inputs);
		
		double[][] result = executeSimulation(graph);
		for (String taskType : tasks.keySet()) {
			Task task = tasks.get(taskType);
			double[][] resultTrimed = Arrays.copyOfRange(result, task.start, task.end);
			double[] taskTrinmed = Arrays.copyOfRange(task.getDataAsArray(), task.start, task.end);
			
			MyOLSMultipleLinearRegression regression = new MyOLSMultipleLinearRegression();
			regression.setNoIntercept(true);
			regression.newSampleData(taskTrinmed, resultTrimed);

			resressions.put(taskType, regression);
		}
		
		return resressions;
	}
	
	private static double[][] executeSimulation(MyOligoGraph graph) {
		OligoSystem<String> model = new OligoSystem<String>(graph);
		double[][] result = model.calculateTimeSeries(null);		
		double[][] resultT = new double[simulationTime][];
		for (int t = 0; t< simulationTime; t++) {
			resultT[t] = new double[graph.getVertexCount() + 1];
			resultT[t][0] = 1.0;
			for (int i = 0; i < graph.getVertexCount(); i++) 
				resultT[t][i + 1] = result[i][t];
		}
		return resultT;
	}
	
	private static HashMap<String, Double> test(MyOligoGraph graph, HashMap<String, MyOLSMultipleLinearRegression> regressions) {
		HashMap<String, Double> nrmses = new HashMap<>();
		
		HashMap<String, Input> inputs = Input.generateInputMap(config.getJsonArray("inputs"));
		HashMap<String, Task> tasks = Task.generateTaskMap(config.getJsonArray("tasks"), inputs);
		graph.attachInputs(config.getJsonObject("graph"), inputs);

		double[][] result = executeSimulation(graph);
		for (String taskType : tasks.keySet()) {
			Task task = tasks.get(taskType);
			double[][] resultTrimed = Arrays.copyOfRange(result, task.start, task.end);
			double[] taskTrinmed = Arrays.copyOfRange(task.getDataAsArray(), task.start, task.end);
			
			MyOLSMultipleLinearRegression regression = regressions.get(taskType);
			
			nrmses.put(taskType, regression.calculateNRMSE(taskTrinmed, resultTrimed));
		}

		return nrmses;
	}
}
