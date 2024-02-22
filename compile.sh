#!/bin/bash
find -name "*.java" > sources.txt
export CLASSPATH=lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar:classes
javac @sources.txt -d classes