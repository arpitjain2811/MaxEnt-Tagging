import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.maxent.*;
import opennlp.maxent.io.*;
import opennlp.model.EventStream;

public class MaxEnt {
    public static void main(String[] args) {
	String modelFileName = "model";
	String dataFileName = "features/train_features.txt";
	String testFileName = "data/test.txt";
	try {
	    FileReader datafr = new FileReader(new File(dataFileName));
	    EventStream es = new BasicEventStream(new PlainTextByLineDataStream(datafr));
	    GISModel model = GIS.trainModel(es, 100, 2);
	    File outputFile = new File(modelFileName);
	    GISModelWriter writer = new SuffixSensitiveGISModelWriter(model, outputFile);
	    writer.persist();
	} catch (Exception e) {
	    System.out.print("Unable to create model due to exception: ");
	    System.out.println(e);
	}
	try {

	    GISModel m = (GISModel) new SuffixSensitiveGISModelReader(new File(modelFileName)).getModel();
	    BufferedReader buf = new BufferedReader(new FileReader(testFileName));

	    String line;
	    String tag;

	    String B = "B";
	    String I = "I";
	    String O = "O";

	    String[] feature_name = { "word", "pos" };

	    HashMap<String, Float> correct = new HashMap<String, Float>();
	    HashMap<String, Float> total = new HashMap<String, Float>();

	    correct.put(B, (float) 0);
	    correct.put(I, (float) 0);
	    correct.put(O, (float) 0);

	    total.put(B, (float) 0);
	    total.put(I, (float) 0);
	    total.put(O, (float) 0);

	    List<String> correct_tags = new ArrayList<String>();
	    List<String> predicted_tags = new ArrayList<String>();

	    String format = "%-25s%-5s%-5s%n";
	    while ((line = buf.readLine()) != null) {

		String[] parts = line.trim().split(" ");
		if (parts.length > 1) {

		    tag = parts[parts.length - 1];

		    String[] feature = new String[2];
		    for (int i = 0; i < parts.length - 1; i++) {
			feature[i] = feature_name[i] + "=" + parts[i];
		    }

		    String predicted = m.getBestOutcome(m.eval(feature));
		    total.put(tag, total.get(tag) + 1);
		    if (tag.equals(predicted)) {

			correct.put(predicted, correct.get(predicted) + 1);
		    }
		    correct_tags.add(tag);
		    predicted_tags.add(predicted);
		}

	    }
	    buf.close();

	    /*
	     * Tag Accuracies
	     */

	    format = "%-5s%-10s%-10s%-10s%n";
	    System.out.println();
	    System.out.printf(format, "Tag", "Correct", "Total", "Accuracy");

	    format = "%-5s%-10.2f%-10.2f%-4.2f%%%n";
	    System.out.printf(format, B, correct.get(B), total.get(B), (correct.get(B) / total.get(B)) * 100);
	    System.out.printf(format, I, correct.get(I), total.get(I), (correct.get(I) / total.get(I)) * 100);
	    System.out.printf(format, O, correct.get(O), total.get(O), (correct.get(O) / total.get(O)) * 100);
	    System.out.println();

	    /*
	     * Precision = # of matching tags / # of tags in response Recall = #
	     * of matching tags / # of tags in key
	     */

	    HashSet<String> ng_correct = getTags(correct_tags);
	    HashSet<String> ng_predicted = getTags(predicted_tags);

	    float num_tag_key = ng_correct.size();
	    float num_tag_response = ng_predicted.size();
	    float num_matching_tag = 0;

	    HashSet<String> intersection = new HashSet<String>(ng_correct);
	    intersection.retainAll(ng_predicted);

	    num_matching_tag = intersection.size();

	    format = "%-25s%-5.1f%n";
	    System.out.printf(format, "#of matching tags", num_matching_tag);
	    System.out.printf(format, "#of tags in response", num_tag_response);
	    System.out.printf(format, "#of tags in key", num_tag_key);
	    System.out.println();
	    float Precision = (num_matching_tag / num_tag_response);
	    float Recall = (num_matching_tag / num_tag_key);
	    float Fscore = (2 * (Precision * Recall)) / (Precision + Recall);
	    format = "%-14s%-10.6f%n";
	    System.out.printf(format, "Precision:", Precision);
	    System.out.printf(format, "Recall:", Recall);
	    System.out.printf(format, "Fscore:", Fscore);

	} catch (Exception e) {
	    System.out.println("Exception" + e.toString());
	}

    }

    private static HashSet<String> getTags(List<String> tag_seq) {

	HashSet<String> tags = new HashSet<String>();

	int start = 0;
	int last = 0;

	for (int i = 0; i < tag_seq.size(); i++) {
	    if (tag_seq.get(i).equals("B")) {
		if (i == 0) {
		    start = i;
		    last = start;
		    continue;
		}

		tags.add(start + " " + last);
		start = i;
		last = start;
	    }

	    if (tag_seq.get(i).equals("I")) {
		if (i == 0) {
		    start = i;
		    last = i;
		    continue;
		}

		if (tag_seq.get(i - 1).equals("B") || tag_seq.get(i - 1).equals("I")) {
		    last++;
		}
		if (tag_seq.get(i - 1).equals("O")) {
		    tags.add(start + " " + last);
		    start = i;
		    last = start;
		}
	    }

	}

	return tags;
    }
}