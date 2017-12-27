import json
from os.path import abspath, dirname

if __name__ == "__main__":
    baseDir = dirname(dirname(abspath(__file__)))
    casesDir = baseDir + "/cases"
    tmpDir = baseDir + "/tmp"

    pref = "osc3"
    taus = ["10", "25", "50", "100"]
    maxs = ["01", "025", "05", "1"]
    maxVals = {"01": 0.1, "025": 0.25, "05": 0.5, "1": 1.0}

    rowsA =[]
    rowsB = []
    for mx in maxs:
        rowA = [maxVals[mx]]
        rowB = [maxVals[mx]]
        for tau in taus:
            caseDir = casesDir + "/%s_%s_%s" % (pref, tau, mx)
            logFile = caseDir + "/log.json"
            f = open(logFile)
            params = json.load(f)
            f.close()
            rowA += [params["TaskA"]["average"], params["TaskA"]["sigma"]]
            rowB += [params["TaskB10"]["average"], params["TaskB10"]["sigma"]]
        rowsA.append([str(x) for x in rowA])
        rowsB.append([str(x) for x in rowB])
    print("\n".join(["\t".join(r) for r in rowsA]))
    print("\n")
    print("\n".join(["\t".join(r) for r in rowsB]))
