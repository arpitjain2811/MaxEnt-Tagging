import opennlp.maxent.*;
import opennlp.maxent.io.*;

public class train {

	
	
	public static void main(String[] args)
	{
		String modelFileName = "features.txt";
	    try {
	        FileReader datafr = new FileReader(new File(dataFileName));
	        EventStream es = new BasicEventStream(new PlainTextByLineDataStream(datafr));
	        GISModel model = GIS.trainModel(es, 100, 4);
	        File outputFile = new File(modelFileName);
	        GISModelWriter writer = new SuffixSensitiveGISModelWriter(model, outputFile);
	        writer.persist();
	    } catch (Exception e) {
	        System.out.print("Unable to create model due to exception: ");
	        System.out.println(e);
	    }
	}
}