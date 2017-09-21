package test;

import java.util.ArrayList;

import input.Input;
import main.Main;
import task.Task;

public class TaskTest {
	static public void run() {
		Input input = Input.generateInput(Main.config);
		ArrayList<Task> tasks = Task.generateTasks(Main.config, input);
		for (int i = 0; i < Main.config.simulationTime; i++) {
			System.out.println(i + " " + input.get(i) + " " + tasks.get(0).get(i));
		}
	}
}
