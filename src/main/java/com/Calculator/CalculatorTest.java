package com.Calculator;

import static org.junit.Assert.*;

import java.util.Stack;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

public class CalculatorTest {

	static final Logger logger = Logger.getLogger(Calculator.class);
	
	@Test
	public void test() {
		//PropertiesConfigurator is used to configure logger from properties file
		PropertyConfigurator.configure("log4j.properties");
		Stack<String> myStack = new Stack<String>();
		
		//Addition Test Case
		myStack.push("ADD");
		assertEquals(Long.parseLong("25"), Long.parseLong(Calculator.computeExpression("ADD(15,10)", myStack)));
		myStack = new Stack<String>();
		//Multiplication Test Case
		myStack.push("MULT");
		assertEquals(Long.parseLong("50"), Long.parseLong(Calculator.computeExpression("MULT(5,10)", myStack)));
		myStack= new Stack<String>();
		//Division Test Case
		myStack.push("DIV");
		assertEquals(Long.parseLong("3"), Long.parseLong(Calculator.computeExpression("DIV(15,5)", myStack)));

		//Extract Parameters Method
		String expression ="LET(A,LET(B,10,ADD(B,B)),MULT(A,10))";
		String actual = Calculator.extractLetParameters(expression).toString();
		assertEquals("[A, LET(B,10,ADD(B,B)), MULT(A,10)]", actual);
		
		//Validate Expression Method
		expression = "(A,10,20,ADD(29,29))";
		boolean valid = Calculator.validateExpression(expression);
		assertEquals(valid, true);
		
	}

}
