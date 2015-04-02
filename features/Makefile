train:
	python create_feats_mallet_crf_train.py;
	java -cp "/Users/arpitjain/mallet-2.0-2.7/class:/Users/arpitjain/mallet-2.0-2.7/lib/mallet-deps.jar" cc.mallet.fst.SimpleTagger --train true --model-file ngcrf --threads 12  train_features_crf.txt

test:
	python create_feat_mallet_crf_test.py;
	java -cp "/Users/arpitjain/mallet-2.0-2.7/class:/Users/arpitjain/mallet-2.0-2.7/lib/mallet-deps.jar" cc.mallet.fst.SimpleTagger --model-file ngcrf  test_features_crf.txt > crf_predictions.txt
clean:
	rm -rf train_features_crf.txt;
	rm -rf ngcrf;
	rm -rf test_features_crf.txt;
	rm -rf crf_predictions.txt
