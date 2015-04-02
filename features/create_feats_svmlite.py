from collections import defaultdict

train_file = open("../data/training.txt","r")
test_file = open("../data/test.txt","r")
train_features_file = open("train_features_mallet.txt","w")
test_features_file = open("test_features_mallet.txt","w")

words = [];
poss = [];
tags = [];
word_dict = defaultdict(float)
pos_dict = defaultdict(float)
tag_dict = defaultdict(float)

print("Creating training features....")

idx_word = 0
idx_pos = 0
idx_tag = 0

for line in train_file:
    p = line.strip().split(" ")
    if len(p) > 1:
        word,pos,tag = line.rstrip().split(" ")
        words.append(word)
        poss.append(pos)
        tags.append(tag)

        if not (word in word_dict):
            word_dict[word] = idx_word
            idx_word = idx_word + 1

        if not (pos in pos_dict):
            pos_dict[pos] = idx_pos
            idx_pos = idx_pos + 1

        if not (tag in tag_dict):
            tag_dict[tag] = idx_tag
            idx_tag = idx_tag + 1


train_file.close()
back_window_size = 1
forward_window_size = 1

default_word = len(word_dict) + 1.0
default_pos = len(pos_dict) + 1.0
default_tag = len(tag_dict) + 1.0
default_shape = None

prev_pos_features = []
next_pos_features = []
prev_word_features = []
next_word_features = []
prev_shape_features = []
next_shape_features = []



for i in range(0,len(words)):
    idx = 1
    feature = str(tag_dict[tags[i]]) + " "
    current_word = words[i]
    current_pos = poss[i]


    for j in range(i-back_window_size,i):
        if j < 0:
            feature = feature + str(idx) + ":" + str(default_word) + " "
            idx = idx + 1
            feature = feature + str(idx) + ":" + str(default_pos)+" "
            idx = idx + 1
            feature = feature + str(idx) + ":" + str(default_tag)+" "
            idx = idx + 1
        else:
            feature = feature + str(idx) + ":" + str(word_dict[words[j]]) + " "
            idx = idx + 1
            feature = feature + str(idx) + ":" + str(pos_dict[poss[j]])+" "
            idx = idx + 1
            feature = feature + str(idx) + ":" + str(tag_dict[tags[j]])+" "
            idx = idx + 1


    feature = feature + str(idx) + ":" + str(word_dict[current_word]) + " "
    idx = idx + 1
    feature = feature + str(idx) + ":" +  str(pos_dict[current_pos]) + " "
    idx = idx + 1
    feature = feature + str(idx) + ":" + str(word_dict[current_word.lower()]) + " "
    idx = idx + 1
    feature = feature + str(idx) + ":" + str(int(str.isalpha(current_word))) + " "
    idx = idx + 1
    feature = feature + str(idx) + ":" + str(int(str.isdigit(current_word))) + " "
    idx = idx + 1
    feature = feature + str(idx) + ":" + str(int(str.islower(current_word))) + " "
    idx = idx + 1
    feature = feature + str(idx) + ":" + str(int(str.istitle(current_word))) + " "
    idx = idx + 1
    feature = feature + str(idx) + ":" + str(int(str.isupper(current_word))) + " "
    idx = idx + 1

    for j in range(i+1,i+1+forward_window_size):
        if j >= len(words) :
            feature = feature + str(idx) + ":" + str(default_word) + " "
            idx = idx + 1
            feature = feature + str(idx) + ":" + str(default_pos)+" "
            idx = idx + 1
            feature = feature + str(idx) + ":" + str(default_tag)+" "
            idx = idx + 1
        else:
            feature = feature + str(idx) + ":" + str(word_dict[words[j]]) + " "
            idx = idx + 1
            feature = feature + str(idx) + ":" + str(pos_dict[poss[j]])+" "
            idx = idx + 1

    feature = feature  + "\n"
    train_features_file.write(feature)
train_features_file.close()


words = [];
poss = [];
tags = [];

for line in test_file:
    p = line.strip().split(" ")
    if len(p) > 1:
        word,pos,tag = line.rstrip().split(" ")
        words.append(word)
        poss.append(pos)
        tags.append(tag)


test_file.close()
back_window_size = 1
forward_window_size = 1

default_word = len(word_dict) + 1.0
default_pos = len(pos_dict) + 1.0
default_tag = len(tag_dict) + 1.0
default_shape = None


for i in range(0,len(words)):
    idx = 1
    feature = str(tag_dict[tags[i]]) + " "
    current_word = words[i]
    current_pos = poss[i]


    for j in range(i-back_window_size,i):
        if j < 0:
            feature = feature + str(idx) + ":" + str(default_word) + " "
            idx = idx + 1
            feature = feature + str(idx) + ":" + str(default_pos)+" "
            idx = idx + 1
            feature = feature + str(idx) + ":" + str(default_tag)+" "
            idx = idx + 1
        else:
            feature = feature + str(idx) + ":" + str(word_dict[words[j]]) + " "
            idx = idx + 1
            feature = feature + str(idx) + ":" + str(pos_dict[poss[j]])+" "
            idx = idx + 1
            feature = feature + str(idx) + ":" + str(tag_dict[tags[j]])+" "
            idx = idx + 1


    feature = feature + str(idx) + ":" + str(word_dict[current_word]) + " "
    idx = idx + 1
    feature = feature + str(idx) + ":" +  str(pos_dict[current_pos]) + " "
    idx = idx + 1
    feature = feature + str(idx) + ":" + str(word_dict[current_word.lower()]) + " "
    idx = idx + 1
    feature = feature + str(idx) + ":" + str(int(str.isalpha(current_word))) + " "
    idx = idx + 1
    feature = feature + str(idx) + ":" + str(int(str.isdigit(current_word))) + " "
    idx = idx + 1
    feature = feature + str(idx) + ":" + str(int(str.islower(current_word))) + " "
    idx = idx + 1
    feature = feature + str(idx) + ":" + str(int(str.istitle(current_word))) + " "
    idx = idx + 1
    feature = feature + str(idx) + ":" + str(int(str.isupper(current_word))) + " "
    idx = idx + 1

    for j in range(i+1,i+1+forward_window_size):
        if j >= len(words) :
            feature = feature + str(idx) + ":" + str(default_word) + " "
            idx = idx + 1
            feature = feature + str(idx) + ":" + str(default_pos)+" "
            idx = idx + 1
            feature = feature + str(idx) + ":" + str(default_tag)+" "
            idx = idx + 1
        else:
            feature = feature + str(idx) + ":" + str(word_dict[words[j]]) + " "
            idx = idx + 1
            feature = feature + str(idx) + ":" + str(pos_dict[poss[j]])+" "
            idx = idx + 1

    feature = feature  + "\n"
    test_features_file.write(feature)
test_features_file.close()
