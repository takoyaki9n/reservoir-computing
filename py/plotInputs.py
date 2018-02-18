from os import system

from config import *

if __name__ == "__main__":
    for mx in maxs:
        for tau in taus:
            caseDir = casesDir + "/%s/%s_%s" % (pref, tau, mx)
            cmd = "gnuplot -e \"dir='%s';tau='%s';S='%s'\" %s/plt/inputs.plt" % (caseDir, tau, maxVals[mx], baseDir)
            print(cmd)
            system(cmd)
