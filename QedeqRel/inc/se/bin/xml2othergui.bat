@echo off
REM this script generates LaTeX files out of *Hilbert II*s XML files

REM remove the following line for Windows 98 or below
@setlocal

REM change to application directory
cd ..

REM set java classpath
set QEDEQ_CP=config;lib/qedeq_se.jar;lib/xercesImpl.jar;lib/xml-apis.jar;lib/commons-logging-1.1.jar;lib/log4j-1.2.14.jar

REM start program
REM if "java" is not in the path you must add the JRE bin dictory to your path or
REM fill in the full path to the java executable
java -cp %QEDEQ_CP% org.qedeq.kernel.rel.test.gui.Xml2OtherGui %1 %2 %3 %4 %5 %6 %7 %8 %9

REM remove the following line for Windows 98 or below
@endlocal
