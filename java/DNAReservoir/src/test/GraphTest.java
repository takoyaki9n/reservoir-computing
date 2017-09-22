package test;

import graphGenerator.MyOligoGraph;
import graphGenerator.RandomGraph;
import model.OligoSystem;

public class GraphTest {
	public static void run() {
		MyOligoGraph g = new RandomGraph(5, 5, 5, 5);
		g.buildGraph();
		System.out.println(g);
		
		OligoSystem<String> model = new OligoSystem<String>(g);
		double[][] result = model.calculateTimeSeries(null);		
		for (int t = 0; t< result[0].length; t++) {
			for (int i = 0; i < g.getVertexCount(); i++) {
				System.out.print(result[i][t] + "\t");
			}
			System.out.println();
		}
	}
}
