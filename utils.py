import json
import numpy as np
from scipy import linalg as LA

def load_input(input_file):
    f = open(input_file, "r")
    lines = f.read().rstrip("\n").split("\n")
    data = [float(x) for x in lines]
    f.close()
    return data

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


def learn(task, waves, start_time):
    task = task[start_time:]
    waves = waves[start_time:]

    G = np.array(waves)
    coff = LA.solve(G.T.dot(G), G.T.dot(task))
    return coff

def NRMSE(task, reg, start_time):
    task = task[start_time:]
    reg = reg[start_time:]

    n = len(task)
    mx = reg[0]
    mn = reg[0]
    for i in range(1, n):
        if mx < reg[i]: mx = reg[i]
        if mn > reg[i]: mn = reg[i]

    nrmse = 0
    for i in range(n):
        nrmse += (reg[i] - task[i]) ** 2
    nrmse = np.sqrt(nrmse / n) / (mx - mn)

    return nrmse