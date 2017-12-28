import json
import os

from config import *

if __name__ == "__main__":
    f = open(casesDir + "/osc3_10_01/config.json")
    template = json.load(f)
    f.close()
    for mx in maxs:
        for tau in taus:
            caseDir = casesDir + "/%s_%s_%s" % (pref, tau, mx)
            if not os.path.isdir(caseDir):
                os.mkdir(caseDir)
            template["inputs"][0]["max"] = maxVals[mx]
            template["inputs"][0]["interval"] = int(tau)
            template["tasks"][1]["interval"] = int(tau)
            print(template)

            conf = caseDir + "/config.json"
            json.dump(template, open(conf, "w"))

            cmd = "java -jar %s/DNAReservoir/build/libs/DNAReservoir.jar -c %s" % (baseDir, conf)
            print(cmd)
            os.system(cmd)