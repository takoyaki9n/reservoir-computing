package sample;

import java.io.File;
import java.io.FileReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class JsonSample {

	public static void main(String[] args) {
		String jsonFileName = args[0];
		File jsonFile = new File(jsonFileName);
		try (JsonReader reader = Json.createReader(new FileReader(jsonFile))) {
			JsonObject jobj = reader.readObject();
			int tau = jobj.getInt("tau");
			double input_max = jobj.getJsonNumber("input_max").doubleValue();
			System.out.println(tau);
			System.out.println(input_max);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
