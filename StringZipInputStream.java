/***
 * author Arjun Dhuliya
 * author Nihar Vanjara
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

/***
 * StringZipInputStream decompress an already compressed file using the LZW compression and decompression algorithm
 */
public class StringZipInputStream {

    private static final int BYTE_SIZE = 3;
    private static final int MAX_BUFFER = BYTE_SIZE * 120;
    private static HashMap<Integer, String> dictionary;
    int MAX_CHAR_COUNT =65536;
    private static int codedInteger;
    private static FileInputStream fileInputStream;
    private static int keyCount = 0;
    private final byte[] byteBufferArray = new byte[MAX_BUFFER];     //used read encoded integer from file
    private final long startTime = System.currentTimeMillis();
    //    private ByteArrayInputStream byteArrayInputStream;
    private boolean firstTime = true;

    /***
     * Creates a new input stream with a default buffer size.
     *
     * @param fileInputStream file inputStream obj to read data
     */
    public StringZipInputStream(FileInputStream fileInputStream) {

        StringZipInputStream.fileInputStream = fileInputStream;
        dictionary = initDictionary();

    }

    /***
     * returns an integer for the byteArray
     *
     * @param bytes byte array
     * @return returns an integer for the byteArray
     */
    private static int toInt(byte[] bytes) {
        int ret = 0;
        for (int i = 0; i < StringZipInputStream.BYTE_SIZE && i < bytes.length; i++) {
            ret <<= 8;
            ret |= (int) bytes[i] & 0xFF;
        }
        return ret;
    }

    /***
     * creates a new dictionary and adds all 0 to 255 ascii chars and values.
     *
     * @return a new dictionary with all 0 to 255 ascii chars and values.
     */
    private HashMap<Integer, String> initDictionary() {
        dictionary = new HashMap<>();
//        ByteArrayOutputStream readBytes = new ByteArrayOutputStream();

//        byte[] eofBytes = {-128, -128, -128};

//        byte[] bytes = new byte[BYTE_SIZE];

        for (keyCount = 0; keyCount < MAX_CHAR_COUNT; keyCount++) {
            dictionary.put(keyCount, (char) keyCount + "");
        }

//        for (int i = 0; i < 256; i++) {
//            dictionary.put(i, (char) i + "");
//        }

//        try {
//            int r = fileInputStream.read(bytes, 0, 3);
//            while (r != -1) {
//                if (Arrays.equals(bytes, eofBytes)) {
//                    r = fileInputStream.read(bytes);
//                    while (r != -1) {
//                        int key = toInt(bytes);
//                        r = fileInputStream.read(bytes);
////                        int ch = toInt(bytes);
//                        if (r != -1) {
//                            dictionary.put(key, ((char) toInt(bytes)) + "");
//                            r = fileInputStream.read(bytes);
//                        }
//                    }
//                } else {
//                    readBytes.write(bytes);
//
//                }
//                r = fileInputStream.read(bytes);
//            }
//            byteArrayInputStream = new ByteArrayInputStream(readBytes.toByteArray());
//
//            //fileInputStream.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return dictionary;
    }

    /***
     * Reads data into a string. the method will block until some input can be read;
     * otherwise, no threeBytesForInt are read and null is returned.
     *
     * @return String to write to file, returns null when nothing to write
     */
    public String read() {
        StringBuilder result = new StringBuilder(); // build your output string
        int r;                                      //tracks if we get byte or not
        String textToWrite;                         //temporary String textToWrite, which is appended to output
        String pC;                                  // previous Coded Integer
        String previousString = "";                      // String corresponding the previous Coded Integer
        int currentByteIndex;
        int previousCodedInt;
        byte[] threeByteCodedInt = new byte[BYTE_SIZE];
        try {
            r = fileInputStream.read(byteBufferArray, 0, MAX_BUFFER);
//            r = byteArrayInputStream.read(byteBufferArray, 0, MAX_BUFFER);
            currentByteIndex = 0;
            if (firstTime) {
                if (r == -1) {                                      // -1 indicate, end of file encountered
                    close();
                    return null;
                }
                for (int count = 0; count < BYTE_SIZE; count++) {
                    threeByteCodedInt[count] = byteBufferArray[currentByteIndex++];
                }
                codedInteger = toInt(threeByteCodedInt);
                textToWrite = dictionary.get(codedInteger);
                result.append(textToWrite);
                firstTime = false;
            }
            if (currentByteIndex >= r - 1) {
                r = fileInputStream.read(byteBufferArray, 0, MAX_BUFFER);
//                r = byteArrayInputStream.read(byteBufferArray, 0, MAX_BUFFER);
                currentByteIndex = 0;
            }
            do {
                previousCodedInt = codedInteger;
                if (r == -1) {                                      // -1 indicate, end of file encountered
                    //close();
                    return null;
                }
                for (int count = 0; count < BYTE_SIZE; count++) {
                    threeByteCodedInt[count] = byteBufferArray[currentByteIndex++];
                }

                codedInteger = toInt(threeByteCodedInt);
                if (dictionary.containsKey(codedInteger)) {
                    textToWrite = dictionary.get(codedInteger);
                    result.append(textToWrite);
                    previousString = dictionary.get(previousCodedInt);
                    dictionary.put(keyCount++, previousString + textToWrite.charAt(0));
                } else {
                    previousString = dictionary.get(previousCodedInt);

                    pC = previousString + previousString.charAt(0);
                    result.append(pC);
                    dictionary.put(keyCount++, pC);
                }
                if (currentByteIndex >= r - 1) {                                //check if pointing index reached end
                    r = fileInputStream.read(byteBufferArray, 0, MAX_BUFFER);
        //          r = byteArrayInputStream.read(byteBufferArray, 0, MAX_BUFFER);
                    currentByteIndex = 0;                                       //reset pointing index
                }
            } while (r != -1);

        } catch (Exception e) {
            System.out.print("****** Exception caused by word: " + previousString);
            e.printStackTrace();
            System.exit(-1);
        }
        return result.toString();
    }

    /***
     * close the fileInputStream.
     */
    public void close() {
        try {
            System.out.print("Successfully UnCompressed. Time taken: " + ((System.currentTimeMillis() - startTime) / 1000.0) +
                    " Secs");
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}