import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FMIndexSearch {

   public static void main(String[] args) throws IOException {

      char[] text = null;
      char[] P = null;
      
      try {
         text = loadFile(args[0], true);
         P = loadFile(args[1], false);
      } catch (IOException e) {
         e.printStackTrace();
      }

      long startTime = System.nanoTime();
      
      FMIndex fmIndex = new FMIndex(text);

      long indexCreationEndTime = System.nanoTime(); 

      
      long searchStartTime = System.nanoTime();

      SearchInterval searchInterval = fmIndex.backwardSearch(P);

      long endTime = System.nanoTime(); 
      
		FileWriter resultsOutput = new FileWriter("results.txt");

      
      System.out.println("\nText Size: " + text.length + "\nPattern Size: " + P.length + "\nAlphabet Size: " + fmIndex.sigma + "\nTotal Space Used in Bytes: " + FMIndex.space);
      resultsOutput.write("\nText Size: " + text.length + "\nPattern Size: " + P.length + "\nAlphabet Size: " + fmIndex.sigma + "\nTotal Space Used in Bytes: " + FMIndex.space);

      double indexCreationExecutionTimeInSeconds = (indexCreationEndTime - startTime) / 1e9;
      System.out.println("\nIndex Creation Execution Time In Seconds: " + indexCreationExecutionTimeInSeconds);
      resultsOutput.write("\nIndex Creation Execution Time In Seconds: " + indexCreationExecutionTimeInSeconds);
      

      double searchExecutionTimeInSeconds = (endTime - searchStartTime) / 1e9;
      System.out.println("\nSearch Execution Time In Seconds: " + searchExecutionTimeInSeconds);
      resultsOutput.write("\nSearch Execution Time In Seconds: " + searchExecutionTimeInSeconds);

      double executionTimeInSeconds = (endTime - startTime) / 1e9;
      System.out.println("\nTotal Execution Time In Seconds: " + executionTimeInSeconds);
      resultsOutput.write("\nTotal Execution Time In Seconds: " + executionTimeInSeconds);

      
      if (searchInterval == null) {
         System.out.println("\nPattern was not found in text.");
         resultsOutput.write("\n\nPattern was not found in text.");
      } else {
         
         int[] lineNumber = new int[text.length];
         int count = 1;
         for (int i = 0; i < text.length; i++) {
            lineNumber[i] = count;
            if (text[i] == '\n') {
               count++;
            }
         }

         int patternsCount = searchInterval.ep - searchInterval.sp + 1;
         System.out.println("\n" + patternsCount + " Pattern(s) was found.");
         resultsOutput.write("\n\n" + patternsCount + " Pattern(s) was found in the following range(s) [begin index, end index] (zero-based index):\n\n");

         int[] firstIndexValues = new int[patternsCount];
         int[] secondIndexValues = new int[patternsCount];
         Integer[] sortedIndex = new Integer[patternsCount];
         count = 0;
         for (int i = searchInterval.sp; i <= searchInterval.ep; i++) {
            firstIndexValues[count] = fmIndex.SA[i];
            secondIndexValues[count] = fmIndex.SA[i] + P.length - 1;
            sortedIndex[count] = count;
            count++;
         }

         IndexSorterInt is = new IndexSorterInt(firstIndexValues, sortedIndex);
         is.sort();

         int line = 0;
         int c = 0;
         int maxLineSize = 100;
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < patternsCount; i++) {
            //System.out.println((i + 1) + "\tLine: " + lineNumber[firstIndexValues[sortedIndex[i]]] + "\t[" + firstIndexValues[sortedIndex[i]] + ", " + secondIndexValues[sortedIndex[i]] + "]");
            resultsOutput.write((i + 1) + "\tLine: " + lineNumber[firstIndexValues[sortedIndex[i]]] + "\t[" + firstIndexValues[sortedIndex[i]] + ", " + secondIndexValues[sortedIndex[i]] + "]\t");

            
            count = maxLineSize;
            sb.append('[');
            c = firstIndexValues[sortedIndex[i]];
            line = lineNumber[c];
            while (count > 0 && c < text.length && lineNumber[c] == line) {
               sb.append(text[c]);

               if (c == secondIndexValues[sortedIndex[i]]) {                  
                  sb.append(']');
               }
               c++;
               count--;
            }

            c = firstIndexValues[sortedIndex[i]] - 1;
            line = lineNumber[c];
            while (count > 0 && c > 0 && lineNumber[c] == line) {
               sb.insert(0, text[c]);
               c--;
               count--;
            }

            sb.setCharAt(sb.length() - 1, '\n');
            
            resultsOutput.write(sb.toString());

            sb.setLength(0);
         }

      }

      resultsOutput.close();
   }

   // code is from: http://akini.mbnet.fi/java/java_utf8_xml/
   // modified to add terminal null character '\0'
   public static char[] loadFile(String file, boolean appendTerminalCharacter) throws IOException {
      // read text file, auto recognize bom marker or use 
      // system default if markers not found.
      BufferedReader reader = null;
      CharArrayWriter writer = null;
      UnicodeReader r = new UnicodeReader(new FileInputStream(file), null);
         
      char[] buffer = new char[16 * 1024];   // 16k buffer
      int read;
      try {
         reader = new BufferedReader(r);
         writer = new CharArrayWriter();
         while( (read = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, read);
         }
         if (appendTerminalCharacter) {
            writer.append('\0');
         }
         writer.flush();
         return writer.toCharArray();
      } catch (IOException ex) {
         throw ex;
      } finally {
         try {
            writer.close(); reader.close(); r.close();
         } catch (Exception ex) { }
      }
   }
  
}


