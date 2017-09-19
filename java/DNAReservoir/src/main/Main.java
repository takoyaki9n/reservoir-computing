package main;

import graphGenerator.GraphGenerator;
import graphGenerator.OscillatorGenerator;
import graphGenerator.RandomGraphGenerator;
import model.Constants;
import model.OligoGraph;
import model.chemicals.SequenceVertex;

public class Main {
	static GraphGenerator gen;
	static OligoGraph<SequenceVertex, String> graph;
			
	public static void main(String[] args) {
		String inputFileName = "", graphFileName = "", waveFileName = "";
		if (args.length >= 1) inputFileName = args[0];			
		if (args.length >= 2) graphFileName = args[1];			
		if (args.length >= 3) waveFileName = args[2];			
		
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
	    Constants.numberOfPoints = 1000;
	}
}
