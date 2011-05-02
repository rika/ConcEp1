
all: compile

compile:
	javac simulator/*.java
	javac GpiWriter.java

plot:
	java GpiWriter
	gnuplot plot/*.gpi

clean:
	rm -f GpiWriter.class
	rm -f simulator/*.class
	rm -f plot/*.gpi

