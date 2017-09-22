package util;

import java.io.File;
import java.io.FileReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import model.Constants;

public class SimulationManager {
	public JsonObject config;
	
	public int repeat, simulationTime;
	
	public SimulationManager(String configFileName) {
		File jsonFile = new File(configFileName);
		try (JsonReader reader = Json.createReader(new FileReader(jsonFile))) {
			this.config = reader.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		repeat = config.getInt("repeat");
		simulationTime = config.getInt("simulation_time");
		Constants.numberOfPoints = simulationTime;
	}
	
}
