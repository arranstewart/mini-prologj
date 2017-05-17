# Java 8 mini-prolog

The inference engine portion of a mini Prolog-style interpreter,
based on the Javascript version of [curiosity-driven][cur-prolog].

[cur-prolog]: https://curiosity-driven.org/prolog-interpreter

That version uses EcmaScript 6 generators and "yield" to do backtracking.
In Java 8, streams can be used to achieve similar functionality.
The result is fairly concise, for Java code.

### Building and running

Build with

> ``> ant``

You'll need to edit the `build.xml` file to insert the correct path to
Junit 4 libraries. 

There's no parser, so terms are built using Java code. The test classes
give a few examples.


