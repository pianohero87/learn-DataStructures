
package borginterpreter;
import static borginterpreter.BorgInterpreter.Borg.SIZE;
// Hashtable for borg interpreter
public class HashTable {

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

            public void add(BorgInterpreter.Borg.Object o) {
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
                
                while (currentHash != null) // hash
                {
                    Node front = currentHash.array[i];
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
                
                while (currentHash != null) // hash
                {
                    Node front = currentHash.array[i];
                    while (front != null) // list
                    {
                        if (front.varName.equals(variableName)) {

                            return front.varName;
                        } else {
                            front = front.next;
                        }
                    }

                    currentHash = currentHash.next;
                }
                return ""; // if key not found
            }

            public boolean isExist(String variableName, HashTable current) {
                int i = hash(variableName);
                HashTable currentHash = current;
                while (currentHash != null) {
                    Node front = currentHash.array[i];
                    while (front != null) {
                        if (front.varName.equals(variableName)) {
                            return true;
                        }
                    }
                currentHash = currentHash.next;
                
                }
                return false;
            }

            public void modify(String variableName, int var, HashTable current) {                
                int i = hash(variableName);
                HashTable currentHash = current;
                while (currentHash != null) {
                    Node front = currentHash.array[i];
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
