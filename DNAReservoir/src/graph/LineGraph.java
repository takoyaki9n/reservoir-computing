package graph;

import javax.json.JsonObject;

import model.Constants;
import model.chemicals.SequenceVertex;

public class LineGraph extends MyOligoGraph {

	private static final long serialVersionUID = 1L;
	
	private int n;

	public LineGraph(JsonObject config) {
		super(config);
		
		this.n = config.getInt("n");
		
		buildGraph();
	}
	
	@Override
	protected void buildGraph() {
		SequenceVertex[] signal = new SequenceVertex[n];
		for (int i = 0; i < n; i++) {
			// add vertex
			signal[i] = getVertexFactory().create();
			double initCons = 1.0;
			addSpecies(signal[i], 1.0 / Constants.PadiracKSimpleDiv, initCons);
			signal[i].setInitialConcentration(initCons); // This line is necessary.
		}
		
		// make loop
		for (int i = 0; i < n - 1; i++) {
			String e = getEdgeFactory().createEdge(signal[i], signal[i + 1]);
			addActivation(e, signal[i], signal[i + 1]);
		}
	}
}
