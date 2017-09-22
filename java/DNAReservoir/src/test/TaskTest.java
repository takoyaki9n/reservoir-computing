package test;

import java.util.ArrayList;

import input.Input;
import task.Task;
import util.SimulationManager;

public class TaskTest {
	static public void run() {
		ArrayList<Input> inputs = Input.generateInputArray(SimulationManager.config.getJsonArray("inputs"));
		Input input = inputs.get(0);
		ArrayList<Task> tasks = Task.generateTaskArray(input, SimulationManager.config.getJsonArray("tasks"));
		for (int i = 0; i < SimulationManager.simulationTime; i++) {
			System.out.println(i + " " + input.get(i) + " " + tasks.get(0).get(i) + " " + tasks.get(1).get(i));
		}
	}
}
