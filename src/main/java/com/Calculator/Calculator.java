package com.Calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

public class Calculator {

	static final Logger logger = Logger.getLogger(Calculator.class);
	static boolean invalidExp = false;
	static Double dResult = 0.0;
	public static void main(String[] args) {
		try{
			//PropertiesConfigurator is used to configure logger from properties file
			PropertyConfigurator.configure("log4j.properties");
			//Log in log file
			logger.debug("Main Program Starts");
			@SuppressWarnings("resource")
			Scanner sc = new Scanner(System.in);
			String str = "";
			System.out.print("Expression> ");
			while(!(str = sc.nextLine()).equals("quit"))
			{
				//Removing all spaces
				str = str.replace(" ","");
				//Replacing all decimals with integral values i.e. => 1.34 becomes 134
				str = str.replace(".", "");
				str = str.toUpperCase();
				if(validateExpression(str)){
					int result = Integer.parseInt(decideOperation(str));
					if(!invalidExp){
						if(dResult>Integer.MAX_VALUE || dResult<Integer.MIN_VALUE){
							System.out.println("Operation results beyond Integer capacity");
						}
						else {
							System.out.println("Output = " + result);
						}
					}
					else {
						logger.info("Invalid Operation");
						System.out.println("Output = Invalid Expression");
					}
					logger.info(str + " = " + result);
					System.out.print("Expression> ");
				}
				else {
					logger.info("Invalid Parenthesis");
					System.out.println("Output = Invalid Expression");
					System.out.print("Expression> ");
				}
			}
			logger.debug("Main Program Ends");
		}
		catch(NumberFormatException nfe){
			System.out.println("Number Format Exception: Please try numbers in the (-2147483647 to 2147483647) range");
			logger.error("Exception :" + nfe.getMessage());
		}
		catch(Exception e){
			System.out.println("An exception has occured, please try again.");
			System.out.println("Exception : " + e.getMessage());
			logger.error("Exception :" + e.getMessage());
		}
	}

	/* 
	 * Recursively calling this for determining the operations
	 * @Params : String input - Expression to be parsed for operations
	 */
	private static String decideOperation(String input) {
		try{
			Stack<String> myStack = new Stack<>();
			String output = "";
			//Pushing operation into Stack
			if(input.startsWith("ADD")){
				myStack.push("ADD");
			}
			else if(input.startsWith("SUB")){
				myStack.push("SUB");
			}
			else if(input.startsWith("MULT")){
				myStack.push("MULT");
			}
			else if(input.startsWith("DIV")){
				myStack.push("DIV");
			}
			else if(input.startsWith("LET")){
				myStack.push("LET");
			}

			if(input.startsWith("LET")){
				//Let expression will always have 3 main components
				List <String> letParameters = new ArrayList<String>();
				String outputExp2 = "";
				String outputExp3 = "";
				//Extracting the parameters of the LET expression
				letParameters = extractLetParameters(input);
				//Getting the second expression
				if(!Character.isDigit(letParameters.get(1).charAt(0))){
					outputExp2 = decideOperation(letParameters.get(1));
				}
				else{
					outputExp2 = letParameters.get(1);
				}

				//Getting the 3rd Expression
				if(!Character.isDigit(letParameters.get(2).charAt(0))){
					if(Character.isDigit(outputExp2.charAt(0))){
						String exp3 = letParameters.get(2);
						if(exp3.contains(letParameters.get(0) + ",")){
							exp3 = exp3.replace(letParameters.get(0) + ",", outputExp2 + ",");	
						}

						if(exp3.contains(letParameters.get(0) + ")")){
							exp3 = exp3.replace(letParameters.get(0) + ")", outputExp2 + ")");	
						}

						letParameters.remove(2);
						letParameters.add(2, exp3);
					}
					outputExp3 = decideOperation(letParameters.get(2));
				}
				else{
					outputExp3 = letParameters.get(2);
				}
				output = outputExp3;
			}
			else {
				output = computeExpression(input, myStack);
				//System.out.println("output = " + output);	
			}
			return output;
		}
		catch(Exception e){
			throw e;
		}
	}

