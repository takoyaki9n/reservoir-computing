import sys
from os.path import abspath, dirname

baseDir = dirname(dirname(abspath(__file__)))
casesDir = baseDir + "/cases"
tmpDir = baseDir + "/tmp"

pref = sys.argv[1]
taus = ["10", "25", "50", "100"]
maxs = ["01", "025", "05", "1", "2"]
maxVals = {"01": 0.1, "025": 0.25, "05": 0.5, "1": 1.0, "2": 2.0}
