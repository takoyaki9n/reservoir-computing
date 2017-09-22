package test;

import java.util.ArrayList;

import input.Input;
import util.SimulationManager;

public class InputTest {
	static public void run() {
		ArrayList<Input> inputs = Input.generateInputArray(SimulationManager.config.getJsonArray("inputs"));
		Input input = inputs.get(0);
		for (int i = 0; i < input.getData().size(); i++) {
			System.out.println(i + " " + input.get(i));
		}
	}
}
