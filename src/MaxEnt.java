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
	    GISModel model = GIS.trainModel(es, 100, 4);
	    File outputFile = new File(modelFileName);
	    GISModelWriter writer = new SuffixSensitiveGISModelWriter(model, outputFile);
	    writer.persist();
	} catch (Exception e) {
	    System.out.print("Unable to create model due to exception: ");
	    System.out.println(e);
	}
	System.out.println("Done training...");
	System.out.println();
	System.out.println("Starting testing...");

	try {

	    GISModel m = (GISModel) new SuffixSensitiveGISModelReader(new File(modelFileName)).getModel();
	    BufferedReader buf = new BufferedReader(new FileReader(testFileName));

	    String B = "B";
	    String I = "I";
	    String O = "O";

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

	    String line;
	    String word;
	    String pos;
	    String tag;
	    List<String> words = new ArrayList<String>();
	    List<String> poss = new ArrayList<String>();
	    List<String> tags = new ArrayList<String>();

	    System.out.println("Reading test file...");
	    while ((line = buf.readLine()) != null) {

		String[] parts = line.trim().split(" ");
		if (parts.length > 1) {

		    word = parts[0];
		    pos = parts[1];
		    tag = parts[parts.length - 1];

		    words.add(word);
		    poss.add(pos);
		    tags.add(tag);
		}

	    }
	    buf.close();

	    String current_word;
	    String current_pos;
	    String default_word = "";
	    String default_pos = "";

	    String predicted;
	    
	    int window_size = 2;
	    int idx;

	    System.out.println("Creating features and predicting...");
	    for (int i = 0; i < words.size(); i++) {

		List<String> feature = new ArrayList<String>();
		current_word = words.get(i);
		current_pos = poss.get(i);
		tag = tags.get(i);

		idx = window_size;
		for (int j = i - window_size; j < i; j++) {

		    if (j < 0) {
			feature.add("prev" + idx + "word" + "=" + default_word);
			feature.add("prev" + idx + "pos" + "=" + default_pos);

		    } else {
			feature.add("prev" + idx + "word" + "=" + words.get(j));
			feature.add("prev" + idx + "pos" + "=" + poss.get(j));
		    }
		    idx--;
		}

		feature.add("currentword=" + current_word);
		feature.add("currentpos=" + current_pos);

		predicted = m.getBestOutcome(m.eval(feature.toArray(new String[feature.size()])));

		// Tag accuracies calculation
		total.put(tag, total.get(tag) + 1);
		if (tag.equals(predicted)) {

		    correct.put(predicted, correct.get(predicted) + 1);
		}

		// Precision Recall Fscore calculation
		correct_tags.add(tag);
		predicted_tags.add(predicted);

	    }

	    System.out.println();
	    System.out.println("Results-");

	    /*
	     * Tag Accuracies
	     */

	    System.out.println();
	    System.out.println("Per tag accuracies:");

	    String format = "%-5s%-10s%-10s%-10s%n";
	    System.out.printf(format, "Tag", "Correct", "Total", "Accuracy");

	    format = "%-5s%-10.2f%-10.2f%-4.2f%%%n";
	    System.out.printf(format, B, correct.get(B), total.get(B), (correct.get(B) / total.get(B)) * 100);
	    System.out.printf(format, I, correct.get(I), total.get(I), (correct.get(I) / total.get(I)) * 100);
	    System.out.printf(format, O, correct.get(O), total.get(O), (correct.get(O) / total.get(O)) * 100);
	    System.out.println();

	    System.out
		    .printf("Overall Tag accuracy: %.2f%%%n",(((correct.get(B) + correct.get(I) + correct.get(O)) / (total.get(B)
				    + total.get(I) + total.get(O))) * 100));
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
	    System.out.printf(format, "#of matching tags:", num_matching_tag);
	    System.out.printf(format, "#of tags in response:", num_tag_response);
	    System.out.printf(format, "#of tags in key:", num_tag_key);
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