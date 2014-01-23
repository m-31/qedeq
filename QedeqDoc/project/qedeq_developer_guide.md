developer guide
===============

Allgemeines
===========

Das Format der QEDEQ-Dateien ist durch eine XSD definiert.
Es gibt eine Referenzimplementierung, die mit solchen Dateien
umgehen kann. D.h. sie kann Überprüfungen vornehmen, LaTeX-Ausgaben
erzeugen u.ä.

Die Referenzimplementierung ist eine Java 1.4.2 GUI-Anwendung,
welches standalone direkt oder via Java-Webstart aufgerufen werden
kann.

Umgebung
========

Aus Kompatibilitätsgründen Java 1.4.2 Programm 

Entwicklung erfolgt unter Eclipse

Build erfolgt mit Ant unter Java 1.6

Es werden auch Maven POMs bereitgestellt

Der Sourcecode liegt in UTF-8 mit Unix-Zeilentrennung vor.

Subversion Repository Location:
<https://pmii.svn.sourceforge.net/svnroot/pmii>

Für den aktuellen Stand auf "trunk" gehen

Folgende Projekte als Eclipse-Projekte auschecken:

    QedeqBase
    QedeqBaseTest
    QedeqBuild
    QedeqDoc
    QedeqGuiSe
    QedeqKernelBo
    QedeqKernelBoTest
    QedeqKernelSe
    QedeqKernelSeTest
    QedeqKernelXml
    QedeqKernelXmlTest
    QedeqLib


Architektur
===========
Grundsätzlich gibt es vier Schichten, die auch in einzelnen
Eclipse-Projekten gekapselt sind:

QedeqBase
---------
Basisklassen, die projektunabhängig sind.

QedeqKernelSe
-------------
Basisklassen des Projekts. Enthält auch alle Value-Objekte.

QedeqKernelBo
-------------
Hier können die Services des Kernels abgerufen werden.


QedeqKernelXml
--------------
Nur hier wird XML gesprochen. Parsen von XML-Dateien und
Serialisierung in XML-Dateien kann hier passieren.


QedeqGuiSe
----------
Hier wird der Kernel initialisiert. Hier wird auch erst die KernelBo-Schicht
mit der KernelXml-Schicht verknüpft.


Allgemeines
-----------
Verwendung von ElementList und Atom als Basiselemente der formalen Sprache.
Vorteil: besseres Patternmatching auf Baumebene möglich.
Flexibel, da untypisiert auf unterster Ebene.


Code-Konventionen
=================
Checkstyle


CI-Server
=========
Falls Code eingecheckt wird, wirft der CI-Server (Hudsen aka Jenkins) das
Build-Script an und baut ein neues Release. Dazu gehört die Erzeugung des
von Auswertungen bezüglich bestimmter Metriken.
Der CI-Server zeigt diese an.
Neben Checkstyle wird auch Clover, FindBugs und JDepend werden natürlich
auch die Ergebnisse der JUnit-Tests ermittelt.

Nightly build auf <http://qedeq.org/nightly> abrufbar. Dazu gehört auch der Maven-Build.


Entwicklungsziele
=================

Kurzfristig
-----------
Erzeugung von HTML Output.

Langfristig
-----------
Einen mehr oder weniger komfortablen Editor für QEDEQ Module.
Andere Input-Formate als XML.
Besseres Automatisiertes Erzeugen von QEDEQ-Modulen aus LaTeX Texten.


Entwickungsprobleme
===================

Verquickung von lokalem mit globalem durch LaTeX
------------------------------------------------
Aktuell ist das Ziel, ein gut verwendbares LaTeX Dokument zur
erhalten. Daher ist die Schaffung eines Indexeintrages eine "lokale"
LaTeX-Anweisung, die "globale" Konsequenzen hat. Ist das ein Problem?

Unterstützung von zu vielen LaTeX-Konstrukten
---------------------------------------------
Aktuell wird nur bei LaTeX2Unicode der LaTeX-Content geparst und Probleme
dabei aufgezeigt.


