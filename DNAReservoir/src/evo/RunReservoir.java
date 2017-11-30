package evo;

import java.io.File;
import java.io.FileReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import erne.Evolver;
import model.Constants;
import reactionnetwork.Library;

public class RunReservoir {
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Config file is required.");
			System.exit(-1);
		}
		
		String configFileName = args[0];
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
}
