package main;

import java.util.HashMap;

import test.GraphTest;
import util.SimulationManager;

public class Main {	
	public static HashMap<String, String> getOpts(String[] args){
		HashMap<String, String> opts = new HashMap<>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].charAt(0) == '-') {
				opts.put(args[i], args[i + 1]);
				i++;
			}
		}
		return opts;
	}
			
	public static void main(String[] args) {	
		HashMap<String, String> opts = getOpts(args);
		
		if (!opts.containsKey("-c")) {
			System.err.println("Config file is required.");
			System.exit(-1);
		}
		SimulationManager.loadConfig(opts.get("-c"));
		System.out.println(SimulationManager.config);
		
		GraphTest.run();
	}
}
