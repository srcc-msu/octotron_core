TGT=bin/octotron.jar

all: build

build: test package copy

quickbuild: package copy

package:
	mvn dependency:copy-dependencies
	mvn -Dmaven.test.skip=true package assembly:single

test:
	mvn test

copy:
	cp `ls -tr target/octotron*jar-with-dependencies.jar | tail -n1` $(TGT)

clean:
	rm -f $(TGT)

veryclean: clean
	mvn clean

.PHONY: build quickbuild package test copy clean veryclean
