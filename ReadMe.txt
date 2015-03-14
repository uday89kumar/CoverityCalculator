Goal:
Write a	calculator program in Java that evaluates expressions in a very simple intege expression language. 
The program takes an input on the commandline, computes the result, and	prints it to the console. 

For example:
java calculator.Main "add(2, 2)"
4
java calculator.Main "mult(add(2,2),div(9,3))"
12
java calculator.Main "let(a,let(b,10,add(b,b)),let(b,20,add(a,b)))"
40
