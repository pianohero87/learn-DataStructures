//Derrick Wong
//CS 41: Data Structures
//19 December 2015
//Assignment 4
package borginterpreter;

import java.io.*;
import java.util.Scanner;

public class BorgInterpreter {

    public static class Borg {

        public static final int SIZE = 27; // default size of hash is 27   
        public Scanner scan;
        HashTable current;

        //object class to pass in to our hash functions
        public class Object {

            String name;
            int value;

            Object(String name, int value) {
                this.name = name;
                this.value = value;
            }

            Object(String name) {
                this.name = name;
            }

            Object(int value) {
                this.value = value;
            }
        }

        public boolean isOperator(String operator) {
            switch (operator) {
                case "+":
                    return true;
                case "-":
                    return true;
                case "*":
                    return true;
                case "/":
                    return true;
                case "%":
                    return true;
                case "^":
                    return true;
                default:
                    return false;
            }
        }

        public int operate(String operator, int operand1, int operand2) {
            switch (operator) {
                case "+":
                    return operand1 + operand2;
                case "-":
                    return operand1 - operand2;
                case "*":
                    return operand1 * operand2;
                case "/":
                    return operand1 / operand2;
                case "%":
                    return operand1 % operand2;
                case "^":                                      
                    return (int)Math.pow(operand1, operand2);
                default:
                    return operand1;
            }

        }

        public boolean isUnary(String unary) {
            switch (unary) {
                case "++":
                    return true;
                case "--":
                    return true;
                default:
                    return false;

            }
        }

        public int unaryOperate(String operator, int operand) {
            switch (operator) {
                case "++":
                    return operand + 1;
                case "--":
                    return operand - 1;
                default:
                    return operand;
            }
        }

        public void openFile() // opens file
        {
            try {
                File file = new File("borg.txt");
                scan = new Scanner(file);
            } catch (Exception e) {
                throw new RuntimeException("FILE NOT FOUND");
            }
        }

        public void readFile() // reads the file and interprets the data
        {
            int lineNumber = 1;

            while (scan.hasNextLine()) {                                
                String line = scan.nextLine();
                Scanner scanLine = new Scanner(line);

                if (line.equals("")) {
                    lineNumber++;                    
                } else {
                    String token = scanLine.next();
                    if (token.equals("COM")) // comment
                    {
                        lineNumber++;                        
                    } else if (token.equals("START")) // maintaining the stack with START AND FINISH
                    {                        
                        lineNumber++;                        
                        HashTable previous = current;
                        current = new HashTable();
                        current.next = previous;
                    } else if (token.equals("FINISH")) {
                        lineNumber++;                        
                        current = current.next;
                    } else if (token.equals("VAR")) {
                        lineNumber++;                      
                        String variable = scanLine.next(); // this is the variable name.                
                        int value = 0; // initialize variable's value to 0

                        if (scanLine.next().equals("=")) // assigning variables and adding operations
                        {
                            while (scanLine.hasNext()) { // keep on reading the line until no more tokens
                                if (scanLine.hasNextInt()) // if the token is an int
                                {
                                    value = scanLine.nextInt();
                                } else // if the token is a String
                                {
                                    // POSSIBLE CONDITIONS:
                                    // operator
                                    // a variable name
                                    String str = scanLine.next();
                                    if (isOperator(str)) // operator
                                    {
                                        if (scanLine.hasNextInt()) // if token after operator is an int
                                        {
                                            int operand = scanLine.nextInt();
                                            value = operate(str, value, operand); // call operate function
                                        } else // if token after operator is a variable name
                                        {
                                            value = operate(str, value, current.getValue(str, current));

                                        }
                                    } else // variable name (not a int nor an operator) 
                                    {
                                        value = current.getValue(str, current);
                                    }
                                }

                            }
                        }                        
                        Object o = new Object(variable, value);
                        current.add(o);
                    } // conditions for ++ and -- int x = ++i; int x = i++; x++, ++x etc
                    else if (token.equals("PRINT")) {
                        lineNumber++;
                        boolean print = true;                       
                        int value = 0;
                        String variable = "";

                        while (scanLine.hasNext()) {
                            if (scanLine.hasNextInt()) {
                                value += scanLine.nextInt();
                            } else {
                                String str = scanLine.next();
                                if (isUnary(str)) {
                                    if (scanLine.hasNextInt()) {
                                        int operand = scanLine.nextInt();
                                        value = unaryOperate(str, operand);
                                    } else if (scanLine.hasNext()) {
                                        variable = scanLine.next();
                                        int operand = current.getValue(variable, current);
                                        value = unaryOperate(str, operand);
                                    } else // if the unary proceeds the value (boramir++)
                                    {
                                        value = unaryOperate(str, value);
                                    }
                                } else if (isOperator(str)) {
                                    if (scanLine.hasNextInt()) {
                                        int operand = scanLine.nextInt();
                                        value = operate(str, value, operand);
                                    } else// then it is a variable
                                    {
                                        variable = scanLine.next();
                                        if (current.isExist(variable, current)) {
                                            int operand = current.getValue(variable, current);
                                            value = operate(str, value, operand);
                                        } else {
                                            System.out.println("LINE " + (lineNumber - 1) + ": ERROR. " + variable + " NOT DEFINED.");
                                            print = false;
                                        }

                                    }
                                } else {
                                    variable = str;                                                                        
                                    if (current.isExist(variable, current)) {
                                        value = current.getValue(str, current);                                                                                
                                    } else {
                                        System.out.println("LINE " + (lineNumber - 1) + ": ERROR. " + variable + " NOT DEFINED.");
                                        print = false;
                                    }
                                }
                            }
                        }
                        if (print == true) {
                            System.out.println(variable + " is " + value);
                        }

                    } else // if stand-alone variable is called on the line
                    {
                        lineNumber++;                        
                        int value = 0;
                        String variable = "";
                        String str = token;   // could be ++ if (++ boramir)                                                
                        while (scanLine.hasNext()) {
                            String modifier = scanLine.next();
                            if (modifier.equals("=")) {                                
                                while (scanLine.hasNext()) {
                                    if (scanLine.hasNextInt()) // if the token is an int
                                    {
                                        value = scanLine.nextInt();
                                    } else 
                                    {
                                        String str2 = scanLine.next();                                        
                                        if (isOperator(str2)) 
                                        {
                                            if (scanLine.hasNextInt())
                                            {
                                                int operand = scanLine.nextInt();
                                                value = operate(str2, value, operand); 
                                            } else
                                            {
                                                String str3 = scanLine.next();                                                
                                                value = operate(str2, value, current.getValue(str3, current));                                                
                                            }
                                        } else 
                                        {
                                            value = current.getValue(str2, current);
                                        }
                                    }
                                    variable = str;
                                }
                            } else if (isUnary(modifier)) {
                                if (scanLine.hasNext()) {
                                    variable = scanLine.next();
                                    value = unaryOperate(str, current.getValue(variable, current));
                                } else 
                                {
                                    variable = token;
                                    value = unaryOperate(modifier, current.getValue(variable, current));
                                }
                            } else
                            {
                                variable = str;
                                value = current.getValue(str, current);
                            }

                        }
                        current.modify(variable, value, current);
                    }
                }
            }

        }

    }

    public static void main(String[] args) {       
        Borg borg = new Borg();
        borg.openFile();
        borg.readFile();

        
        

    }
}
