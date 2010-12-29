Code Name:   gaffsie
Version:     0.04.00
Timestamp:   2010-12-28 20:21:23
Subversion:  qedeq_unstable_0_04_00_20101228202123

This is an unstable development release of *Hilbert II*. 


This release contains a program suite that can produce LaTeX files
and UTF-8 text files out of QEDEQ XML files. The QEDEQ files can be
checked for syntactic correctness. Also part of this suite is a
semantical checker for some finite models of set theory. So you
can check which formula is valid in which model.

Samples and a script with the beginning of axiomatic set theory
"qedeq_set_theory_v1.pdf" are included. The logical background of
this project is described in "qedeq_logic_v1.pdf". Both documents
were generated with the program suite. All formulas of axioms,
definitions and propositions are written in a formal language. 
Included are also samples showing the error handling for incorrect
QEDEQ files. Beside the XSD verification there are some semantic
checks: "are all defined labels different" and "are all formulas
well formed"?

Formal proofs and their verification are not yet integrated in the
program suite. At least "set_theory_v1" contains some informal
proofs that can be easily transformed into formal ones.


Precondition to start the program is a Java Runtime Environment, at
least version 1.4.2

The "dev" releases include also the source code, JUnit test classes,
and various code metric results.

PLEASE NOTE: the tar.bz2 archives can only be untared with GNU tar.
The "win" and "unx" releases differ only in the carriage return
character for text files.
