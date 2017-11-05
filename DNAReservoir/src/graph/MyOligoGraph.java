package graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import input.Input;
import model.Constants;
import model.OligoGraph;
import model.SlowdownConstants;
import model.chemicals.SequenceVertex;
import model.input.AbstractInput;
import model.input.ExternalInput;
import model.input.PulseInput;
import optimizer.Parameter;
import utils.EdgeFactory;
import utils.SequenceVertexComparator;
import utils.VertexFactory;

public class MyOligoGraph extends OligoGraph<SequenceVertex, String> {
	public String id;
	
	private static final long serialVersionUID = -3549974446236175546L;
	
	public MyOligoGraph(JsonObject config) {
		this(config.getString("id"));
	}


	public MyOligoGraph(String id) {
		this.id = id;

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
	
	public void attachInputs(JsonObject graphConfig, HashMap<String, Input> inputs) {
		JsonArray inputConfigArray = graphConfig.getJsonArray("inputs");
		for (int i = 0; i < inputConfigArray.size(); i++) {
			JsonObject inputConfig = inputConfigArray.getJsonObject(i);
			
			int vertexId = inputConfig.getInt("vertex_id");
			String inputId = inputConfig.getString("input_id");			
			
			SequenceVertex vertex = getVertexByID(vertexId);
			Input input = inputs.get(inputId);
			
			vertex.inputs.add(new ExternalInput(input.getDataAsDouble(), input.file));
		}
	}
	
	public SequenceVertex getVertexByID(Integer ID) {
		for (SequenceVertex v : getVertices()) {
			if (v.ID.equals(ID)) return v;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static MyOligoGraph open(String path){
		String file = new File(path).getName();
		String graphId = file.substring(0, file.lastIndexOf("."));
		MyOligoGraph graph = new MyOligoGraph(graphId);
		SequenceVertex newv;
		String newEdge;
		try {
			FileReader in = new FileReader(path);
			BufferedReader reader = new BufferedReader(in);
			String line = null;
			line = reader.readLine();
			String[] split;
			while(line.startsWith("#")){
				line = reader.readLine();
			}
			if (line.startsWith("SEQ")){

				//TODO: I should parse the file first... Also, available seqs?=> Do not guaranty the name?
				graph.totalReset();
				
				HashMap<Integer,SequenceVertex> idvertices = new HashMap<Integer,SequenceVertex>();
				boolean changeKInhib = false;
				while (!(line = reader.readLine()).startsWith("TEM")) { 
					if(line.startsWith("#")){
						continue;
					}
					split= line.split("\t");
					newv = new SequenceVertex(Integer.valueOf(split[0]),Double.valueOf(split[1]));
					idvertices.put(Integer.valueOf(split[0]), newv);
					graph.addVertex(newv);
					if(split.length >= 5){
						graph.K.put(newv, Double.valueOf(split[4]));
					} else {
						graph.K.put(newv, Constants.Kduplex/Constants.PadiracKSimpleDiv);
						changeKInhib = true;
					}
					if(newv != null){
						Parameter<SequenceVertex, String> tempSeqK = null;
						//seqK
						Enumeration<DefaultMutableTreeNode> it = ((MutableTreeNode) graph.optimizable.getChild(graph.optimizable.getRoot(), 0)).children();
						while(it.hasMoreElements()){
							DefaultMutableTreeNode next = it.nextElement();
							if(((Parameter<SequenceVertex, String>) next.getUserObject()).target.equals(newv)){
								tempSeqK = (Parameter<SequenceVertex, String>) next.getUserObject();
								break;
							}
						}
						if(tempSeqK != null){
							tempSeqK.currentValue = graph.getK(newv);
							tempSeqK.minValue = graph.isInhibitor(newv)? Constants.inhibKmin : Constants.simpleKmin;
							tempSeqK.maxValue = graph.isInhibitor(newv)? Constants.inhibKmax : Constants.simpleKmax;
						}
						//seqC
						it = ((MutableTreeNode) graph.optimizable.getChild(graph.optimizable.getRoot(), 1)).children();
						while(it.hasMoreElements()){
							DefaultMutableTreeNode next = it.nextElement();
							if(((Parameter<SequenceVertex, String>) next.getUserObject()).target.equals(newv)){
								tempSeqK = (Parameter<SequenceVertex, String>) next.getUserObject();
								break;
							}
						}
						if(tempSeqK != null){
							tempSeqK.currentValue = ((SequenceVertex) newv).initialConcentration;
							tempSeqK.minValue = 0;
							tempSeqK.maxValue = 100;
						}
					}
				}
				while (!(line = reader.readLine()).startsWith("INHIB")) {
					if(line.startsWith("#")){
						continue;
					}
					split= line.split("\t");
					newEdge = graph.getEdgeFactory().createEdge(idvertices.get(Integer.valueOf(split[1])), idvertices.get(Integer.valueOf(split[2])));
					graph.addActivation(newEdge, idvertices.get(Integer.valueOf(split[1])), idvertices.get(Integer.valueOf(split[2])), Double.valueOf(split[3]));
					graph.setStacking(newEdge, split.length>=5?Double.valueOf(split[4]):model.Constants.baseStack);
					graph.setDangleL(newEdge, split.length>=6?Double.valueOf(split[5]):model.Constants.baseDangleL);
					graph.setDangleR(newEdge, split.length>=7?Double.valueOf(split[6]):model.Constants.baseDangleR);
					graph.setType(newEdge, split.length>=8?split[7]:"Default");
				}
				while ((line = reader.readLine()) != null && !line.startsWith("INPUTS") && !line.startsWith("PARAMS")){
					if(line.startsWith("#")){
						continue;
					}
					split= line.split("\t");
					newEdge = graph.findEdge(idvertices.get(Integer.valueOf(split[2])), idvertices.get(Integer.valueOf(split[3])));
					idvertices.get(Integer.valueOf(split[0])).setInhib(true);
					graph.addInhibition(newEdge, idvertices.get(Integer.valueOf(split[0])));
					if(changeKInhib){
						graph.K.put(idvertices.get(Integer.valueOf(split[0])), Constants.Kduplex/Constants.PadiracKInhibDiv);
					}
				}
				//INPUT is optional, so we have to be careful
				while(!line.startsWith("PARAMS") && (line = reader.readLine()) != null && !line.startsWith("PARAMS")){
					if(line.startsWith("#")){
						continue;
					}
					split= line.split("\t");
					SequenceVertex v = idvertices.get(Integer.valueOf(split[0]));
					if(split[1].equals("pulse")){
						PulseInput newinp = new PulseInput(Integer.valueOf(split[2]),Double.valueOf(split[3]));
						if(split.length>4){
							newinp.periodic = true;
							newinp.period = Integer.valueOf(split[4]);
						}
						v.inputs.add(newinp);
					} else if(split[1].equals("file")){
						File open = new File(split[2]);
						try {
							FileReader inin = new FileReader(open);
							BufferedReader readerin = new BufferedReader(inin);
							String linein = null;
							String[] splitin;
							ArrayList<Double> values = new ArrayList<Double>();
							while ((linein = readerin.readLine()) != null){
								splitin= linein.split("\t");
								for(int i=0; i<splitin.length;i++){
									values.add(Double.parseDouble(splitin[i]));
								}
							}
							Double[] inp = new Double[values.size()];
							inp = values.toArray(inp);
							v.inputs.add(new ExternalInput(inp,open));
							readerin.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				//PARAMS
				while ((line = reader.readLine()) != null){
					if(line.startsWith("#")){
						continue;
					}
					split = line.split("\t");
					//Could use a switch if we were using Java 7. Better to stay compatible with 5
					
					if(split[0].equals("absprec")){
						Constants.absprec = Double.parseDouble(split[1]);
					} else if(split[0].equals("dangle")){
						graph.dangle = Boolean.parseBoolean(split[1]);
					} else if(split[0].equals("relprec")){
						Constants.relprec = Double.parseDouble(split[1]);
					} else if(split[0].equals("inhfact")){
						Constants.alpha = Double.parseDouble(split[1]);
					} else if(split[0].equals("inhdang")){
						SlowdownConstants.inhibDangleSlowdown = Double.parseDouble(split[1]);
					} else if(split[0].equals("diplrat")){
						Constants.displ = Double.parseDouble(split[1]);
					} else if(split[0].equals("exokmib")){
						Constants.exoKmInhib = Double.parseDouble(split[1]);
					} else if(split[0].equals("exokmsi")){
						Constants.exoKmSimple = Double.parseDouble(split[1]);
					} else if(split[0].equals("exokmtm")){
						Constants.exoKmTemplate = Double.parseDouble(split[1]);
					} else if(split[0].equals("exovm")){
						Constants.exoVm = Double.parseDouble(split[1]);
					} else if(split[0].equals("kduplex")){
						Constants.Kduplex = Double.parseDouble(split[1]);
					} else if(split[0].equals("nickkm")){
						Constants.nickKm = Double.parseDouble(split[1]);
					} else if(split[0].equals("nickvm")){
						Constants.nickVm = Double.parseDouble(split[1]);
					} else if(split[0].equals("maxtime")){
						Constants.numberOfPoints = Integer.parseInt(split[1]);
					} else if(split[0].equals("polkm")){
						Constants.polKm = Double.parseDouble(split[1]);
					} else if(split[0].equals("polkmbo")){
						Constants.polKmBoth = Double.parseDouble(split[1]);
					} else if(split[0].equals("polvm")){
						Constants.polVm = Double.parseDouble(split[1]);
					} else if(split[0].equals("selfsta")){
						Constants.ratioSelfStart = Double.parseDouble(split[1]);
					} else if(split[0].equals("toeleft")){
						Constants.ratioToeholdLeft = Double.parseDouble(split[1]);
					} else if(split[0].equals("toeright")){
						Constants.ratioToeholdRight = Double.parseDouble(split[1]);
					} else if(split[0].equals("notplot")){
						String[] splot = split[1].split(" ");
						for(int i=0; i<splot.length;i++){
							SequenceVertex remseq = graph.getEquivalentVertex(new SequenceVertex(Integer.parseInt(splot[i])));
							graph.removePlottedSeq(remseq);
						}
					}
				}
			}
			//We have to make sure everything was updated first
			Constants.alphaBase = Constants.alpha/SlowdownConstants.inhibDangleSlowdown;
			graph.saved = true; // we just loaded it...
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return graph;
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
		String type = graphConfig.getString("type");
		
		if (type.equals("oscillator")) {
			return new Oscillator(graphConfig);
		} else if (type.equals("random")) {
			return new RandomGraph(graphConfig);
		}
		
		return null;
	}
}
