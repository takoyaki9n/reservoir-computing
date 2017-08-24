#!/Users/wataru/.pyenv/shims/python
import sys

import config

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("dirname is needed.")
        sys.exit(1)

    case_dir = sys.argv[1]
    cnf = config.Config(case_dir + "/config.json")

    fi = open(sys.argv[1] + "/input.txt", "r")
    lines = fi.read().rstrip("\n").split("\n")
    data = [float(x) for x in lines]
    fi.close()

    file_task = open(case_dir + "/tasks.txt", mode = "w")
    taskA = 0
    taskB = 0
    for t in range(cnf.time_end):
        if t >= cnf.time_taskA:
            taskA = data[t - 1] + 2 * data[t - 2]
        if t >= cnf.time_taskB:
            t1 = int(t - cnf.tau)
            t2 = int(t - 1.5 * cnf.tau)
            taskB = data[t1] + 0.5 * data[t2]

        file_task.write("%f\t%f\n" % (taskA, taskB))
    file_task.close()