train_file = open("../data/training.txt","r")
train_features_file = open("train_features.txt","w")
words = [];
poss = [];
tags = [];
for line in train_file:
    p = line.strip().split()
    if len(p) > 1:
        word,pos,tag = line.split(" ")
        words.append(word)
        poss.append(pos)
        tags.append(tag)
train_file.close()

window_size = 2

default_word = ""
default_pos = ""
default_shape = None

prev_pos_features = []
next_pos_features = []
prev_word_features = []
next_word_features = []
prev_shape_features = []
next_shape_features = []



for i in range(0,len(words)):
    feature = ""
    current_word = words[i]
    current_pos = poss[i]

    idx = window_size
    for j in range(i-window_size,i):
        if j < 0:
            feature = feature + "prev" + str(idx) + "word" + "=" + default_word + " " + "prev" + str(idx) + "pos" + "=" + default_pos+" "
        else:
            feature = feature + "prev" + str(idx) + "word" + "=" + words[j] + " " + "prev" + str(idx) + "pos" + "=" + poss[j] + " "
        idx = idx - 1

    feature = feature + "currentword=" + current_word + " " + "currentpos=" + current_pos + " "
    feature = feature + tags[i]
    train_features_file.write(feature)

train_features_file.close()