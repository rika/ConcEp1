#!/bin/bash -v
#java  -XX:MaxPermSize=512m -Xms512m -Xmx512m -Xss16k simulator/Router
java -Xss48k simulator/Router
