@echo off
REM this script starts the standalone text front end of *Hilbert II*

REM remove the following line for Windows 98 or below
@setlocal

REM set http proxy (if any)
set QEDEQ_PROXY=
REM if you need a proxy please remove the leading REM of the following line and adjust the settings
REM set QEDEQ_PROXY=-DproxySet=true -DproxyHost=myProxyHost -DproxyPort=myProxyPort

REM set java classpath
set QEDEQ_CP=config;lib/qedeq_gui_se-@QEDEQ_VERSION@.jar;lib/qedeq_kernel-@QEDEQ_VERSION@.jar;lib/xercesImpl.jar;lib/xml-apis.jar;lib/commons-io-1.4.jar;lib/commons-lang-2.4.jar;lib/commons-logging-1.1.1.jar;lib/commons-httpclient-3.1.jar;lib/commons-codec-1.3.jar;lib/log4j-1.2.14.jar;lib/forms-1.1.0.jar;lib/looks-2.1.4.jar;lib/jh-2.0.5.jar

REM set splash screen (activate for java 1.6)
REM set QEDEQ_SPLASH=-splash:qedeq.png

REM start program
REM if "java" is not in the path you must add the JRE bin directory to your path or
REM fill in the full path to the java executable
java -Xms64m -Xmx1024m %QEDEQ_PROXY% %QEDEQ_SPLASH% -cp %QEDEQ_CP% org.qedeq.text.se.main.Main %1 %2 %3 %4 %5 %6 %7 %8 %9

REM remove the following line for Windows 98 or below
@endlocal
