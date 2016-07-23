/*Abigail Walker
 * EECS 233
 * Programming Project #3*/

import java.util.*;

/*This class implements a hash table to store words from a file (Strings) and their corresponding frequencies (ints).
 * Collisions in the hash table are handled with separate chaining.
 * Each instance of the table is initialized with a chosen size.  The table is determined to be "full" when the average list size is greater than
 * a certain maximum list size, set to be 5.  Choosing a maximum list size of 5 helps to preserve the O(c) run time for search and insert (c is a constant)
 * while also keeping the number of rehash operations to a reasonable number, since those are costly.
 * The table doubles in size if the maximum allowed list size is reached. */
public class FrequencyTable {
  
  //Global Variables
  
  //The size of the hash table array
  private int tableSize;
  
  /*The array representing the hash table.  The table is made up of individual lists of collided words (formed by separate chaining)
   * (see inner class at bottom of file)*/
  private CollisionList[] table;
  
  //The total number of Entries in the table
  private int numItems;
  
  //The maximum allowed average list size
  private int maxListSize = 5;
  
  //Constructor: tableSize is determined upon creation
  public FrequencyTable(int tableSize) {
    this.tableSize = tableSize;
    table = new CollisionList[tableSize];
  }
  
  /*Inserts an Entry into the hash table (see inner class Entry at bottom of file).
   * First, the method checks to see whether the table is full.  This is determined by checking whether the average length of Collision Lists
   * (i.e. the Load Factor) is greater than maxListSize.  Then, the method inserts the new word.
   * Finally, the method increments the number of items in the list. */
  public void insert(String word, int frequency) {
   if (averageListLength() > maxListSize)
     rehash();
   
   //Get table index using hashing function
   int i =  Math.abs(word.hashCode() % tableSize);
   //If there are no Entries yet at this index, create a new Collision List there with this new Entry as the front
   if (table[i] == null) {
     table[i] = new CollisionList(new Entry(word, frequency), 1);
   }
   //Otherwise, add this Entry to the Collision list
   else
     table[i].add(new Entry(word, frequency));
   numItems = numItems + 1;
  }
  
  /*Increments the frequency of an existing word in the hash table.
   * Throws NoSuchElementException if the element is not found in the table*/
  public void incrementFrequency(String word) throws NoSuchElementException {
    Entry e = findEntry(word);
    if (e != null)
      e.frequency = e.frequency + 1;
    else
      throw new NoSuchElementException();
  }
  
  /*Searches for a word in the hash table.
   * If found, the method returns the frequency of the word (> 0).  If not found, the method returns a frequency of 0.*/
  public int search(String word) {
    Entry e = findEntry(word);
    if (e != null)
      return e.frequency;
    else
      return 0;
  }
  
  /*Helper method to locate an Entry in the hash table
   * Input argument is the word we are looking for. Returns the corresponding table Entry, or null if the Entry is not found. */
  private Entry findEntry(String word) {
    //Get index of Entry by calculating hash function
    int i =  Math.abs(word.hashCode() % tableSize);
    //Loop through the Collision List at that index until we find the Entry we want
    CollisionList cl = table[i];
    if (table[i] != null) {
      Iterator<Entry> it = cl.iterator();
      while (it.hasNext()) {
        Entry e = it.next();
        if (e.key.equals(word))
          return e;
      }
    }
    //If table[i] is empty, or we go through the entire collision list at table[i] and don't find the word, return null
    return null;
  }
  
  /*Method for rehashing the table.  The method creates a new table array that is double the size of the original, and inserts each entry from the original
   * into the new table*/
  public void rehash() {
    int oldSize = tableSize;
    CollisionList[] oldTable = table;
    tableSize = 2*oldSize;
    table = new CollisionList[tableSize];
    //Set number of items to zero, since this will be incremented each time we insert one of the existing values into the new table
    numItems = 0;
    //Loop through each list at each index of the old table, and insert all existing elements into the new table.
    for (int i = 0; i < oldSize; i++) {
      if (oldTable[i] != null) {
        CollisionList cl = oldTable[i];
        for (Entry e: cl) {
          insert(e.key, e.frequency);
        }
      }  
    }
  }
  
  //Gets an Enumeration of all elements in the table (See inner class at bottom)
  public Enumeration<String> elements() {
    return new TableEnumeration(table);
  }
  
  //Gets the current size of the table
  public int size() {
    return tableSize;
  }
  
  //Gets the current number of items in the table
  public int numWords() {
    return numItems;
  }
  
  //Loops through the table to calculate the average Collision List length
  public int averageListLength() {
    return numItems/tableSize;
  }  
  
