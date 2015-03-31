import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

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
	    String[] feature_name = {"word","pos"};
	    
	    float correct = 0;
	    float total = 0;
	    String format = "%-25s%-5s%-5s%n";
	    while ((line = buf.readLine()) != null)
	    {
	    	
	    	String[] parts = line.trim().split(" ");
	    	if (parts.length > 1) {
	    		
	    		total++;
	    		
	    		word = parts[0];
	    		pos = parts[1];
	    		tag = parts[2];
	    		
	    		String[] feature = new String[2];
	    		for (int i = 0; i < parts.length-1; i++)
	    		{	
	    			feature[i] = feature_name[i]+"="+parts[i];
				}
	      		
	    		//System.out.println(tag);
	    		String predicted  = m.getBestOutcome(m.eval(feature));
	    		System.out.printf(format,word,tag,predicted);
	    		
	    		if(tag.equals(predicted))
	    		{
	    			correct++;
	    		}
			}
	    	
	    }
	    
	    System.out.println("Correct: " + correct);
	    System.out.println("Total: " + total);
	    System.out.println("Accuracy: " + (correct/total)*100 +" %");
	    
	    }
	    catch (Exception e)
	    {
	    	System.out.println("Exception" + e.toString());
	    }
	    
	}
}