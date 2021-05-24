package rsacipher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RSACipherTest {

    private RSACipher rsaCipher;

    @BeforeEach
    public void setUp(){
        rsaCipher = new RSACipher();
    }

    private final int SAMPLE_SIZE = 10_000;

    @Test
    public void testEncryptAndDecrypt(){
        String testPlainText = "PlainText example";

        System.out.println("Encrypting Text: " + testPlainText);
        System.out.println("String in Bytes: "
                + RSACipher.bytesToString(testPlainText.getBytes()));

        byte[] encrypted = rsaCipher.encrypt(testPlainText.getBytes());
        byte[] decryptedViaStandard = rsaCipher.decrypt(encrypted);
        byte[] decryptedViaCRT = rsaCipher.decryptViaCRT(encrypted);

        System.out.println("Standard Decrypting Bytes: " + RSACipher.bytesToString(decryptedViaStandard));
        System.out.println("Standard Decrypted String: " + new String(decryptedViaStandard));

        System.out.println("CRT Decrypting Bytes: " + RSACipher.bytesToString(decryptedViaCRT));
        System.out.println("CRT Decrypted String: " + new String(decryptedViaCRT));

        assertTrue(testPlainText.equals(new String(decryptedViaStandard)));
        assertTrue(testPlainText.equals(new String(decryptedViaCRT)));


    }

    @Test
    public void testStandardDecryptionPerformance(){
        var start = System.currentTimeMillis();
        for (int i=0; i<SAMPLE_SIZE; i++) {

            String testPlainText = generateRandomString(32);

            byte[] encrypted = rsaCipher.encrypt(testPlainText.getBytes());
            byte[] decrypted = rsaCipher.decrypt(encrypted);

            assertTrue(testPlainText.equals(new String(decrypted)));
        }
        var end = System.currentTimeMillis();;
        System.out.println(end - start);
    }

    @Test
    public void testCRTDecryptionPerformance(){
        var start = System.currentTimeMillis();
        for (int i=0; i<SAMPLE_SIZE; i++) {
            String testPlainText = generateRandomString(32);

            byte[] encrypted = rsaCipher.encrypt(testPlainText.getBytes());
            byte[] decrypted = rsaCipher.decryptViaCRT(encrypted);

            assertTrue(testPlainText.equals(new String(decrypted)));
        }
        var end = System.currentTimeMillis();;
        System.out.println(end - start);
    }

    private String generateRandomString(int length){
        int leftLimit = 48;
        int rightLimit = 122;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }
}