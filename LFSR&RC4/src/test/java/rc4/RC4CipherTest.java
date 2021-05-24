package rc4;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;


class RC4CipherTest {

    private RC4Cipher rc4Cipher;

    @Test
    @DisplayName("The cipher works correctly!")
    public void testCorrectness(){
        String plainText = "plain text";
        String key = "simple key";
        rc4Cipher = new RC4Cipher( key, plainText);

        var encryptedText = rc4Cipher.encrypt();

        System.out.println(encryptedText);

        assertTrue(rc4Cipher.decrypt().equals(plainText));
    }

    @Test
    @DisplayName("The cipher works correctly!")
    public void testBias(){
        final int SAMPLE_SIZE =1_000_000;

        for (int i=0;i<SAMPLE_SIZE;i++){
            String randomKey=generateRandomString((int)(Math.random()*10)+5);
            String randomPlainText = generateRandomString(32);
            rc4Cipher = new RC4Cipher(randomKey, randomPlainText);
            rc4Cipher.encrypt();
            assertTrue(rc4Cipher.decrypt().equals(randomPlainText));
        }

        for (int i=0;i<RC4Cipher.RC4GenRezFrequency.length;i++)
            System.out.print((double)RC4Cipher.RC4GenRezFrequency[i]/RC4Cipher.instances + " ");

    }

    private String generateRandomString(int length) {
        byte[] array = new byte[length];
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));

        return generatedString;
    }


}