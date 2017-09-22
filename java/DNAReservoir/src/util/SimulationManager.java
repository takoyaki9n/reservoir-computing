package util;

import java.io.File;
import java.io.FileReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import model.Constants;

public class SimulationManager {
	public static JsonObject config;
	
	public static int repeat, simulationTime;
	
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
	}
}
