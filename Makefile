.PHONY: all clean compile run package install

# Ant wrapper
ANT = ant

all: compile

compile:
	$(ANT) compile

run: compile
	$(ANT) run

package:
	$(ANT) jar

install: package
	@echo "JavaIDE JAR created in build/jar/javaide.jar"
	@echo "Run with: java -jar build/jar/javaide.jar"

clean:
	$(ANT) clean
