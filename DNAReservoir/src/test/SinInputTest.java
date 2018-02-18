package test;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import input.Input;

public class SinInputTest {
	public static void main(String[] args) {
		JsonObject config;
		File configFile = new File(args[0]);
		try (JsonReader reader = Json.createReader(new FileReader(configFile))) {
			config = reader.readObject();
			HashMap<String, Input> inputs = Input.generateInputMap(config.getJsonArray("inputs"));
			Input input = inputs.get("sin");
			for (int i = 0; i < input.getData().length; i++) {
				System.out.println(i + " " + input.get(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
