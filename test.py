import sys
import json
import numpy as np
from scipy import linalg as LA

import config

def load_waves(wave_file):
    f = open(wave_file, "r")
    next(f)
    waves = []
    for line in f:
        line = line.rstrip("\t\n").split("\t")
        row = [float(d) for d in line]
        row.append(1.0)
        waves.append(row)
    f.close()
    return waves

def load_tasks(task_file):
    f = open(task_file, "r")
    tasks = []
    for line in f:
        line = line.rstrip("\n")
        row = line.split("\t")
        pair = [float(d) for d in row]
        tasks.append(pair)
    f.close()
    return tasks

def load_params(param_file):
    f = open(param_file, "r")
    params = json.loads(f.read())
    f.close()
    return params

if __name__ == "__main__":
    if len(sys.argv) < 4:
        print("dirname is needed.")
        sys.exit(1)

    case_dir = sys.argv[1]

    cnf = config.Config(case_dir + "/config.json")

    params = load_params(case_dir + "/params.json")
    coffA = np.array(params["coffA"])
    coffB = np.array(params["coffB"])

    tasks = load_tasks(sys.argv[2])
    waves = load_waves(sys.argv[3])

    for t in range(cnf.time_end):
        yA = 0
        yB = 0
        if t >= cnf.time_taskA:
            yA = coffA.dot(waves[t])
        if t >= cnf.time_taskB:
            yB = coffB.dot(waves[t])
        print("%f\t%f" % (yA, yB))
