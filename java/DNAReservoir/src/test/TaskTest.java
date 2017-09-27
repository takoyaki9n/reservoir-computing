package test;

import java.util.HashMap;

import input.Input;
import task.Task;
import util.SimulationManager;

public class TaskTest {
	static public void run() {
		HashMap<String, Input> inputs = Input.generateInputMap(SimulationManager.config.getJsonArray("inputs"));
		
		HashMap<String, Task> tasks = Task.generateTaskMap(SimulationManager.config.getJsonArray("tasks"), inputs);
		
		for (int i = 0; i < SimulationManager.simulationTime; i++) {
			System.out.print(i + " ");
			for (String id : inputs.keySet()) System.out.print(inputs.get(id).get(i) + " ");
			System.out.println(tasks.get("A").get(i) + " " + tasks.get("B").get(i));
		}
	}
}
