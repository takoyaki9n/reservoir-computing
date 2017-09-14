package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import edu.uci.ics.jung.algorithms.layout.Layout;
import graphGenerator.GraphGenerator;
import graphGenerator.OscillatorGenerator;
import graphGenerator.RandomGraphGenerator;
import graphical.frame.DataPanel;
import graphical.frame.MyVisualizationServer;
import model.Constants;
import model.OligoGraph;
import model.OligoSystem;
import model.SlowdownConstants;
import model.chemicals.SequenceVertex;
import model.input.AbstractInput;
import model.input.ExternalInput;
import model.input.PulseInput;
import utils.CodeGenerator;
import utils.EdgeFactory;
import utils.SequenceVertexComparator;
import utils.VertexFactory;

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
