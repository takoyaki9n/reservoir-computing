package test;

import graph.MyOligoGraph;
import graph.RandomGraph;
import model.OligoSystem;

public class GraphTest {
	public static void run() {
		MyOligoGraph graph = new RandomGraph(5, 5, 5, 5);
		graph.buildGraph();
		System.out.println(graph);
		System.out.println(graph.getVertexByID(1));
		
		OligoSystem<String> model = new OligoSystem<String>(graph);
		double[][] result = model.calculateTimeSeries(null);		
		for (int t = 0; t< result[0].length; t++) {
			for (int i = 0; i < graph.getVertexCount(); i++) {
				System.out.print(result[i][t] + "\t");
			}
			System.out.println();
		}
	}
}
