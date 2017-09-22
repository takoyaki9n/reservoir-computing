package test;

import java.util.ArrayList;
import java.util.HashMap;

import input.Input;
import task.Task;
import util.SimulationManager;

public class TaskTest {
	static public void run() {
		HashMap<String, Input> inputs = new HashMap<>();
		for (Input input : Input.generateInputArray(SimulationManager.config.getJsonArray("inputs"))) {
			inputs.put(input.id, input);
		}
		
		ArrayList<Task> tasks = Task.generateTaskArray(SimulationManager.config.getJsonArray("tasks"), inputs);
		
		for (int i = 0; i < SimulationManager.simulationTime; i++) {
			System.out.print(i + " ");
			for (String id : inputs.keySet()) System.out.print(inputs.get(id).get(i) + " ");
			System.out.println(tasks.get(0).get(i) + " " + tasks.get(1).get(i));
		}
	}
}
