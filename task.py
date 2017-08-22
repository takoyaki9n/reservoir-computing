#!/Users/wataru/.pyenv/shims/python
import sys

import config

if len(sys.argv) < 2:
    print("dirname is needed.")
    sys.exit(1)

cnf = config.Config(sys.argv[1] + "/config.json")

fi = open(sys.argv[1] + "/input.txt", "r")
lines = fi.read().rstrip("\n").split("\n")
data = [float(x) for x in lines]
fi.close()

taskA = 0
taskB = 0
for t in range(cnf.time_end):
    if t >= cnf.time_taskA:
        taskA = data[t - 1] + 2 * data[t - 2]
    if t >= cnf.time_taskB:
        t1 = int(t - cnf.tau)
        t2 = int(t - 1.5 * cnf.tau)
        taskB = data[t1] + 0.5 * data[t2]

    print("%f\t%f" % (taskA, taskB))