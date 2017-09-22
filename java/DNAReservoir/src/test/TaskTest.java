package test;

import java.util.ArrayList;

import input.Input;
import main.Main;
import task.Task;

public class TaskTest {
	static public void run() {
		Input input = Input.generateInput(Main.manager);
		ArrayList<Task> tasks = Task.generateTasks(input, Main.manager);
		for (int i = 0; i < Main.manager.simulationTime; i++) {
			System.out.println(i + " " + input.get(i) + " " + tasks.get(0).get(i) + " " + tasks.get(1).get(i));
		}
	}
}
