package main;

import model.Constants;
import model.OligoGraph;
import model.OligoSystem;
import model.chemicals.SequenceVertex;
import utils.EdgeFactory;
import utils.VertexFactory;

public class Main {
static OligoGraph<SequenceVertex, String> graph;
	
	public static void initGraph(){
		final OligoGraph<SequenceVertex, String> g = new OligoGraph<SequenceVertex,String>();
	    g.initFactories(new VertexFactory<SequenceVertex>(g){
	    	
			public SequenceVertex create() {
				SequenceVertex newvertex = associatedGraph.popAvailableVertex();
				if (newvertex == null){
					newvertex = new SequenceVertex(associatedGraph.getVertexCount() + 1);
				} else {
					newvertex = new SequenceVertex(newvertex.ID);
				}
				return newvertex;
			}

			@Override
			public SequenceVertex copy(SequenceVertex original) {
				 SequenceVertex ret = new SequenceVertex(original.ID);
				 ret.inputs = original.inputs;
				 return ret;
			} 	
	    }, 
	    	new EdgeFactory<SequenceVertex,String>(g) {
    			public String createEdge(SequenceVertex v1, SequenceVertex v2) {
    				return v1.ID+"->"+v2.ID;
    			}
    			public String inhibitorName(String s) {
    				return "Inhib"+s;
    			}
	    });
	    	    
	    graph = g;
	}
	
	public static void config() {
	    Constants.numberOfPoints = 5000;
	    graph.saturableExo = true;
	    graph.saturableNick = true;
	    graph.saturablePoly = true;
	}
	
	public static void buildOscillator() {
		initGraph();
		config();
		
		int n = 3;
		SequenceVertex[] signal = new SequenceVertex[n];
		SequenceVertex[] inhibitor = new SequenceVertex[n];
		for (int i = 0; i < n; i++) {
			// add vertex
			signal[i] = graph.getVertexFactory().create();
			double initCons = (i == 0)? 5.0: 1.0;
			graph.addSpecies(signal[i], 1.0 / Constants.PadiracKSimpleDiv, initCons);
			signal[i].setInitialConcentration(initCons); // This line is necessary.
			
			// add activation
			String e = graph.getEdgeFactory().createEdge(signal[i], signal[i]);
			graph.addActivation(e, signal[i], signal[i]);
			
			//add inhibitor
			inhibitor[i] = graph.getVertexFactory().create();
			inhibitor[i].setInhib(true);
			graph.addSpecies(inhibitor[i], 1.0 / Constants.PadiracKInhibDiv, 0.0);
			inhibitor[i].setInitialConcentration(0.0); // This line is necessary.
			graph.addInhibition(e, inhibitor[i]);
		}
		// make loop
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			String e = graph.getEdgeFactory().createEdge(signal[i], inhibitor[j]);
			graph.addActivation(e, signal[i], inhibitor[j]);
		}
	}
	
	public static void main(String[] args) {	
		
		buildOscillator();
		
		OligoSystem<String> model = new OligoSystem<String>(graph);
		double[][] result = model.calculateTimeSeries(null);
		
		for (int t = 0; t< result[0].length; t++) {
			for (int i = 0; i < graph.getVertexCount(); i++) {
				System.out.print(result[i][t] + "\t");
			}
			System.out.println();
		}	
		System.out.println(args[1]);
	}
}