	/*
	 * Determine if the expression has equal number of parenthesis
	 * @Params : String str - Expression to evaluate
	*/
	public static boolean validateExpression(String str){
		//Assuming last character will always be ')'
		Stack<String> parenthesis = new Stack<String>();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '(') {
				parenthesis.push("X"); 
			} 
			else if (str.charAt(i) == ')') {
				if(!parenthesis.isEmpty())
				parenthesis.pop();
			}		    
		}
		return parenthesis.isEmpty();
	}

	/*
	 * Extracting the sub parameters
	 * @Params : String input - Expression to be parsed
	 */
	public static List<String> extractLetParameters(String input) {
		try{
			List<String> parameters = new ArrayList<String>();		

			//Dividing the expression into 3 parts
			String exp1 = input.substring(input.indexOf("(") + 1, input.indexOf(","));

			int exp2StartPos = input.indexOf(",")+1;
			int exp2EndPos;
			String exp2;

			if(Character.isDigit(input.substring(input.indexOf(",")+1).charAt(0))){
				exp2 = input.substring(input.indexOf(",")+1, input.indexOf(",", exp2StartPos));
				exp2EndPos = input.indexOf(",", exp2StartPos);
			}
			else
			{
				exp2EndPos = getPosition(input, exp2StartPos);			
				exp2 = input.substring(input.indexOf(",")+1, exp2EndPos);			
			}

			String exp3 = input.substring(exp2EndPos + 1, input.lastIndexOf(")"));

			parameters.add(exp1);
			parameters.add(exp2);
			parameters.add(exp3);
			logger.debug("Expression 1 : " + exp1);
			logger.debug("Expression 2 : " + exp2);
			logger.debug("Expression 3 : " + exp3);
			return parameters;
		}
		catch(Exception e){
			throw e;
		}
	}

	/*
	 * Determining the sub-expression using braces '(' & ')'
	 * @Params input - The sub expression
	 * @Params startPos - Starting position of the 2nd sub position
	 */
	private static int getPosition(String input, int startPos) {
		try{
			String subsetString = input.substring(startPos);
			int braceCount = 0;
			int position = 0;
			for(int i = 0; i< subsetString.length();i++){
				if(subsetString.charAt(i) == '(')
					braceCount++;
				else if(subsetString.charAt(i) == ')')
				{
					braceCount--;
					if(braceCount == 0){
						position = startPos + i + 1;
						break;
					}
				}			
			}
			return position;
		}
		catch(Exception e){
			throw e;
		}
	}

	/* 
	 * Evaluate the expression
	 * @Params input - The expression which will be evaluated
	 * @Params myStack - The stack with the operation and operands
	 */
	public static String computeExpression(String input, Stack<String> myStack) {
		String error = "";
		try{
			logger.debug("Stack : " + myStack.toString());
			int value = 0;
			input = input.substring(input.indexOf("("));
			for(int i = 0; i < input.length(); i++){
				//For every closing bracket, the expression will be evaluated.
				if(input.charAt(i) == ')'){
					//Pop second number
					String s2 = myStack.pop();
					//Pop comma
					myStack.pop();
					//Pop first number
					String s1 = myStack.pop();
					//Pop opening brace
					myStack.pop();

					//Pop operator {ADD,MULT,DIV,SUB}
					if(myStack.isEmpty()){
						invalidExp = true;
						logger.debug("Invalid Expression");
					}
					else {
						String operator = myStack.pop();
						error = s1 + " "+ operator +" " + s2;
						switch (operator){
						case "ADD":
							//Add the numbers
							value = Integer.parseInt(s1) + Integer.parseInt(s2);
							dResult = Double.valueOf((Integer.parseInt(s1)) + Double.valueOf(Integer.parseInt(s2)));
							break;
						case "SUB":
							//Subtract the numbers
							value = Integer.parseInt(s1) - Integer.parseInt(s2);
							dResult = Double.valueOf((Integer.parseInt(s1)) - Double.valueOf(Integer.parseInt(s2)));
							break;
						case "DIV":
							//Divide the numbers
							value = Integer.parseInt(s1) / Integer.parseInt(s2);
							dResult = Double.valueOf((Integer.parseInt(s1)) / Double.valueOf(Integer.parseInt(s2)));
							break;
						case "MULT":
							//Multiply the numbers
							value = Integer.parseInt(s1) * Integer.parseInt(s2);
							dResult = Double.valueOf((Integer.parseInt(s1)) * Double.valueOf(Integer.parseInt(s2)));
							break;
						}

						logger.debug("Intermediate Values : " + value);
						String newValue = String.valueOf(value);				
						// push the result back to the stack
						myStack.push(newValue);
					}
				}
				else
				{
					//Adding the Operation characters from the input
					if(input.charAt(i) == 'A' && input.charAt(i+1) == 'D' && input.charAt(i+2) == 'D')
					{
						myStack.push("ADD");
						i = i+2;
					}
					else if(input.charAt(i) == 'S' && input.charAt(i+1) == 'U' && input.charAt(i+2) == 'B')
					{
						myStack.push("SUB");
						i = i+2;
					}
					else if(input.charAt(i) == 'D' && input.charAt(i+1) == 'I' && input.charAt(i+2) == 'V')
					{
						myStack.push("DIV");
						i = i+2;
					}
					else if(input.charAt(i) == 'M' && input.charAt(i+1) == 'U' && input.charAt(i+2) == 'L' && input.charAt(i+3) == 'T')
					{
						myStack.push("MULT");
						i = i+3;
					}
					//Also taking care of negative numbers
					else if(Character.isDigit(input.charAt(i)) || 
							(input.charAt(i) == '-' && Character.isDigit(input.charAt(i+1))))
					{
						// if this is the second number
						String s = "";
						if(input.charAt(i-1) == ','){
							s = input.substring(input.indexOf(",", i-1)+1, input.indexOf(")",i+1));						
						}
						else{
							s = input.substring(i, input.indexOf(",", i));
						}
						myStack.push(s);
						i = i + s.length() -1;
					}
					else {
						myStack.push(String.valueOf(input.charAt(i)));
					}
				}
			}
			return myStack.isEmpty()? "0" : myStack.peek();
		}
		catch(NumberFormatException nfe){
			logger.error("Number Format Exceltion in : "+ error);
			throw nfe;
		}
		catch(Exception e){
			throw e;
		}
	}
}
