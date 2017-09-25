package graph;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.json.JsonArray;
import javax.json.JsonObject;

import input.Input;
import model.Constants;
import model.OligoGraph;
import model.SlowdownConstants;
import model.chemicals.SequenceVertex;
import model.input.AbstractInput;
import model.input.ExternalInput;
import model.input.PulseInput;
import utils.EdgeFactory;
import utils.SequenceVertexComparator;
import utils.VertexFactory;

public class MyOligoGraph extends OligoGraph<SequenceVertex, String> {

	private static final long serialVersionUID = -3549974446236175546L;

	public MyOligoGraph() {
	    this.initFactories(new VertexFactory<SequenceVertex>(this){
	    	
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
	    	new EdgeFactory<SequenceVertex,String>(this) {
    			public String createEdge(SequenceVertex v1, SequenceVertex v2) {
    				return v1.ID+"->"+v2.ID;
    			}
    			public String inhibitorName(String s) {
    				return "Inhib"+s;
    			}
	    });
	}
	
	protected void buildGraph() {}
	
	protected void attachInputs(JsonObject graphConfig, HashMap<String, Input> inputs) {
		JsonArray inputConfigArray = graphConfig.getJsonArray("inputs");
		for (int i = 0; i < inputConfigArray.size(); i++) {
			JsonObject inputConfig = inputConfigArray.getJsonObject(i);
			
			int vertexId = inputConfig.getInt("vertex_id");
			String inputId = inputConfig.getString("input_id");			
			
			SequenceVertex vertex = getVertexByID(vertexId);
			Input input = inputs.get(inputId);
			
			vertex.inputs.add(new ExternalInput(input.getDataAsArray(), null)); //MEMO: bad hack
		}
	}
	
	public SequenceVertex getVertexByID(Integer ID) {
		for (SequenceVertex v : getVertices()) {
			if (v.ID.equals(ID)) return v;
		}
		return null;
	}
	
	public void export(String fileName){
		String res = "SEQ\n";		
		ArrayList<SequenceVertex> list = new ArrayList<SequenceVertex>(getVertices());
		Collections.sort(list, new SequenceVertexComparator());
		for (SequenceVertex v: list){
			double x = 600 * Math.random(); //TODO: config window size
			double y = 275 * Math.random(); //TODO: config window size
			res += v.ID+"\t"+v.initialConcentration+"\t"+x+"\t"+y+"\t"+getK(v)+"\n";
		}
		res+= "TEM\n";
		for (String e: getEdges()){
			res += e.toString() +"\t"+getSource(e).ID+"\t"+getDest(e).ID+"\t"+getTemplateConcentration(e)+"\t"+getStacking(e)+"\t"+getDangleL(e)+"\t"+getDangleR(e)+"\t"+getType(e)+"\n";
		}
		res+= "INHIB\n";
		for (SequenceVertex v: inhibitors.keySet()){
			res += v.ID+"\t"+getInhibitedEdge(v).toString() +"\t"+getSource(getInhibitedEdge(v)).ID+"\t"+getDest(getInhibitedEdge(v)).ID+"\n";
		}
		res+= "INPUTS\n";
		for (SequenceVertex v: getVertices()){
			for(AbstractInput inp: v.inputs){
				if(inp.getClass() == PulseInput.class){
					PulseInput pu = (PulseInput) inp;
					res += v.ID+"\t"+"pulse"+"\t"+(int)pu.pulseTime+"\t"+pu.ampli+"\t"+(pu.periodic?(int)pu.period:"")+"\n";
				} else if(inp.getClass() == ExternalInput.class){
					ExternalInput ext = (ExternalInput) inp;
					res += v.ID+"\t"+"file"+"\t"+ext.file.getAbsolutePath()+"\n";
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
		res+= "\ndangle\t"+dangle;
		
		if(!getNotPlottedSeqs().isEmpty()){
			res+="\nnotplot\t";
			for(SequenceVertex n : getNotPlottedSeqs()){
				res+=""+n.ID+" ";
			}
		}
		
		try {
			PrintWriter writer = new PrintWriter(fileName);
			writer.print(res);
			writer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public static MyOligoGraph generateGraph(JsonObject graphConfig) {
		// TODO: implement
		return new MyOligoGraph();
	}
}
