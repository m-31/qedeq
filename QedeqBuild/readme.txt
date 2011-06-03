Code Name:   gaffsie
Version:     0.04.03
Timestamp:   2011-05-01 07:19:24
Subversion:  qedeq_unstable_0_04_03_20110501071924

This is an unstable development release of *Hilbert II*. 


This release contains a program suite that can produce LaTeX files
and UTF-8 text files out of QEDEQ XML files. The QEDEQ files can be
checked for syntactic correctness. Also part of this suite is a
semantical checker for some finite models of set theory. So you
can check which formula is valid in which model. Newly integrated is
a proof checker that can verify the integrity of simple formal
proofs.

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

Currently no formal proofs can be found in the main mathematical
documents. But "doc/math/qedeq_formal_logic_v1_en.pdf" contains
the propositional axioms that enable us to produce all formal
derivations. There you can find the first formal proofs.
"set_theory_v1" contains some informal proofs that are close to 
formal proofs. Further more "sample/qedeq_sample3.xml" contains 
more formal proofs. Currently only the usage of basic proof methods 
are permitted. See "doc/project/qedeq_logic_language_en.pdf" for 
more details.

Precondition to start the program is a Java Runtime Environment, at
least version 1.4.2

The "dev" releases include also the source code, JUnit test classes,
and various code metric results.

PLEASE NOTE: the tar.bz2 archives can only be untared with GNU tar.
The "win" and "unx" releases differ only in the carriage return
character for text files.
