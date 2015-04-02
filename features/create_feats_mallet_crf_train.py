back_window_size = 1
forward_window_size = 1
boolean_features = False
train_file = open("../data/training.txt","r")
train_features_file = open("train_features_crf.txt","w")
words = [];
poss = [];
tags = [];
for line in train_file:
    p = line.strip().split(" ")
    if len(p) > 1:
        word,pos,tag = line.rstrip().split(" ")
        words.append(word)
        poss.append(pos)
        tags.append(tag)
train_file.close()


default_word = "."
default_pos = "."
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

    idx_back = back_window_size
    for j in range(i-back_window_size,i):
        if j < 0:
            #feature = feature + default_word + " " + default_pos+" " +  default_pos+" "
            feature = feature + default_word + " " + default_pos+" "
        else:
            feature = feature +  words[j] + " " + poss[j] + " "
            #feature = feature +  words[j] + " " + poss[j] + " " + tags[j]+ " "
        idx_back = idx_back - 1

    feature = feature + current_word + " " + current_pos + " "
    feature = feature + current_word.lower() + " "
    if boolean_features:    
        feature = feature + str(int(str.isalpha(current_word))) + " "
        feature = feature + str(int(str.isdigit(current_word))) + " "
        feature = feature + str(int(str.islower(current_word))) + " "
        feature = feature + str(int(str.istitle(current_word))) + " "
        feature = feature + str(int(str.isupper(current_word))) + " "

    idx_forward = 1
    for j in range(i+1,i+1+forward_window_size):
        if j >= len(words) :
            feature = feature + default_word + " " + default_pos+" "
        else:
            feature = feature + words[j] + " " + poss[j] + " "
        idx_forward = idx_forward + 1

    feature = feature + tags[i] + "\n"
    train_features_file.write(feature)

train_features_file.close()