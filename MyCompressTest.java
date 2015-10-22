import java.io.*;
// import java.util.zip.StringZipInputStream;
// import java.util.zip.StringZipOutputStream;

public class MyCompressTest {
    final int MAX = 1024;
    	String inputFileName 	= "words.txt";
//    	String inputFileName 	= "words.txt";
//    	String inputFileName 	= "words.txt";
//    String inputFileName = "lwords.txt";
//    String inputFileName 	= "wrds.txt";
    String outputFileName = "words.compress";
    String uncompressed = "wordsUncompress.txt";

    public static void main(String args[]) {
        MyCompressTest aMyCompressTest = new MyCompressTest();
        aMyCompressTest.compress();
        aMyCompressTest.unCompress();
        System.out.println("\nrun command to check result: diff " + aMyCompressTest.inputFileName + " " +
                aMyCompressTest.uncompressed);

    }

    void compress() {
        try {
            String aWord;

            BufferedReader input = new BufferedReader(new FileReader(inputFileName));
            StringZipOutputStream aStringZipOutputStream = new StringZipOutputStream(new FileOutputStream(outputFileName));
            int countLines = 0;
            while ((aWord = input.readLine()) != null) {
                if (countLines++ < 10)
                    System.out.println("write:	" + aWord);
                aStringZipOutputStream.write(aWord);
            }
            aStringZipOutputStream.close();
            input.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    void unCompress() {
        try {
            String aWord;
            byte[] buffer = new byte[MAX];

            BufferedWriter uncompress = new BufferedWriter(new FileWriter(uncompressed));
            StringZipInputStream aStringZipInputStream = new StringZipInputStream(new FileInputStream(outputFileName));
            String theWord;

            while ((theWord = aStringZipInputStream.read()) != null) {
                uncompress.write(theWord, 0, theWord.length());
                //System.out.print(theWord);
            }
            aStringZipInputStream.close();
            uncompress.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}