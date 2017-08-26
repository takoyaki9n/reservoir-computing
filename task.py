import sys

import config

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("usage:\npython task.py [case] [mode]")
        sys.exit(1)

    case = sys.argv[1]
    mode = sys.argv[2]

    cnf = config.Config(case + "/config.json")

    fi = open("%s/input_%s.txt" % (case, mode), "r")
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
