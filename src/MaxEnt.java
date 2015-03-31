import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import opennlp.maxent.*;
import opennlp.maxent.io.*;
import opennlp.model.EventStream;

public class MaxEnt {
	public static void main(String[] args)
	{
		String modelFileName = "model";
		String dataFileName = "features/train_features.txt";
		String testFileName = "data/test.txt";
	    try {
			FileReader datafr = new FileReader(new File(dataFileName));
	        EventStream es = new BasicEventStream(new PlainTextByLineDataStream(datafr));
	        GISModel model = GIS.trainModel(es, 100, 4);
	        File outputFile = new File(modelFileName);
	        GISModelWriter writer = new SuffixSensitiveGISModelWriter(model, outputFile);
	        writer.persist();
	    }
	    catch (Exception e)
	    {
	        System.out.print("Unable to create model due to exception: ");
	        System.out.println(e);
	    }
	    try{
	    	
	    GISModel m = (GISModel) new SuffixSensitiveGISModelReader(new File(modelFileName)).getModel();
	    BufferedReader buf = new BufferedReader(new FileReader(testFileName));
	    
	    String line;
	    String word;
	    String pos;
	    String tag;

	    String B = "B";
	    String I = "I";
	    String O = "O";
	    
	    String[] feature_name = {"word","pos"};
	    
	    HashMap<String, Float> correct = new HashMap<String,Float>();
	    HashMap<String, Float> total = new HashMap<String,Float>();

	    correct.put(B, (float) 0);
	    correct.put(I, (float) 0);
	    correct.put(O, (float) 0);
	    
	    
	    total.put(B, (float) 0);
	    total.put(I, (float) 0);
	    total.put(O, (float) 0);
	    
	    
	    String format = "%-25s%-5s%-5s%n";
	    while ((line = buf.readLine()) != null)
	    {
	    	
	    	String[] parts = line.trim().split(" ");
	    	if (parts.length > 1) {
	    		
	    		word = parts[0];
	    		pos = parts[1];
	    		tag = parts[parts.length-1];
	    		String[] feature = new String[2];
	    		for (int i = 0; i < parts.length-1; i++)
	    		{	
	    			feature[i] = feature_name[i]+"="+parts[i];
				}
	    		
	    		String predicted  = m.getBestOutcome(m.eval(feature));
	    		
	    		total.put(tag, total.get(tag)+1);
	    		
	    		if (tag.equals(predicted)) {
					
	    			correct.put(predicted, correct.get(predicted)+1);
				}
	    		
	    		//System.out.printf(format,word,tag,predicted);
			}
	    	
	    }
	    
	    format = "%-5s%-10s%-10s%-10s%n";
	    System.out.printf(format, "Tag","Correct","Total","Accuracy");
	    
	    format = "%-5s%-10.2f%-10.2f%-4.2f%%%n";
	    System.out.printf(format, B,correct.get(B),total.get(B),(correct.get(B)/total.get(B))*100);
	    System.out.printf(format, I,correct.get(I),total.get(I),(correct.get(I)/total.get(I))*100);
	    System.out.printf(format, O,correct.get(O),total.get(O),(correct.get(O)/total.get(O))*100);

	    
	    }
	    catch (Exception e)
	    {
	    	System.out.println("Exception" + e.toString());
	    }
	    
	}
}