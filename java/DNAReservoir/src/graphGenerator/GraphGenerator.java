package graphGenerator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import model.Constants;
import model.OligoGraph;
import model.OligoSystem;
import model.SlowdownConstants;
import model.chemicals.SequenceVertex;
import model.input.AbstractInput;
import model.input.ExternalInput;
import model.input.PulseInput;
import utils.EdgeFactory;
import utils.SequenceVertexComparator;
import utils.VertexFactory;

public class GraphGenerator {
	public OligoGraph<SequenceVertex, String> graph;
	
	public GraphGenerator() {
		initGraph();
	}
	
	private void initGraph(){
		graph = new OligoGraph<SequenceVertex,String>();
	    graph.initFactories(new VertexFactory<SequenceVertex>(graph){
	    	
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
	    	new EdgeFactory<SequenceVertex,String>(graph) {
    			public String createEdge(SequenceVertex v1, SequenceVertex v2) {
    				return v1.ID+"->"+v2.ID;
    			}
    			public String inhibitorName(String s) {
    				return "Inhib"+s;
    			}
	    });
	    	    
	    graph.saturableExo = true;
	    graph.saturableNick = true;
	    graph.saturablePoly = true;
	}
	
	public OligoGraph<SequenceVertex, String> generateGraph(){
		return graph;
	}
	
	public void executeSimulation(String waveFileName) {
		OligoSystem<String> model = new OligoSystem<String>(graph);
		double[][] result = model.calculateTimeSeries(null);		
		try {
			PrintWriter writer = new PrintWriter(waveFileName);
			for (int t = 0; t< result[0].length; t++) {
				for (int i = 0; i < graph.getVertexCount(); i++) {
					writer.print(result[i][t] + "\t");
				}
				writer.println();
			}
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void exportGraph(String graphFileName){
		String res = "SEQ\n";		
		ArrayList<SequenceVertex> list = new ArrayList<SequenceVertex>(graph.getVertices());
		Collections.sort(list, new SequenceVertexComparator());
		for (SequenceVertex v: list){
			double x = 600 * Math.random();
			double y = 275 * Math.random();
			res += v.ID+"\t"+v.initialConcentration+"\t"+x+"\t"+y+"\t"+graph.getK(v)+"\n";
		}
		res+= "TEM\n";
		for (String e: graph.getEdges()){
			res += e.toString() +"\t"+graph.getSource(e).ID+"\t"+graph.getDest(e).ID+"\t"+graph.getTemplateConcentration(e)+"\t"+graph.getStacking(e)+"\t"+graph.getDangleL(e)+"\t"+graph.getDangleR(e)+"\t"+graph.getType(e)+"\n";
		}
		res+= "INHIB\n";
		for (SequenceVertex v: graph.inhibitors.keySet()){
			res += v.ID+"\t"+graph.getInhibitedEdge(v).toString() +"\t"+graph.getSource(graph.getInhibitedEdge(v)).ID+"\t"+graph.getDest(graph.getInhibitedEdge(v)).ID+"\n";
		}
		res+= "INPUTS\n";
		for (SequenceVertex v: graph.getVertices()){
			for(AbstractInput inp: v.inputs){
				if(inp.getClass() == PulseInput.class){
					PulseInput pu = (PulseInput) inp;
					res += v.ID+"\t"+"pulse"+"\t"+(int)pu.pulseTime+"\t"+pu.ampli+"\t"+(pu.periodic?(int)pu.period:"")+"\n";
				} else if(inp.getClass() == ExternalInput.class){
					ExternalInput ext = (ExternalInput) inp;
					res += v.ID+"\t"+"file"+"\t"+ext.file+"\n";
				}
			}
		}
		res+= "PARAMS\n";
		res+= "absprec\t"+Constants.absprec;
		res+= "\nrelprec\t"+Constants.relprec;
		res+= "\ninhfact\t"+Constants.alpha;
		res+= "\ninhdang\t"+SlowdownConstants.inhibDangleSlowdown;
		res+= "\ndiplrat\t"+Constants.displ;
		res+= "\nexokmib\t"+Constants.exoKmInhib;
		res+= "\nexokmsi\t"+Constants.exoKmSimple;
		res+= "\nexokmtm\t"+Constants.exoKmTemplate;
		res+= "\nexovm\t"+Constants.exoVm;
		res+= "\nkduplex\t"+Constants.Kduplex;
		res+= "\nnickkm\t"+Constants.nickKm;
		res+= "\nnickvm\t"+Constants.nickVm;
		res+= "\nmaxtime\t"+Constants.numberOfPoints;
		res+= "\npolkm\t"+Constants.polKm;
		res+= "\npolkmbo\t"+Constants.polKmBoth;
		res+= "\npolvm\t"+Constants.polVm;
		res+= "\nselfsta\t"+Constants.ratioSelfStart;
		res+= "\ntoeleft\t"+Constants.ratioToeholdLeft;
		res+= "\ntoeright\t"+Constants.ratioToeholdRight;
		res+= "\ndangle\t"+graph.dangle;
		
		if(!graph.getNotPlottedSeqs().isEmpty()){
			res+="\nnotplot\t";
			for(SequenceVertex n : graph.getNotPlottedSeqs()){
				res+=""+n.ID+" ";
			}
		}
		
		try {
			PrintWriter writer = new PrintWriter(graphFileName);
			writer.print(res);
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}