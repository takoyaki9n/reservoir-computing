package main;

import graphGenerator.GraphGenerator;
import graphGenerator.OscillatorGenerator;
import graphGenerator.RandomGraphGenerator;
import model.Constants;
import model.OligoGraph;
import model.chemicals.SequenceVertex;

public class Main {
	static int simulationTime = 5000;
	static GraphGenerator gen;
	static OligoGraph<SequenceVertex, String> graph;
			
	public static void main(String[] args) {
		String inputFileName = "", graphFileName = "", waveFileName = "";
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-i")) 
				inputFileName = args[i + 1];
			if (args[i].equals("-g")) 
				graphFileName = args[i + 1];
			if (args[i].equals("-o")) 
				waveFileName = args[i + 1];
			if (args[i].equals("-t"))
				simulationTime = Integer.parseInt(args[i + 1]);
		}
		
		config();
		
		gen = new OscillatorGenerator(3);
//		gen = new RandomGraphGenerator(4, 4, 4, 4);
		graph = gen.generateGraph();
		
		if (inputFileName.length() > 0)
			gen.importInput(inputFileName, gen.s1);

		if (graphFileName.length() > 0)
			gen.exportGraph(graphFileName);
		
		if (waveFileName.length() > 0)
			gen.executeSimulation(waveFileName);
		
		System.err.println(graph);
	}
		
	public static void config() {
	    Constants.numberOfPoints = simulationTime;
	}
}
