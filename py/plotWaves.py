from os import system

from config import *

if __name__ == "__main__":
    for mx in maxs:
        for tau in taus:
            caseDir = casesDir + "/%s_%s_%s" % (pref, tau, mx)
            cmd = "gnuplot -e \"dir='%s'\" %s/plt/waves.plt" % (caseDir, baseDir)
            print(cmd)
            system(cmd)
