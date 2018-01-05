import numpy as np
from scipy.optimize import nnls
import sys
f = open(sys.argv[1], 'r')
m = int(f.readline())
A = np.array([[float(x) for x in f.readline().rstrip().split(' ')] for _ in range(m)])
b = np.array([float(x) for x in f.readline().rstrip().split(' ')])
f.close()
(x, rnorm) = nnls(A,b)
print('\n'.join([str(d) for d in x]))