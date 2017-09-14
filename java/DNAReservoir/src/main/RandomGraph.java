package main;

import java.util.ArrayList;
import java.util.Random;

import edu.uci.ics.jung.graph.util.Pair;
import model.Constants;
import model.OligoGraph;
import model.chemicals.SequenceVertex;
import utils.EdgeFactory;
import utils.VertexFactory;

public class RandomGraph {
	static final double maxInitSignalCons = 0.1;
	
	private int nS, nI;
	private int mS, mI;
	public OligoGraph<SequenceVertex, String> graph;
	private SequenceVertex s1;
	
	public RandomGraph(int nS, int nI, int mS, int mI) {
		this.nS = nS; this.nI = nI;
		this.mS = mS; this.mI = mI;
		this.generateGraph();
	}
		
	public void initGraph(){
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
	    graph.saturableExo = true;
	    graph.saturableNick = true;
	    graph.saturablePoly = true;
	}

	public void generateGraph() {
		this.initGraph();
		this.makeSpanningTree();
		this.addActivation();
		this.addInhibition();
		this.makeOutEdgeFromInput();
	}
	
	public void makeSpanningTree() {
		ArrayList<SequenceVertex> U = new ArrayList<SequenceVertex>();
		ArrayList<SequenceVertex> V = new ArrayList<SequenceVertex>();
		for (int i = 0; i < nS; i++) {
			SequenceVertex sig = graph.getVertexFactory().create();
			double conc = Math.random() * maxInitSignalCons;
			if (i == 0) {
				this.s1 = sig;
				U.add(sig);
			} else {
				V.add(sig);
			}
			graph.addSpecies(sig, 1.0 / Constants.PadiracKSimpleDiv, conc);
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
			String e = graph.getEdgeFactory().createEdge(u, v);
			graph.addActivation(e, u, v, 10.0);			
		}
	}
	
	public void addActivation() {
		ArrayList<SequenceVertex> vertices = new ArrayList<SequenceVertex>(graph.getVertices());
		int mSRest = mS - nS + 1;
		while (mSRest > 0) {
			SequenceVertex u = vertices.get(new Random().nextInt(nS));
			SequenceVertex v = vertices.get(new Random().nextInt(nS));
			String e = graph.getEdgeFactory().createEdge(u, v);
			if (!graph.containsEdge(e)) {
				graph.addActivation(e, u, v, 10.0);
				mSRest--;
			}
		}
	}
	
	public void addInhibition() {
		ArrayList<SequenceVertex> signals = new ArrayList<>(graph.getVertices());
		ArrayList<SequenceVertex> inhibitors = new ArrayList<>();
		ArrayList<String> edges = new ArrayList<>(graph.getEdges());
		for (int i = 0; i < nI; i++) {
			SequenceVertex ihb = graph.getVertexFactory().create();
			ihb.setInhib(true);
			graph.addSpecies(ihb, 1.0 / Constants.PadiracKInhibDiv, 0.0);
			ihb.setInitialConcentration(0.0);
			
			String e = edges.get(new Random().nextInt(mS));
			graph.addInhibition(e, ihb);
			
			SequenceVertex sig = signals.get(new Random().nextInt(nS));
			String f = graph.getEdgeFactory().createEdge(sig, ihb);
			graph.addActivation(f, sig, ihb, 10.0);
			inhibitors.add(ihb);
		}
		
		int mIRest = mI - nI;
		while (mIRest > 0) {
			SequenceVertex sig = signals.get(new Random().nextInt(nS));
			SequenceVertex ihb = inhibitors.get(new Random().nextInt(nI));
			String e = graph.getEdgeFactory().createEdge(sig, ihb);
			if (!graph.containsEdge(e)) {
				graph.addActivation(e, sig, ihb, 10.0);
				mIRest--;
			}
		}
	}
	
	public void makeOutEdgeFromInput() {
		if (graph.getOutEdges(s1).size() == 0) {
			ArrayList<String> inEdges = new ArrayList<>(graph.getInEdges(s1));
			String e = inEdges.get(new Random().nextInt(inEdges.size()));
			Pair<SequenceVertex> p = graph.getEndpoints(e);
			double conc = graph.getTemplateConcentration(e);
			graph.removeEdge(e);
			
			String f = graph.getEdgeFactory().createEdge(p.getSecond(), p.getFirst());
			graph.addActivation(f, p.getSecond(), p.getFirst(), conc);
		}
	}
}
