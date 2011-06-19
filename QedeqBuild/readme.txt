Code Name:   gaffsie
Version:     0.04.04
Timestamp:   2011-06-13 11:03:12
Subversion:  qedeq_unstable_0_04_04_20110613110312

This is an unstable development release of *Hilbert II*. Again this
release is not fully tested, not all FIXMEs were solved, and the
texts are still written in terribly, awfully, bad English - but you
are welcome to change that ;-)

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

A beginning of a fully formal development can be found in
"doc/math/qedeq_formal_logic_v1_en.pdf". It contains all necessary
axioms and inference rules for predicate calculus. Here you find
some logical propositions and their formal proofs. A first meta
rule - conditional proof - is also introduced and can also be checked
with the proof checker. See "sample/qedeq_sample3.xml" for some other
proof samples. See "doc/project/qedeq_logic_language_en.pdf" for 
more details of the formal language. "set_theory_v1" contains some
informal proofs that are close to 

So one question remains: why is this a "gaffsie" release and not a
"misabel"? We can write down simple formal proofs and check them!
Yes that is true, but we still can only write very simple formal
proofs; and although the proof checkers were tested there are still
some JUnit tests to write and the documentation of the formal
language should contain the possible error codes; it is not
checked if a rule is already defined before it is used; the LaTeX
generator doesn't link to the rule definition when a rule is used.
So you see there is some work left before calling it a "misabel"
release...

Precondition to start the program is a Java Runtime Environment, at
least version 1.4.2

The "dev" releases include also the source code, JUnit test classes,
and various code metric results.

PLEASE NOTE: the tar.bz2 archives can only be untared with GNU tar.
The "win" and "unx" releases differ only in the carriage return
character for text files.
