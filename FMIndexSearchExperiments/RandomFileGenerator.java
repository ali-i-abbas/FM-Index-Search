import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class RandomFileGenerator {
    public static void main(String[] args) throws IOException {
        FileWriter fileOutput = new FileWriter("random.txt");
        Random random = new Random();

        for (int i = 0; i < 4100000; i++) {
            fileOutput.write(random.nextInt(255) + 1);
        }

        fileOutput.close();
    }
    
}