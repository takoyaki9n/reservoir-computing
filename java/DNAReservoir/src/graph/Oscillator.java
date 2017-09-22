package graph;

import javax.json.JsonObject;

import model.Constants;
import model.chemicals.SequenceVertex;

public class Oscillator extends MyOligoGraph {

	private static final long serialVersionUID = -3112551794846151943L;

	private int n;
	
	public Oscillator(int n) {
		this.n = n;
	}
	
	public Oscillator(JsonObject config) {
		this(config.getInt("n"));
	}
	
	public void buildGraph() {
		SequenceVertex[] signal = new SequenceVertex[n];
		SequenceVertex[] inhibitor = new SequenceVertex[n];
		for (int i = 0; i < n; i++) {
			// add vertex
			signal[i] = getVertexFactory().create();
			double initCons = (i == 0)? 5.0: 1.0;
			addSpecies(signal[i], 1.0 / Constants.PadiracKSimpleDiv, initCons);
			signal[i].setInitialConcentration(initCons); // This line is necessary.
			
			// add activation
			String e = getEdgeFactory().createEdge(signal[i], signal[i]);
			addActivation(e, signal[i], signal[i]);
			
			//add inhibitor
			inhibitor[i] = getVertexFactory().create();
			inhibitor[i].setInhib(true);
			addSpecies(inhibitor[i], 1.0 / Constants.PadiracKInhibDiv, 0.0);
			inhibitor[i].setInitialConcentration(0.0); // This line is necessary.
			addInhibition(e, inhibitor[i]);
		}
		
		// make loop
		for (int i = 0; i < n; i++) {
			int j = (i + 1) % n;
			String e = getEdgeFactory().createEdge(signal[i], inhibitor[j]);
			addActivation(e, signal[i], inhibitor[j]);
		}
	}

}
