train_file = open("../data/training.txt","r")
train_features_file = open("train_features.txt","w")
for line in train_file:
    p = line.strip().split()
    if len(p) > 1:
    	word,pos,tag = line.split(" ")
    	train_features_file.write("word=%s pos=%s %s"%(word,pos,tag))
train_features_file.close()        