import pandas as pd
from matplotlib import pyplot
from pandas.plotting import scatter_matrix
from sklearn import linear_model, metrics, model_selection

path = "Distances.csv"

names = ['id','d0','d1','d2','d3','d4','d5','d6','d7','d8']

df = pd.read_csv(path, names = names)

mylog_model = linear_model.LogisticRegression()

all = df.values

y = []
x = []

size = int(all.size / all[0].size)

for i in range(size):
    y.append(all[i][0])
    x.append(all[i][1:all[i].size])

X_train, X_test, Y_train, Y_test = model_selection.train_test_split(x,y,test_size=0.3)


iterNum = mylog_model.max_iter
mylog_model.max_iter = 1000
mylog_model.fit(x,y)

y_pred = mylog_model.predict(x)

print(metrics.accuracy_score(y, y_pred))