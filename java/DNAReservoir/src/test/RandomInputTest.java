package test;

import input.Input;
import main.Main;
import util.SimulationManager;

public class RandomInputTest {
	static public void run() {
		Input input = Input.generateInput(SimulationManager.config.getJsonObject("input"));
		for (int i = 0; i < input.getData().size(); i++) {
			System.out.println(i + " " + input.get(i));
		}
	}
}
