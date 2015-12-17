//Derrick Wong
//CS 41: Data Structures
//28 November 2015
//Assignment 4
package borginterpreter;

import java.io.*;
import java.util.*;
import java.util.Scanner;

public class BorgInterpreter {

    public static class Borg {

        public static final int SIZE = 27; // default size of hash is 27   
        public Scanner scan;
        HashTable current;

        public static class HashTable {

            Node array[] = new Node[SIZE];
            public HashTable next;

            private class Node {

                Node next = null;
                String varName = "";
                int var = -1;

                Node(String varName, int var) {
                    this.varName = varName;
                    this.var = var;
                    next = null;
                }
            }

            public int hash(String variableName) {
                char[] convert = variableName.toCharArray();
                int ordinalSum = 0;

                for (int i = 0; i < convert.length; i++) {
                    ordinalSum += (i + 1) * ((int) convert[i]); // type cast char to int
                }
                return ordinalSum % SIZE;
            }

            public void add(Object o) {
                int i = hash(o.name);                
                Node newNode = new Node(o.name, o.value); // linked list probing
                if (array[i] != null) {
                    array[i].next = newNode;
                } else {
                    array[i] = newNode;
                }
            }

            public int getValue(String variableName, HashTable current) {
                int i = hash(variableName);
                HashTable currentHash = current;
                Node front = array[i];
                while (currentHash != null) // hash
                {
                    while (front != null) // list
                    {
                        if (front.varName.equals(variableName)) {
                            if (front.var == -1) {
                                // variable was not initialized                  
                                return 0;
                            } else {
                                return front.var;
                            }
                        } else {
                            front = front.next;
                        }
                    }

                    currentHash = currentHash.next;
                }
                return 0; // if key not found
            }

            public String getName(String variableName, HashTable current) {
                int i = hash(variableName);
                HashTable currentHash = current;
                Node front = array[i];
                while (current != null) // hash
                {
                    while (front != null) // list
                    {
                        if (front.varName.equals(variableName)) {

                            return front.varName;
                        } else {
                            front = front.next;
                        }
                    }

                    current = current.next;
                }
                return ""; // if key not found
            }

            public boolean isExist(String variableName, HashTable current) {
                int i = hash(variableName);
                HashTable currentHash = current;
                Node front = array[i];
                while (currentHash != null) {
                    while (front != null) {
                        if (front.varName.equals(variableName)) {
                            return true;
                        } else {
                            current = current.next;
                        }
                    }
                    currentHash = currentHash.next;
                }
                return false;
            }

            public void modify(String variableName, int var, HashTable current) {                
                int i = hash(variableName);
                HashTable currentHash = current;
                Node front = array[i];
                while (currentHash != null) {
                    //System.out.println("enter the while loop1");
                    while (front != null) {
                        //System.out.println("enter the while loop2");
                        if (front.varName.equals(variableName)) {
                            front.var = var;
                            //System.out.println("modified the value: " + array[i].var);                            
                            break;
                        } else {
                            front = front.next;
                        }
                    }
                    currentHash = currentHash.next;
                    //System.out.println("current is now null");
                }
            }
        }

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
                System.out.println("FILE NOT FOUND");
            }
        }

        public void readFile() // reads the file and interprets the data
        {
            int lineNumber = 1;

            while (scan.hasNextLine()) {
                System.out.print(lineNumber + "\t");
                //parse each line
                String line = scan.nextLine();
                Scanner scanLine = new Scanner(line);

                if (line.equals("")) {
                    lineNumber++;
                    System.out.println("empty");
                } else {
                    String token = scanLine.next();
                    if (token.equals("COM")) // comment
                    {
                        lineNumber++;
                        System.out.println("comment");
                    } else if (token.equals("START")) // maintaining the stack with START AND FINISH
                    {
                        //create new hash
                        lineNumber++;
                        System.out.println("Start");
                        HashTable previous = current;
                        current = new HashTable();
                        current.next = previous;
                    } else if (token.equals("FINISH")) {
                        lineNumber++;
                        System.out.println("Finish");
                        current = current.next;
                    } else if (token.equals("VAR")) {
                        lineNumber++;
                        System.out.println("Var");
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
                        //System.out.print(variable + " " + value);
                        Object o = new Object(variable, value);
                        current.add(o);
                    } // conditions for ++ and -- int x = ++i; int x = i++; x++, ++x etc
                    else if (token.equals("PRINT")) {
                        lineNumber++;
                        boolean print = true;
                        System.out.println("Print");
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
                                    //System.out.println(variable);                                    
                                    System.out.println(current.isExist(variable, current));
                                    if (current.isExist(variable, current)) {
                                        value = current.getValue(str, current);
                                        //System.out.println("derp");
                                        System.out.println(value);

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
                        System.out.println("Lonely");
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
                                    } else // if the token is a String
                                    {
                                        // POSSIBLE CONDITIONS:
                                        // operator
                                        // a variable name
                                        //String str2 = scanLine.next();
                                        if (isOperator(modifier)) // operator
                                        {
                                            if (scanLine.hasNextInt()) // if token after operator is an int
                                            {
                                                int operand = scanLine.nextInt();
                                                value = operate(modifier, value, operand); // call operate function
                                            } else // if token after operator is a variable name
                                            {
                                                value = operate(modifier, value, current.getValue(modifier, current));
                                            }
                                        } else // variable name (not a int nor an operator) 
                                        {
                                            value = current.getValue(str, current);
                                        }
                                    }
                                }
                            } else if (isUnary(modifier)) {
                                if (scanLine.hasNext()) {
                                    variable = scanLine.next();
                                    value = unaryOperate(str, current.getValue(variable, current));
                                } else // boramir ++
                                {
                                    variable = token;
                                    value = unaryOperate(modifier, current.getValue(variable, current));
                                    //System.out.println(value + "\thi");
                                }
                            } else // token is a variabe
                            {
                                variable = str;
                                value = current.getValue(str, current);
                            }

                        }
                        //System.out.println(value + "\tbye");
                        current.modify(variable, value, current);
                        //System.out.println(current.getName(variable, current));
                        // if these two pass, it means that the modify function is not working properly
                        //System.out.println("hi" + current.getValue(variable, current));
                    }
                }
            }

        }

    }

    public static void main(String[] args) {
        // open file
        // parse each line
        // scanner each token
        Borg borg = new Borg();
        borg.openFile();
        borg.readFile();
        /* 
         Scanner scanner = new Scanner(System.in);
        
        
         String line = scanner.next();
         Scanner scanner2 = new Scanner(line);
         if(scanner2.next().equals("hi"))
         {
         System.out.println("ok");
         }
         */
    }
}
