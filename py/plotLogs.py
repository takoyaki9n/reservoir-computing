import json
import os

from config import *

if __name__ == "__main__":
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

    f = open(tmpDir + "/logs.dat", "w")
    f.write("\n".join(["\t".join(r) for r in rowsA]) + "\n")
    f.write("\n\n")
    f.write("\n".join(["\t".join(r) for r in rowsB]) + "\n")
    f.close()

    cmd = "gnuplot -e \"dir='%s'\" %s/plt/logs.plt " % (tmpDir, baseDir)
    print(cmd)
    os.system(cmd)

