import sys
import json
import numpy as np
from scipy import linalg as LA

import config
import utils

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("usage:\npython test.py [case]")
        sys.exit(1)

    case = sys.argv[1]

    cnf = config.Config(case + "/config.json")

    params = utils.load_params(case + "/params.json")
    coffA = np.array(params["coffA"])
    coffB = np.array(params["coffB"])

    tasks = utils.load_tasks(case + "/tasks_test.txt")
    waves = utils.load_waves(case + "/waves_test.txt")

    taskA = [p[0] for p in tasks]
    taskB = [p[1] for p in tasks]

    regA = []
    regB = []
    for t in range(cnf.time_end):
        yA = 0
        yB = 0
        if t >= cnf.time_taskA:
            yA = coffA.dot(waves[t])
        if t >= cnf.time_taskB:
            yB = coffB.dot(waves[t])
        print("%f\t%f" % (yA, yB))
        regA.append(yA)
        regB.append(yB)

    file_nrmse = open(case + "/nrmse.json", mode = "w")
    nrmse = {"A": utils.NRMSE(taskA, regA, cnf.time_taskA), "B": utils.NRMSE(taskB, regB, cnf.time_taskB)}
    json.dump(nrmse, file_nrmse, sort_keys = True, indent = 4)
    file_nrmse.close()