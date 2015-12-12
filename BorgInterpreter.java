
package borginterpreter;

import java.io.*;
import java.util.*;
import java.util.Arrays;
import java.util.Scanner;

public class BorgInterpreter {

    public static final int SIZE = 27; // default size of hash is 27
    HashTable current;
    
    public static class HashTable 
    {

        Node array[] = new Node[SIZE];        
        public HashTable next;        

        private class Node 
        {
            Node next = null;   
            String varName = "";
            int var = -1; 
            
            Node(String varName, int var)
            {
                this.varName = varName;
                this.var = var;
                next = null;
            }            
        }
        public int hash(Object o) // hash function 
        {
            char[] convert = o.name.toCharArray();
            int ordinalSum = 0;

            for (int i = 0; i < convert.length; i++) 
            {
                ordinalSum += (i + 1) * (int) convert[i]; // type cast char to int
            }             
            return ordinalSum % SIZE;            
        }
        
        public void add(Object o)
        {
            int i = hash(o);
            
            if(array[i].var != -1 || array[i].varName != null)  
            {
                Node newNode = new Node(o.name, o.value); // linked list probing
                array[i].next = newNode;                
            }
            
            else
            {
                array[i].var = o.value;
                array[i].varName = o.name;
            }
        }
    }
    
    private Scanner scan;
    
    //object class to pass in to our hash functions
    public class Object 
    {
        String name;
        int value;
        
        Object(String name, int value)
        {
            this.name = name;
            this.value = value;
        }
        
        Object(String name)
        {
            this.name = name;
        }
        
        Object(int value)
        {
            this.value = value;
        }
    }
    /*
    public boolean isOperator(String operator)
    {
        switch(operator)
        {
            case "+":
                return true;
            case "-":
                return true;
            case "*":
                return true;
            case "/":
                return true;
            case "^":
                return true;
            default: 
                return false;
        }
    }
    */
    public void openFile() // oppens file
    {
        try {
            scan = new Scanner(new File("borg.txt"));
        } catch (Exception e) {
            System.out.println("FILE NOT FOUND");
        }
    }

    public void readFile() // reads the file and interprets the data
    {
        while (scan.hasNext()) 
        {
            //parse each line
            String line = scan.nextLine(); 
            Scanner scanLine = new Scanner(line);
            
            if(scanLine.next().equals("COM"))
            {
                // do nothing
            }
            
            else if(scanLine.next().equals("START")) // need another possible condition;
            {
                //create new hash                
                HashTable previous = current;
                current = new HashTable();
                current.next = previous;                                                
            }
            
            else if (scanLine.next().equals("FINISH"))
            {
                current = current.next;                 
            }
            
            else if (scanLine.next().equals("VAR"))
            {
                String variable = scanLine.next(); // this is the variable name.
                
                int value = 0;
                if(scanLine.next().equals("=")) // assigning variables and adding operations
                {
                   if(scanLine.hasNextInt())
                   {
                       value = scanLine.nextInt();
                   }
                   
                   // what if the next token is a String e.ge var x = a + 6;
                   // cant assume next token is an operator.
                   else if (scanLine.hasNext())  
                   {
                       String operator = scanLine.next();
                       int operand = 0;
                       
                       if(scanLine.hasNextInt())
                       {
                           operand = scan.nextInt();
                       }
                       
                       else
                       {
                           // traverse the hash tables to find the actual value
                           // instantiate operand
                           // search(o.name)
                       }
                       
                       // condition for ++ or --
                       
                       switch(operator)
                       {
                           case "+":
                               value  += operand;
                               break;
                           case "-":
                               value -= operand;
                               break;
                           case "*":
                               value *= operand;
                               break;
                           case "/":
                               value /= operand;
                               break;
                           case "%":
                               value %= operand;
                               break;
                       }
                   }
                
                }
              
                                
                
                Object o = new Object(variable, value);
                current.hash(o);
            }
            
            // conditions for ++ and -- int x = ++i; int x = i++; x++, ++x etc
            
            else if (scanLine.next().equals("PRINT"))
            {
                
            }

        }

    }

    // calls the hash function and allocates the addresses.
    // chaining collision resolution if address is already taking up space.
    // search function (remember to include the chainging resolution.
    public static void main(String[] args) 
    {
        // open file
        // parse each line
        // scanner each token
    }

}