  /*This is an inner class that represents an entry in the table.  Each Entry has associated with it a word, a frequency (number of times
  * the word appears), and a "next" Entry, i.e. the reference to the next Entry in the list formed by separate chaining, when collisions in the table occur*/
  private class Entry {
    //The word stored in this table Entry
    private String key;
    
    //The word's frequency
    private int frequency;
    
    //The next Entry in the list formed by separate chaining
    private Entry next;
    
    
    //Constructor
    private Entry(String key, int frequency) {
      this.key = key;
      this.frequency = frequency;
    }
    
    /*Determines whether this Entry is at the end of the Collision List it is part of
     * Returns true if this Entry has a next Entry; false if not*/
    private boolean atEndOfList() {
      return (next == null);
    }
    
  }
  
  /*An inner class to represent a list of table entries formed by separate chaining, when collisions in the table occur.
   * Each element of the table consists of a CollisionList (or is null if empty).*/
  private class CollisionList implements Iterable<Entry> {
    
    //The first Entry in this list
    private Entry front;
    
    //The number of items in this list
    private int numItems;
    
    //Constructor
    private CollisionList(Entry front, int numItems)  {
      this.front = front;
      this.numItems = numItems;
    }
    
    //Adds an Entry to the front of this list.  Also updates front and increments numItems.
    private void add(Entry newEntry) {
      Entry oldFront = front;
      this.front = newEntry;
      newEntry.next = oldFront;
      numItems = numItems + 1;
    }
    
    //Gets the Entry at the front of the list
    private Entry getFront() {
      return front;
    }
    
    //Returns an Iterator for the list
    public Iterator<Entry> iterator() {
      return new Iterator<Entry>() {
        //Pointer variable to keep track of the current Entry in the list iteration.  Starts at the front of the list
        private Entry ptr = front;
        
        /*Determines whether there are any Entries left in this iteration
         * Returns true if there are Entries remaining; false if we have reached the end of the list*/
        public boolean hasNext() {
          return (ptr != null);
        }
        
        /*Returns the next Entry in this list iteration, and advances the pointer variable.*/
        public Entry next() {
          Entry e = ptr;
          ptr = ptr.next;
          return e;
        }
      };
    }
  }
  
  //Inner Classes
  
  /*An inner class that represents an Enumeration of the entire hash table.
   * Can be used to loop through each Entry in the table*/
  private class TableEnumeration implements Enumeration<String> {
    
    //Keeps track of the current index of the table array
    private int index;
    
    //Pointer variable to keep track of the current Entry in the table
    private Entry ptr;
    
    //Constructor
    private TableEnumeration(CollisionList[] table) {
      index = 0;
      /*Loop through the indices of the hash table array, starting at zero, 
       * until we reach either the first occupied index, or the last element of the table array.*/
        while (table[index] == null && index < tableSize-1) {
          index = index + 1;
        }
        /*Get the contents of the current table index.  If it is not null, start the pointer variable at the front of the list of Entries there.
         * If it is null, then that means we reached the end of the table without finding any Entries (so set the pointer to null)*/
        CollisionList cl = table[index];
        if (cl != null)
          ptr = cl.getFront();
        else
          ptr = null; 
      }
      
     /*Determines whether there are any more Entries in this Enumeration of the table.
      * Returns true if there are Entries remaining (i.e. pointer variable is not null); false if not*/
      public boolean hasMoreElements() {
        return (ptr != null);
      }
      
      /*Gets the next Entry in this Enumeration, and advances the pointer variable to the next available Entry*/
      public String nextElement() {
        //Get the word of the Entry to return
        String next = ptr.key;
        //If the current Entry is the end of its Collision list, determine whether there are more occurpied indices of the table
        if (ptr.atEndOfList()) {
          index = index + 1;
          //Set ptr to null, to starrt
          ptr = null;
          //If we have not yet reached the end of the table, check whether there are any occupied spaces remaining
          if (index < tableSize-1) {
            //Loop through the table until we reach an occupied slot, or reach the end
            while (table[index] == null && index < tableSize-1) {
              index = index + 1;
            }
            /*Get the contents of the current (or last) table index.  If it is not null, start the pointer variable at the front of the list of Entries there.
             * If it is null, then that means we reached the end of the table without finding any Entries (so keep ptr set to null)*/
            CollisionList cl = table[index];
            if (cl != null)
              ptr = cl.getFront();
          }
        }
        //If the current Entry is not at the end of its Collision List, just advance the pointer
        else
          ptr = ptr.next;
        return next;
      }
   }
 
  }