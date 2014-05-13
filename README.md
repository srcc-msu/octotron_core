Octotron core repository
========

This is a core of [Octotron](http://github.com/srcc-msu/octotron) system - all sources needed to build octotron.jar, that is used in the basic Octotron distribution. You will not need those files for usual work with the system.

Visit http://github.com/srcc-msu/octotron if you want to get started with Octotron - a framework for modeling and operational control of complex computer system.

Requirements
========

* JDK 1.7, Maven
* (Optional) make

Building and testing with maven
========

Build jar with all dependencies and run unit tests use next commands:

	mvn dependency:copy-dependencies
	mvn package assembly:single

If the build is successfull jar file `octotron-%version%-jar-with-dependencies.jar` will be located in the `target/` folder.

Build without unit tests:

	mvn dependency:copy-dependencies
	mvn -Dmaven.test.skip=true package assembly:single

Clean all maven files:

    mvn clean

Building with make
========

Makefile contains some convenient shortcuts.

Build jar, test it and put the result to `bin/` folder with name octotron.jar:

    make build

Build without unit tests:

    make quickbuild

Clean the target folder (`bin/`):

    make clean

Clean the target folder and maven files:

    make veryclean
