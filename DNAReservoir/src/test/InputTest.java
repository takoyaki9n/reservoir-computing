package test;

import java.util.HashMap;

import input.Input;
import util.SimulationManager;

public class InputTest {
	static public void run() {
		HashMap<String, Input> inputs = Input.generateInputMap(SimulationManager.config.getJsonArray("inputs"));
		Input input = inputs.get("random1");
		for (int i = 0; i < input.getData().length; i++) {
			System.out.println(i + " " + input.get(i));
		}
	}
}
