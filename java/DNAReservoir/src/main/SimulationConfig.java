package main;

import java.io.File;
import java.io.FileReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import model.Constants;

public class SimulationConfig {
	public JsonObject json;
	
	public int repeat, simulationTime;
	
	public SimulationConfig(String configFileName) {
		File jsonFile = new File(configFileName);
		try (JsonReader reader = Json.createReader(new FileReader(jsonFile))) {
			this.json = reader.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		repeat = json.getInt("repeat");
		simulationTime = json.getInt("simulation_time");
		Constants.numberOfPoints = simulationTime;
	}
	
}
