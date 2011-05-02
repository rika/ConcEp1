
all: compile

compile:
	javac simulator/*.java
	javac plot/GpiWriter.java

clean:
	rm -f plot/GpiWriter.class
	rm -f simulator/*.class
	rm -f plot/*.gpi

