# CalculatorTM
Simple calculator in Java


Run Home.java for the home menu.
Spawn a server, and multiple Simple calculators and/or Matrix calculators as needed.
Calculators need to connect to the server to calculate.

Simple calculator:
	Can deal properly with PMDAS. Infix notation.
	Note: if you want to input a negative number, press the Neg button rather than the - button!
	They look the same in the textField but are treated differently in the backend.
	Ex: if you want to do -1 + 2, press 'Neg' '1' '+' '2', NOT '-' '1' '+' '2'.
	Prev. Ans button inputs the last answer returned by the server. Default is 0.
	Del removes last character, clear clears all input.
	
	Try:
		3 * (5 + 2) -> correctly returns 21.0
		5 * (4 - 2) - 6 / 3 -> correctly returns 8.0
		

Matrix calculator:
If A = [1, 6] [9, 3.5] and B = [0 -1] [-1 2],
input for A value as space-delimited numbers going from row to row, hence: 1 6 9 3.5
and B: 0 -1 -1 2
	
Can do matrix addition, subtraction, and multiplication.
The input is not cleared unless Clear button is pressed for convenience.
