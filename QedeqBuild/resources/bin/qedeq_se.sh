#!/bin/sh

# this script starts the standalone GUI front end of *Hilbert II*

# change to directory of script directory
cd $(dirname $0)

# set http proxy (if any)
QEDEQ_PROXY=
# if you need a proxy please remove the leading # of the following line and adjust the settings
# QEDEQ_PROXY="-DproxySet=true  -DproxyHost=myProxyHost -DproxyPort=myProxyPort" 

# set java classpath
QEDEQ_CP=config:lib/qedeq_gui_se-@QEDEQ_VERSION@.jar:lib/qedeq_kernel-@QEDEQ_VERSION@.jar:lib/xercesImpl.jar:lib/xml-apis.jar:lib/commons-io-1.4.jar:lib/commons-lang-2.4.jar:lib/commons-logging-1.1.1.jar:lib/commons-httpclient-3.1.jar:lib/commons-codec-1.3.jar:lib/log4j-1.2.14.jar:lib/forms-1.1.0.jar:lib/looks-2.1.4.jar

# set splash screen (activate for java 1.6)
# set QEDEQ_SPLASH=-splash:qedeq.png

#start program
# if "java" is not in the path you must add the JRE bin dictory to your path or
# fill in the full path to the java executable
java $QEDEQ_PROXY $QEDEQ_SPLASH -cp $QEDEQ_CP org.qedeq.gui.se.main.QedeqMainFrame "$@"




