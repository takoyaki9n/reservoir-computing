package graph;

import java.util.ArrayList;
import java.util.Random;

import javax.json.JsonObject;

import model.Constants;
import model.chemicals.SequenceVertex;

public class RandomGraph extends MyOligoGraph {
	
	private static final long serialVersionUID = 1225720254035584107L;
	
	private int nS, nI, mS, mI;
	
	static final private double maxInitSignalCons = 0.5;

	public RandomGraph(int nS, int nI, int mS, int mI) {
		this.nS = nS; this.nI = nI;
		this.mS = mS; this.mI = mI;
	}
	
	public RandomGraph(JsonObject config) {
		this(config.getInt("nS"), config.getInt("nI"), config.getInt("mS"), config.getInt("mI"));
		buildGraph();
	}
	
	@Override
	protected void buildGraph() {
		this.makeSpanningTree();
		this.addActivation();
		this.addInhibition();
	}
	
	public void makeSpanningTree() {
		ArrayList<SequenceVertex> U = new ArrayList<SequenceVertex>();
		ArrayList<SequenceVertex> V = new ArrayList<SequenceVertex>();
		for (int i = 0; i < nS; i++) {
			SequenceVertex sig = getVertexFactory().create();
			double conc = Math.random() * maxInitSignalCons;
			if (i == 0) {
				U.add(sig);
			} else {
				V.add(sig);
			}
			addSpecies(sig, 1.0 / Constants.PadiracKSimpleDiv, conc);
			sig.setInitialConcentration(conc);
		}
		
		while (V.size() > 0) {
			int iu = new Random().nextInt(U.size());
			int iv = new Random().nextInt(V.size());
			SequenceVertex u = U.get(iu);
			SequenceVertex v = V.get(iv);
			U.add(v); V.remove(iv);
			if (new Random().nextBoolean()) {
				SequenceVertex tmp = u; u = v; v = tmp; // swap
			}
			String e = getEdgeFactory().createEdge(u, v);
			addActivation(e, u, v, 10.0);			
		}
	}
	
	public void addActivation() {
		ArrayList<SequenceVertex> vertices = new ArrayList<SequenceVertex>(getVertices());
		int mSRest = mS - nS + 1;
		while (mSRest > 0) {
			SequenceVertex u = vertices.get(new Random().nextInt(nS));
			SequenceVertex v = vertices.get(new Random().nextInt(nS));
			String e = getEdgeFactory().createEdge(u, v);
			if (!containsEdge(e)) {
				addActivation(e, u, v, 10.0);
				mSRest--;
			}
		}
	}
	
	public void addInhibition() {
		ArrayList<SequenceVertex> signals = new ArrayList<>(getVertices());
		ArrayList<SequenceVertex> inhibitors = new ArrayList<>();
		ArrayList<String> edges = new ArrayList<>(getEdges());
		for (int i = 0; i < nI; i++) {
			SequenceVertex ihb = getVertexFactory().create();
			ihb.setInhib(true);
			addSpecies(ihb, 1.0 / Constants.PadiracKInhibDiv, 0.0);
			ihb.setInitialConcentration(0.0);
			
			String e = edges.get(new Random().nextInt(mS));
			addInhibition(e, ihb);
			
			SequenceVertex sig = signals.get(new Random().nextInt(nS));
			String f = getEdgeFactory().createEdge(sig, ihb);
			addActivation(f, sig, ihb, 10.0);
			inhibitors.add(ihb);
		}
		
		int mIRest = mI - nI;
		while (mIRest > 0) {
			SequenceVertex sig = signals.get(new Random().nextInt(nS));
			SequenceVertex ihb = inhibitors.get(new Random().nextInt(nI));
			String e = getEdgeFactory().createEdge(sig, ihb);
			if (!containsEdge(e)) {
				addActivation(e, sig, ihb, 10.0);
				mIRest--;
			}
		}
	}
}
