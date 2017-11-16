REPO_ROOT=./$(shell git rev-parse --show-cdup)

JAVA_SRC=$(shell find "$(REPO_ROOT)" -name *.java)
DNA_RESERVOIR=$(REPO_ROOT)DNAReservoir/
DACCAD=$(REPO_ROOT)daccad/DACCAD/
BIONEAT=$(REPO_ROOT)bioneat/
JAR=$(DNA_RESERVOIR)build/libs/DNAReservoir.jar

ECLIPSE_SETTINGS=$(DNA_RESERVOIR).classpath $(DNA_RESERVOIR).settings $(DNA_RESERVOIR).project $(DACCAD).classpath $(DACCAD).settings $(DACCAD).project $(BIONEAT).classpath $(BIONEAT).settings $(BIONEAT).project

all: $(TARGETS)

jar: $(JAR)

$(JAR): $(JAVA_SRC)
	cd $(DNA_RESERVOIR); ./gradlew build

eclipse: $(DNA_RESERVOIR)/build.gradle $(DACCAD)/build.gradle
	cd $(DNA_RESERVOIR); ./gradlew eclipse
	cd $(DACCAD); ./gradlew eclipse
	cd $(BIONEAT); ./gradlew eclipse

clean:
	cd $(DNA_RESERVOIR); ./gradlew clean
	rm -r $(ECLIPSE_SETTINGS)

.PHONY: all clean jar eclipse
