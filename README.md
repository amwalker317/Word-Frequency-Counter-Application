# Word-Frequency-Counter-Application
This program essentially a word-frequency counter, such as those used by search engines when performing query searches.  What it does is process a text file (which could be a book, journal article, etc), and count the number of occurrences of each word in the file.  To do this, the program runs through the entire text file, and keeps track of each word and its frequency in a hash table.  Once the entire file is processed, the program prints each word, and its frequency of occurrence, to a new text file.  The console output also includes information on whether an error occurred during the process (or "OK;" if no error occurred), the total number of words in the file, the size of the final hash table, and the average length of collision lists in the hash table (collisions were handled with separate chaining).

The included files are: 
1) The source code (Main class WordCount and hash table class FrequencyTable)
2) A demonstration text file for the program to read ("SoTheyBakedACake.txt," an online book from Project Gutenberg)
3) The resulting output text for the demonstration file ("SoTheyBakedACakeFrequencies.txt")
4) A text file with the command to run the code and the resulting console output ("SoTheyBakedACake_ConsoleOutput.txt").  
