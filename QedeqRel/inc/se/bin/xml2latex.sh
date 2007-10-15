#!/bin/sh

# this script generates LaTeX files out of *Hilbert II*s XML files

# change to directory above script directory
cd $(dirname $0)/..

# set java classpath
QEDEQ_CP=config:lib/qedeq_se.jar:lib/xercesImpl.jar:lib/xml-apis.jar:lib/commons-logging-1.1.jar:lib/log4j-1.2.14.jar

#start program
# if "java" is not in the path you must add the JRE bin dictory to your path or
# fill in the full path to the java executable
java -cp $QEDEQ_CP org.qedeq.kernel.rel.test.text.Xml2Latex "$@"




