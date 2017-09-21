REPO_ROOT=./$(shell git rev-parse --show-cdup)

JAVA_SRC=$(shell find "$(REPO_ROOT)java" -name *.java)
DNA_RESERVOIR=$(REPO_ROOT)java/DNAReservoir/
DACCAD=$(REPO_ROOT)java/daccad/DACCAD/
JAR=$(DNA_RESERVOIR)build/libs/DNAReservoir.jar

ECLIPSE_SETTINGS=$(DNA_RESERVOIR).classpath $(DNA_RESERVOIR).settings $(DNA_RESERVOIR).project $(DACCAD).classpath $(DACCAD).settings $(DACCAD).project

all: $(TARGETS)

jar: $(JAR)

$(JAR): $(JAVA_SRC)
	cd $(DNA_RESERVOIR); ./gradlew build

eclipse: $(ECLIPSE_SETTINGS)
	cd $(DNA_RESERVOIR); ./gradlew eclipse
	cd $(DACCAD); ./gradlew eclipse

clean:
	cd $(DNA_RESERVOIR); ./gradlew clean
	rm -r $(ECLIPSE_SETTINGS)

.PHONY: all clean jar eclipse
