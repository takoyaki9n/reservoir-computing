
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
