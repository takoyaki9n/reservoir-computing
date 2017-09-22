package test;

import input.Input;
import main.Main;

public class RandominputTest {
	static public void run() {
		Input input = Input.generateInput(Main.manager);
		for (int i = 0; i < input.getData().size(); i++) {
			System.out.println(i + " " + input.get(i));
		}
	}
}
