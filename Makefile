main: clean Server.java Client.java
	javac -cp . Server.java
	javac -cp . Client.java

clean:
	rm -f *.class