package util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import graph.MyOligoGraph;
import input.Input;
import model.Constants;
import model.OligoSystem;
import task.Task;

public class SimulationManager {
	public static JsonObject config;
	public static int repeat, simulationTime;
	public static String caseDir;
	
	private static HashMap<String, ArrayList<ArrayList<Double>>> betaLog;
	private static HashMap<String, ArrayList<Double>> nrmseLog;
	private static String graphFileName;
	
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
		graphFileName = caseDir + "/" + config.getJsonObject("graph").getString("type") + ".graph";
		graph.export(graphFileName);
		
		initLogs();
		
		for (int r = 0; r < repeat; r++) {
			System.out.println("repeat: " + r);			
			HashMap<String, MyOLSMultipleLinearRegression> regressions = train();
			HashMap<String, Double> nrmses = test(regressions);
			updateLog(regressions, nrmses);
		}
		
		exportLog();
	}
	
	private static void initLogs() {
		betaLog = new HashMap<>();
		nrmseLog = new HashMap<>();
				
		JsonArray tasksConfig = config.getJsonArray("tasks");
		for (int i = 0; i < tasksConfig.size(); i++) {
			String taskId = tasksConfig.getJsonObject(i).getString("id");
			betaLog.put(taskId, new ArrayList<ArrayList<Double>>(repeat));
			nrmseLog.put(taskId, new ArrayList<Double>(repeat));
		}
	}
	
	private static HashMap<String, MyOLSMultipleLinearRegression> train() {
		HashMap<String, MyOLSMultipleLinearRegression> resressions = new HashMap<>();
		
		HashMap<String, Input> inputs = Input.generateInputMap(config.getJsonArray("inputs"));
		HashMap<String, Task> tasks = Task.generateTaskMap(config.getJsonArray("tasks"), inputs);
		MyOligoGraph graph = MyOligoGraph.open(graphFileName);
		graph.attachInputs(config.getJsonObject("graph"), inputs);
		
		double[][] result = executeSimulation(graph);
		for (String taskId : tasks.keySet()) {
			Task task = tasks.get(taskId);
			double[][] resultTrimed = Arrays.copyOfRange(result, task.start, task.end);
			double[] taskTrinmed = Arrays.copyOfRange(task.getData(), task.start, task.end);
			
			MyOLSMultipleLinearRegression regression = new MyOLSMultipleLinearRegression();
			regression.setNoIntercept(true);
			regression.newSampleData(taskTrinmed, resultTrimed);

			resressions.put(taskId, regression);
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
		exportWaves(caseDir + "/waves.dat", resultT, graph);
		return resultT;
	}
	
	private static void exportWaves(String waveFileName, double[][] result, MyOligoGraph graph) {
		try {
			FileWriter writer = new FileWriter(waveFileName);
			for (int t = 0; t< simulationTime; t++) {
				for (int i = 0; i < graph.getVertexCount(); i++) 
					writer.write(result[t][i + 1] + "\t");
				writer.write("\n");				
			}
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private static HashMap<String, Double> test(HashMap<String, MyOLSMultipleLinearRegression> regressions) {
		HashMap<String, Double> nrmses = new HashMap<>();
		
		HashMap<String, Input> inputs = Input.generateInputMap(config.getJsonArray("inputs"));
		HashMap<String, Task> tasks = Task.generateTaskMap(config.getJsonArray("tasks"), inputs);
		MyOligoGraph graph = MyOligoGraph.open(graphFileName);
		graph.attachInputs(config.getJsonObject("graph"), inputs);

		double[][] result = executeSimulation(graph);
		for (String taskId : tasks.keySet()) {
			Task task = tasks.get(taskId);
			double[][] resultTrimed = Arrays.copyOfRange(result, task.start, task.end);
			double[] taskTrinmed = Arrays.copyOfRange(task.getData(), task.start, task.end);
			
			MyOLSMultipleLinearRegression regression = regressions.get(taskId);
			
			nrmses.put(taskId, regression.calculateNRMSE(taskTrinmed, resultTrimed));
		}

		return nrmses;
	}
	
	private static void updateLog(HashMap<String, MyOLSMultipleLinearRegression> regressions, HashMap<String, Double> nrmses) {
		for (String taskType : betaLog.keySet()) {
			MyOLSMultipleLinearRegression regression = regressions.get(taskType);
			double[] beta = regression.estimateRegressionParameters();
			ArrayList<Double> betaArray = new ArrayList<>(beta.length);
			for (int i = 0; i < beta.length; i++) betaArray.add(beta[i]);
			betaLog.get(taskType).add(betaArray);
			nrmseLog.get(taskType).add(nrmses.get(taskType));
		}
	}
	
	private static void exportLog() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		for (String taskType : nrmseLog.keySet()) {
			ArrayList<Double> nrmseArray = nrmseLog.get(taskType);
			ArrayList<ArrayList<Double>> betaArray = betaLog.get(taskType);
			
			double average = 0.0;
			for (int i = 0; i < repeat; i++) average += nrmseArray.get(i);
			average = average / repeat;
			
			double sigma = 0.0;
			for (int i = 0; i < repeat; i++) {
				double diff = nrmseArray.get(i) - average;
				sigma += diff * diff;
			}
			sigma = Math.sqrt(sigma / repeat);
			
			JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
			for (int i = 0; i < repeat; i++) {
				JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
				objectBuilder.add("nrmse", nrmseArray.get(i));
				objectBuilder.add("beta", Json.createArrayBuilder(betaArray.get(i)));
				arrayBuilder.add(objectBuilder);
			}			

			JsonObjectBuilder taskBuilder = Json.createObjectBuilder();
			taskBuilder.add("average", average);
			taskBuilder.add("sigma", sigma);
			taskBuilder.add("log", arrayBuilder);
			builder.add(taskType, taskBuilder);
		}

		try {
			FileWriter writer = new FileWriter(caseDir + "/log.json");
			writer.write(builder.build().toString());
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
