package graphGenerator;

import model.Constants;
import model.OligoGraph;
import model.chemicals.SequenceVertex;

public class OscillatorGenerator extends GraphGenerator {
	private int n;
	public OscillatorGenerator(int n) {
		this.n = n;
	}
	
	public OligoGraph<SequenceVertex, String> generateGraph(){
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
		
		return graph;
	}
}
