Code Name:   gaffsie
Version:     0.04.01
Timestamp:   2011-03-04 21:45:39
Subversion:  qedeq_unstable_0_04_01_20110304214539

This is an unstable development release of *Hilbert II*. 


This release contains a program suite that can produce LaTeX files
and UTF-8 text files out of QEDEQ XML files. The QEDEQ files can be
checked for syntactic correctness. Also part of this suite is a
semantical checker for some finite models of set theory. So you
can check which formula is valid in which model.

Samples and a script with the beginning of axiomatic set theory are
included. See "doc/math/qedeq_set_theory_v1.pdf" for the script.
The logical background of this project is described in 
"doc/math/qedeq_logic_v1.pdf". Both documents were generated with
the current program suite. All formulas of axioms, definitions and
propositions are written in a formal language.

Included are also samples showing the error handling for incorrect
QEDEQ files. Beside the XSD verification there are some semantic
checks: "are all defined labels different" and "are all formulas
well formed"?

Verification of formal proofs is not yet integrated in the program
suite. At least "set_theory_v1" contains some informal proofs that
are close to formal proofs. Further more "sample/qedeq_sample3.xml"
contains some formal proofs. Currently these proofs can only be
viewed and only basic proof rules are permitted.
See "doc/project/qedeq_logic_language_en.pdf" for more details.

Precondition to start the program is a Java Runtime Environment, at
least version 1.4.2

The "dev" releases include also the source code, JUnit test classes,
and various code metric results.

PLEASE NOTE: the tar.bz2 archives can only be untared with GNU tar.
The "win" and "unx" releases differ only in the carriage return
character for text files.
