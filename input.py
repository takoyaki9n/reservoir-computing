import random
import sys

import config

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("usage:\npython input.py [case]")
        sys.exit(1)

    case = sys.argv[1]
    cnf = config.Config(case + "/config.json")

    s1 = 0.0
    for t in range(cnf.time_end):
        if t >= cnf.time_start:
            if (t - cnf.time_start) % cnf.tau == 0:
                rnd = random.randrange(cnf.input_levels + 1)
                s1 = cnf.input_step * rnd + cnf.input_min
        print("%f" % (s1))