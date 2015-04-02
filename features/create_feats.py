back_window_size = 1
forward_window_size = 1
boolean_features = True
prev_tag = False

train_file = open("../data/training.txt","r")
train_features_file = open("train_features.txt","w")
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

    idx_back = back_window_size
    for j in range(i-back_window_size,i):
        if j < 0:
            if prev_tag:
                feature = feature + "prev" + str(idx_back) + "word" + "=" + default_word + " " + "prev" + str(idx_back) + "pos" + "=" + default_pos+" " + "prev" + str(idx_back) + "tag" + "=" + default_pos+" "
            else:
                feature = feature + "prev" + str(idx_back) + "word" + "=" + default_word + " " + "prev" + str(idx_back) + "pos" + "=" + default_pos+" "        

        else:
            if prev_tag:
                feature = feature + "prev" + str(idx_back) + "word" + "=" + words[j] + " " + "prev" + str(idx_back) + "pos" + "=" + poss[j] + " " + "prev" + str(idx_back) + "tag" + "=" + tags[j]+ " "
            else:
                feature = feature + "prev" + str(idx_back) + "word" + "=" + words[j] + " " + "prev" + str(idx_back) + "pos" + "=" + poss[j] + " "
        idx_back = idx_back - 1
    
    feature = feature + "currentword=" + current_word + " " + "currentpos=" + current_pos + " "
    feature = feature + "forcesmall=" + current_word.lower() + " "    
    
    if boolean_features:    
        feature = feature + "isalpha=" + str(int(str.isalpha(current_word))) + " "
        feature = feature + "isdigit=" + str(int(str.isdigit(current_word))) + " "
        feature = feature + "islower=" + str(int(str.islower(current_word))) + " "
        feature = feature + "istitle=" + str(int(str.istitle(current_word))) + " "
        feature = feature + "isupper=" + str(int(str.isupper(current_word))) + " "

    idx_forward = 1
    for j in range(i+1,i+1+forward_window_size):
        if j >= len(words) :
            feature = feature + "next" + str(idx_forward) + "word" + "=" + default_word + " " + "next" + str(idx_forward) + "pos" + "=" + default_pos+" "
        else:
            feature = feature + "next" + str(idx_forward) + "word" + "=" + words[j] + " " + "next" + str(idx_forward) + "pos" + "=" + poss[j] + " "
        idx_forward = idx_forward + 1

    feature = feature + tags[i] + "\n"
    train_features_file.write(feature)

train_features_file.close()