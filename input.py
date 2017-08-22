#!/Users/wataru/.pyenv/shims/python
import random
import sys

import config

if len(sys.argv) < 2:
    print("dirname is needed.")
    sys.exit(1)

cnf = config.Config(sys.argv[1] + "/config.json")

s1 = 0.0
for t in range(cnf.time_end):
    if t >= cnf.time_start:
        if (t - cnf.time_start) % cnf.tau == 0:
            rnd = random.randrange(cnf.input_levels + 1)
            s1 = cnf.input_step * rnd + cnf.input_min
    print(s1)
