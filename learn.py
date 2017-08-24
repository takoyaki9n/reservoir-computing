#!/Users/wataru/.pyenv/shims/python
import sys
import numpy as np
from scipy import linalg as LA

import config

def load_waves(case_dir):
    f = open(case_dir + "/waves.txt", "r")
    next(f)
    waves = []
    for line in f:
        line = line.rstrip("\t\n").split("\t")
        row = [float(d) for d in line]
        row.append(1.0)
        waves.append(row)
    f.close()
    return waves

def load_tasks(case_dir):
    f = open(case_dir + "/tasks.txt", "r")
    tasks = []
    for line in f:
        line = line.rstrip("\n")
        row = line.split("\t")
        pair = [float(d) for d in row]
        tasks.append(pair)
    f.close()
    return tasks

def learn(task, waves):
    G = np.array(waves)
    coff = LA.solve(G.T.dot(G), G.T.dot(task))
    return coff

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("dirname is needed.")
        sys.exit(1)

    case_dir = sys.argv[1]

    cnf = config.Config(case_dir + "/config.json")

    waves = load_waves(case_dir)
    tasks = load_tasks(case_dir)

    taskA = [p[0] for p in tasks]
    taskA = taskA[cnf.time_taskA:]
    wavesA = waves[cnf.time_taskA:]
    coffA = learn(taskA, wavesA)

    taskB = [p[1] for p in tasks]
    taskB = taskB[cnf.time_taskB:]
    wavesB = waves[cnf.time_taskB:]
    coffB = learn(taskB, wavesB)

    print(coffA)
    print(coffB)

    file_result = open(case_dir + "/result.txt", mode = "w")
    for t in range(cnf.time_end):
        yA = 0
        yB = 0
        if t >= cnf.time_taskA:
            yA = coffA.dot(waves[t])
        if t >= cnf.time_taskB:
            yB = coffB.dot(waves[t])
        file_result.write("%f\t%f\n" % (yA, yB))
    file_result.close()