package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import graphGenerator.GraphGenerator;
import graphGenerator.OscillatorGenerator;
import graphGenerator.RandomGraphGenerator;
import input.Input;
import model.Constants;
import model.OligoGraph;
import model.chemicals.SequenceVertex;

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
		
		Input input = Input.generateInput(config);
		for (int i = 0; i < input.data.size(); i++) {
			System.out.println(i + " " + input.data.get(i));
		}
	}
}
