package test;

import java.util.ArrayList;

import input.Input;
import task.Task;
import util.SimulationManager;

public class TaskTest {
	static public void run() {
		Input input = Input.generateInput(SimulationManager.config.getJsonObject("input"));
		ArrayList<Task> tasks = Task.generateTasks(input, SimulationManager.config.getJsonArray("tasks"));
		for (int i = 0; i < SimulationManager.simulationTime; i++) {
			System.out.println(i + " " + input.get(i) + " " + tasks.get(0).get(i) + " " + tasks.get(1).get(i));
		}
	}
}
