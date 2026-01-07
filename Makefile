.PHONY: all clean compile run package install

# Maven wrapper
MVN = mvn

all: compile

compile:
	$(MVN) compile

run: compile
	$(MVN) exec:java -Dexec.mainClass="com.javaide.JavaIDE"

package:
	$(MVN) package

install: package
	@echo "JavaIDE JAR created in target/javaide-1.0.0.jar"
	@echo "Run with: java -jar target/javaide-1.0.0.jar"

clean:
	$(MVN) clean
