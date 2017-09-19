REPO_ROOT=./$(shell git rev-parse --show-cdup)

JAVA_SRC=$(shell find "$(REPO_ROOT)java" -name *.java)
DNA_RESERVOIR=$(REPO_ROOT)java/DNAReservoir/
JAR=$(DNA_RESERVOIR)build/libs/DNAReservoir.jar

all: $(TARGETS)

jar: $(JAR)

$(JAR): $(JAVA_SRC)
	cd $(DNA_RESERVOIR); gradle build

clean:
	cd $(DNA_RESERVOIR); gradle clean

.PHONY: all clean jar
