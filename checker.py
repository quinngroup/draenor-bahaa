import sys
import math
X = []
with open('data/inputData.csv') as f:
    for line in f:
        X.append([ int(x) for x in line[:-1].split(',')])

def vectorsqdist(x,y):
    return float(sum([(x[i]-y[i])**2 for i in range(len(x))]))
def inverseQuadratic(x,y):
    return 1.0/ ( 1.0 + ( 5.0 * vectorsqdist(x,y) )**2 ) 
def dotProduct(x,y):
    return sum([x[i]*y[i] for i in range(len(x)) ])
def rbf(x,y):
    return math.exp(- vectorsqdist(x,y)/(2.0*5.0*5.0))
computeKernel=rbf
K = [[computeKernel(n,m) for m in X] for n in X]
D = [sum(d) for d in K]

with open(sys.argv[1]) as f:
    for line in f:
        line = line[1:-2].split(',')
        x,y,k = (int(line[0]),int(line[1]),float(line[2]))
        #print(vectorsqdist(X[x],X[x]),vectorsqdist(X[x],X[y]))
        #print(K[x][y], D[x],D[y] )
        c = ( (-K[x][y]/math.sqrt(D[x]*D[y]) )if x != y else ( 1.0 - K[x][x]/float(D[x])))
        if abs(c-k) > 10e-8 : 
            print(x,y, k,c)
            input()



