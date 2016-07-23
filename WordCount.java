/*Abigail Walker
 * EECS 233
 * Programming Project #3*/

import java.io.*;
import java.util.*;

/*Main class: controls the process of generating the hash table and output file with word frequencies.*/
public class WordCount {
  
  //Global Variables
  
  //A hash table to keep track of each word in the file along with its frequency
  private FrequencyTable wordFreqs;
  
  //The name of the input file that we are reading
  private String inputFileName;
  
  //The name of the output file, where we will write the results of the word frequency counts
  private String outputFileName;
  
  //Constructor
  public WordCount(String inputFileName, String outputFileName) {
    //Initial table size is 2^7, assuming the file is relatively large
   wordFreqs = new FrequencyTable(128);
   this.inputFileName = inputFileName;
   this.outputFileName = outputFileName;
  }
  
     /*This method calls each helper method in the correct order, to execute the steps necessary to count the word frequencies and output the results
   * The method returns a String, which will be one of the following:
   * 1. "Input error" : This indicates that there was an error in the initial processing of the input file in the inputData() method
   * 2. "Error writing to output file" : This indicates that there was an error in the printData() method
   * 3. "OK; Total Words: XXX, Hash Table Size: XXX, Average length of collision lists: XXX - This indicates that the operations were done successfully, 
   * and prints information on the final state of the hash table. */  
  public static String wordCount(String inputFileName, String outputFileName) {
    WordCount wc = new WordCount(inputFileName, outputFileName);
    String outcome;
    //Step 1: scan in the file and insert each word into the hash table.
    boolean inputSuccess = wc.inputData();
    //If the input process was successful, continue; if not, stop and return error message
    if (inputSuccess) {
      //Step 2: Loop through the hash table and print each word/frequency to the output file.
      boolean outputSuccess = wc.printData();
      //If the file output process was successful, continue; if not, stop and return error message
      if (outputSuccess) {
        //Step 3: Get information on the state of the hash table at the end of the program.
        int[] tableStats = wc.getTableStats();
        int numberOfWords = tableStats[0];
        int hashTableSize = tableStats[1];
        int averageCollisionListLength = tableStats[2];
        outcome = "OK; Total Words: " + numberOfWords + ", Hash Table Size: " + hashTableSize + ", Average length of collision lists: " + averageCollisionListLength;  
      }
      else
        outcome = "Error writing to output file";
    }
     else
        outcome = "Input Error";
     return outcome;
  }
  
  /*Step 1: scan in the file and insert each word into the hash table.
   * Returns a boolean: true if we get through the whole file successfully; false if an exception is raised along the way*/
  public boolean inputData() {
    try {
      BufferedReader in = getBufferedReader(inputFileName);
      //Read the file, one line at a time
      String line = in.readLine();
      while (line != null) {
        insertWords(line);
        line = in.readLine();
      }
      in.close();
      //Return true if we reach the end of the file successfully
      return true;
    }
    catch (Exception e) {
      //Return false if any exception was raised
      return false;
    }
  }
  
     /*Helper method: Returns a BufferedReader for reading the input file
   * Takes the name of the file as input*/
  private BufferedReader getBufferedReader(String inputFileName) throws IOException {
    return new BufferedReader(new FileReader(inputFileName));
  }
  
  /*A helper method to split each line of the file into individual words, and remove punctuation. 
   * The method then adds each word to the hash table.
   * Input argument is a full line from the input file. */
  private void insertWords(String toSplit) {
    //Split the line by white spaces first
    String[] split1 = toSplit.split("\\s");
    for (int i = 0; i < split1.length; i++) {
      //Split each result of split1 by punctuation characters.
      String[] split2 = split1[i].split("\\p{Punct}");
      //Add each result of split2 (of length > 0) into the hash table
      for (int j = 0; j < split2.length; j++) {
        if (split2[j].length() > 0) {
          addToTable(split2[j].toLowerCase());
        }
      }    
    }
  }

  /*A helper method to add a word into the hash table (or increment frequency, if the word already exists in the table).
   * Input argument is the word to insert*/
  private void addToTable(String word) {
    //First check to see whether the word is already in the table
    int frequency = wordFreqs.search(word);
    //If it is, increment frequency
    if (frequency > 0) {
      wordFreqs.incrementFrequency(word);
    }
    //If not, insert with a frequency of 1
    else {
      wordFreqs.insert(word, 1); 
    }
  }
  
  /*Step 2: Loop through the hash table and print each word/frequency to the output file.
   * Returns a boolean: true if we get through the whole hash table successfully; false if any exception is reaised along the way. */
  public boolean printData() {
    try {
      //Get an Enumeration to loop through all elements (keys) in the hash table
      Enumeration<String> elements = wordFreqs.elements();
      BufferedWriter out = getBufferedWriter();
      //For each element, get frequency and print to file
      while (elements.hasMoreElements()) {
        String key = elements.nextElement();
        int frequency = wordFreqs.search(key);
        String s = "(" + key + " " + frequency + ")";
        out.write(s, 0, s.length());
      }
      out.close();
      //Return true if we get through the whole table successfully
      return true;
    }
    //Return false if any exception was raised
    catch (IOException e) {
      return false;
    }
 }
  
     /*Helper method: Returns a BufferedWriter for writing to the output file
   * Takes the name of the file as input*/
  private BufferedWriter getBufferedWriter() throws IOException {
    return new BufferedWriter(new FileWriter(outputFileName));
  }
  
  /*Step 3: Get information on the state of the hash table at the end of the program.
   * Returns an integer array that contains 1) the number of words in the hash table; 2) final hash table size; 3) the average length of collision lists */
  public int[] getTableStats() {
    int[] results = new int[3];
    results[0] = wordFreqs.numWords();
    results[1] = wordFreqs.size();
    results[2] = wordFreqs.averageListLength();
    return results;
  }
    

  /*Main method.  Invokes the wordCount method to execute the process of outputting the word frequency data.
   * Input arguments should be:
   * 1) Name of input file
   * 2) Name of output file*/
  public static void main(String[] args) {
    String inputFileName = args[0];
    String outputFileName = args[1];
    String outcome = wordCount(inputFileName, outputFileName);
    System.out.println(outcome);
  }
}
