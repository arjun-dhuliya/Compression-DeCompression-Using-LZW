/***
 * author Arjun Dhuliya
 * author Nihar Vanjara
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/***
 * StringZipInputStream decompress an already compressed file using the LZW compression and decompression algorithm
 */
public class StringZipOutputStream {
    private static final int BYTE_SIZE = 3;
    private static HashMap<String, Integer> dictionary;
    int MAX_CHAR_COUNT =65536;                          //java char is 16 bits, so 2^16 = 65536
    private final String newLineChar = '\n'+"";
//    private final String newLineChar = System.getProperty("line.separator");
    private final OutputStream outputStream;
    //private final byte[] codedLineInBytes = new byte[4096];
    private final long timer = System.currentTimeMillis();
    private final HashMap<String, Integer> specialCharDictionary = new HashMap<>();
    private final ByteArrayOutputStream codedLineInBytes = new ByteArrayOutputStream(0);
    private byte[] threeBytesForInt;
    private String word = "";
    private int keyCount = 0;
    private int specialCharIndex = (int) (Math.pow(2.0, 8.0 * BYTE_SIZE)) - 1;


    /***
     * creates an object of StringZipOutputStream, with outputStream
     *
     * @param outputStream outputStream to write on file.
     */
    public StringZipOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        dictionary = initDictionary();
    }

    /***
     * initializes a new dictionary, and adds key,value with ascii characters for 0 to 255
     *
     * @return dictionary
     */
    private HashMap<String, Integer> initDictionary() {
        dictionary = new HashMap<>();
        for (keyCount = 0; keyCount < MAX_CHAR_COUNT; keyCount++) {
            dictionary.put((char) keyCount + "", keyCount);
        }
//  for (int i = 0; i < 256; i++) {
//            dictionary.put((char) i + "", i);
//        }
        return dictionary;
    }

    /***
     * returns a byte array of specified size in BYTE_SIZE
     *
     * @param value integer to be converted to Byte array
     * @return byte[] representation of the value (integer)
     */
    private byte[] intToByteArray(int value) {
        byte[] bytes = new byte[BYTE_SIZE];
        int multiplier = BYTE_SIZE - 1;
        for (int index = 0; index < BYTE_SIZE; index++) {
            bytes[index] = (byte) (value >>> (8 * multiplier--));
        }
        return bytes;
    }

    /***
     * Reads data into a string. the method will block until some input can be read; otherwise,
     * no threeBytesForInt are read and null is returned.
     *
     * @param lineToCompress line that need to be compressed.
     * @throws IOException
     */
    public void write(String lineToCompress) throws IOException {
        //int byteSize = 0;
        codedLineInBytes.reset();
        lineToCompress += newLineChar;
        try {
            for (char c : lineToCompress.toCharArray()) {
//                if (!dictionary.containsKey(c + "")) {
//                    dictionary.put(c + "", specialCharIndex);
//                    specialCharDictionary.put(c + "", specialCharIndex--);
//                }
                String wc = word + c;                       //temporary concat current and last;
                if (dictionary.containsKey(wc)) {
                    word = wc;                              //if already in dictionary we extend the word
                } else {
                    int integerCode = dictionary.get(word);
                    threeBytesForInt = intToByteArray(integerCode);
                    codedLineInBytes.write(threeBytesForInt);           //add to Byte buffer.
//                    for (byte b : threeBytesForInt) {
//                        codedLineInBytes[byteSize++] = b;
//                    }
                    dictionary.put(wc, keyCount++);
                    word = "" + c;                                      //word points to last encountered Char
                }
            }
            outputStream.write(codedLineInBytes.toByteArray(), 0, codedLineInBytes.size());     //write the Byte Array
        } catch (Exception e) {
            System.out.println("word " + word);
            e.printStackTrace();
            throw new IOException();
        }
    }

    /***
     * Closes this input stream and releases any system resources associated with the stream.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        //adds the last word
        if (!word.equals("") && dictionary.containsKey(word)) {             //last char is written
            int integerCode = dictionary.get(word);
            threeBytesForInt = intToByteArray(integerCode);
            outputStream.write(threeBytesForInt);
        }
//        //signal end of file by a 3 special BYTE -128,-128,-128;
//        threeBytesForInt[0] = -128;
//        threeBytesForInt[1] = -128;
//        threeBytesForInt[2] = -128;
//        outputStream.write(threeBytesForInt);
        //start attaching the special Char Dictionary to File.
        for (String key : specialCharDictionary.keySet()) {
            int codedInt = dictionary.get(key);
            char specialChar = key.charAt(0);
            outputStream.write(intToByteArray(codedInt));
            outputStream.write(intToByteArray(key.charAt(0)));
            System.out.println("*** Added : " + specialChar + " , codedInt: " + codedInt);
        }
        int dictionarySize = (dictionary.size() + specialCharDictionary.size());
        System.out.println("*** Total new characters Added :" + specialCharDictionary.size());
        System.out.println("*** Total Dictionary :" + dictionarySize);
        System.out.println("*** Still available keys for 24 Bytes :" + (Math.pow(2, 24) - dictionarySize));
        System.out.println("*** Time required to compress :" + ((System.currentTimeMillis() - timer) / 1000) + " Secs");
        outputStream.flush();
        outputStream.close();
    }
}
