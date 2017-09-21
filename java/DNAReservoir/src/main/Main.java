package main;

import java.util.HashMap;

import graphGenerator.GraphGenerator;
import model.OligoGraph;
import model.chemicals.SequenceVertex;
import test.RandominputTest;

public class Main {
	public static SimulationConfig config;
	static GraphGenerator gen;
	static OligoGraph<SequenceVertex, String> graph;
	
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
		config = new SimulationConfig(opts.get("-c"));
		System.out.println(config.json);
		
		RandominputTest.run();
	}
}
