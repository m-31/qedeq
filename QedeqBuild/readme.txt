Code Name:   gaffsie
Version:     0.04.07
Timestamp:   2013-05-23 17:53:27
Subversion:  qedeq_unstable_0_04_07_20130523175327

This is an unstable development release of *Hilbert II*. Once again 
this release is not completely tested, not all FIXMEs were solved, 
and the texts are still written in terribly, awfully, bad English 
- but you are welcome to change that ;-)

This release contains a program suite that can produce LaTeX files
and UTF-8 text files out of QEDEQ XML files. The QEDEQ files can be
checked for syntactic correctness. Part of this suite is also a
semantical checker for some finite models of set theory. So you
can check which formula is valid in which model. Also integrated is
a proof checker that can verify the integrity of very simple formal
proofs.

A script with the beginning of axiomatic set theory is included. See
"doc/math/qedeq_set_theory_v1.pdf" for the script.
The logical background of this project is described in 
"doc/math/qedeq_logic_v1.pdf". Both documents were generated with
the current program suite. All formulas of axioms, definitions and
propositions are written in a formal language.

A fully formal mathematical development can be found in
"doc/math/qedeq_formal_logic_v1_en.pdf". It contains all necessary
axioms and inference rules for predicate calculus. Here you find
several logical propositions and their formal proofs. A first meta 
rule - conditional proof - is also introduced and can be used.
All proofs were successfully checked with the proof checker.
This document will be constantly updated as the number of meta rules
grow.

See "sample/qedeq_sample3.xml" for some other
proof samples that demonstrate the usage of all basic inference rules.

See "doc/project/qedeq_logic_language_en.pdf" for 
more details of the formal language. Included are also samples
showing the error handling for incorrect QEDEQ files. Beside the XSD 
verification there are some semantic checks: "are all defined labels 
different" and "are all formulas well formed"?

So one question remains: why is this a "gaffsie" release and not a
"misabel"? We can write down formal proofs and check them!
Yes that is true, but we still can only write very simple formal
proofs; and although the proof checkers were tested there are still
some more JUnit tests to write and the documentation of the formal
language should contain the possible error codes.
So you see there is still some work left before calling it a "misabel"
release...

Precondition to start the program is a Java Runtime Environment, at
least version 1.4.2

The "dev" releases include also the source code, JUnit test classes,
and various code metric results.

PLEASE NOTE: the tar.bz2 archives can only be untared with GNU tar.
The "win" and "unx" releases differ only in the carriage return
character for text files.
