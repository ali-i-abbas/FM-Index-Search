import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class FMIndexSearchExperiments {

   public static void main(String[] args) throws IOException {

      FileWriter statsOutput = new FileWriter("stats.csv");
      statsOutput.write("fileName,m,n,it,st,s\n");

      Random random = new Random();
   
      String[] fileNames = new String[]{"bible.txt", "E.coli", "random.txt"};

      for (String fileName : fileNames) {

         char[] fullText = loadFile(fileName, true);         

         for (int m = 200000; m <= 4000000; m+= 200000) {
            
            char[] text = new char[m];
            for (int i = 0; i < m - 1; i++) {
               text[i] = fullText[i];
            }
            text[m - 1] = '\0';

            long indexStartTime = System.nanoTime();
         
            FMIndex.space = 0;
            FMIndex fmIndex = new FMIndex(text);
      
            long indexEndTime = System.nanoTime(); 

            double indexTimeInSeconds = (indexEndTime - indexStartTime) / 1e9;

            for (int n = 2; n < m; n = (n == 2) ? 200000 : n + 200000) {

               char[] P = new char[n];
               int patternOffset = random.nextInt(m - n + 1);
               for (int i = 0; i < n; i++) {
                  P[i] = fullText[i + patternOffset];
               }
               
               long searchStartTime = System.nanoTime();

               SearchInterval searchInterval = fmIndex.backwardSearch(P);
         
               long searchEndTime = System.nanoTime(); 

               double searchTimeInSeconds = (searchEndTime - searchStartTime) / 1e9;

               statsOutput.write(fileName + "," + m + "," + n + "," + indexTimeInSeconds + "," + searchTimeInSeconds + "," + FMIndex.space + "\n");
         
            }

         }
      }

      

      statsOutput.close();
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


