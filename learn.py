import sys
import json
import numpy as np
from scipy import linalg as LA

import config
import utils

def learn(task, waves, start_time):
    task = task[start_time:]
    waves = waves[start_time:]

    G = np.array(waves)
    coff = LA.solve(G.T.dot(G), G.T.dot(task))
    return coff

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("usage:\npython learn.py [case]")
        sys.exit(1)

    case = sys.argv[1]

    cnf = config.Config(case + "/config.json")

    tasks = utils.load_tasks(case + "/tasks_train.txt")
    waves = utils.load_waves(case + "/waves_train.txt")

    taskA = [p[0] for p in tasks]
    coffA = learn(taskA, waves, cnf.time_taskA)

    taskB = [p[1] for p in tasks]
    coffB = learn(taskB, waves, cnf.time_taskB)

    for t in range(cnf.time_end):
        yA = 0
        yB = 0
        if t >= cnf.time_taskA:
            yA = coffA.dot(waves[t])
        if t >= cnf.time_taskB:
            yB = coffB.dot(waves[t])
        print("%f\t%f" % (yA, yB))

    file_prams = open(case + "/params.json", mode = "w")
    params = {"coffA": coffA.tolist(), "coffB": coffB.tolist()}
    json.dump(params, file_prams)
