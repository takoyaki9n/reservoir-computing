package evo;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import erne.Evolver;
import model.Constants;
import reactionnetwork.Library;

public class RunReservoir {
	public static HashMap<String, String> getOpts(String[] args){
		HashMap<String, String> opts = new HashMap<>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].charAt(0) == '-') {
				opts.put(args[i], args[i + 1]);
				i++;
			}
		}
		return opts;
	}

	public static void main(String[] args) {
		HashMap<String, String> opts = getOpts(args);

		if (opts.containsKey("-c")) {
			run(opts.get("-c"));
		} else if (opts.containsKey("-r")) {
			view(opts.get("-r"));
		} else {
			System.err.println("-c configFile or -r resultDir");
		}
	}
	
	public static void run(String configFileName) {
		File configFile = new File(configFileName);
		try (JsonReader reader = Json.createReader(new FileReader(configFile))) {
			JsonObject config = reader.readObject();
			String logDir = configFile.getParent();
			
			Constants.numberOfPoints = config.getInt("simulation_time");
			erne.Constants.maxEvalTime = config.getInt("simulation_time");
			erne.Constants.maxEvalClockTime = -1;
			erne.Constants.stabilityCheck = false;
	        
			ReservoirFitnessFunction fitnessFunction = new ReservoirFitnessFunction(config.toString());

			Evolver evolver;
			evolver = new Evolver(Library.startingMath, fitnessFunction, new ReservoirFitnessDisplayer());
			evolver.setGUI(true);
			evolver.evolve();
	        System.out.println("Evolution completed.");
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public static void view(String resultDir) {
		try {
			ReservoirFitnessFunction fitnessFunction = new ReservoirFitnessFunction("");
			Evolver evolver;
			evolver = new Evolver(Library.startingMath, fitnessFunction, new ReservoirFitnessDisplayer());
			evolver.setGUI(true);
			evolver.setReader(resultDir);
			evolver.evolve();
		}  catch (Exception e) {
			e.printStackTrace();
		}
	}
}
