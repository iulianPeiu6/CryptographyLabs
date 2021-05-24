package rc4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RC4Cipher {
    static int RC4GenRezFrequency[] = new int[32];
    static{
        Arrays.fill(RC4GenRezFrequency,0);
    }
    static int instances = 0;

    private final int ASCII_LIMIT = 256;
    private String key;
    private String plainText, encryptText;

    public RC4Cipher(String key, String plainText) {
        instances ++;
        this.key = key;
        this.plainText = plainText;
    }

    private State initialState, currentState;

    private int[] init() {
        int[] k = new int[ASCII_LIMIT];

        for (int i=0;i<ASCII_LIMIT;i++)
            k[i] = i;

        int j = 0;
        for (int i=0;i<ASCII_LIMIT;i++) {
            j = (j + k[i] + key.charAt(i % key.length())) % 256;

            int aux=k[i];
            k[i]=k[j];
            k[j]=aux;
        }

        return k;
    }

    public String encrypt() {
        var t = init();
        int a =0, b = 0;

        StringBuilder encryptedTextBuilder = new StringBuilder();
        for (int i = 0; i < plainText.length(); i++) {
            a = (a + 1) % ASCII_LIMIT;
            b = (b + t[a]) % ASCII_LIMIT;
            int aux=t[a];
            t[a]=t[b];
            t[b]=aux;
            int mid = (t[ (t[a] + t[b]) % ASCII_LIMIT]);
            if (i<32)
                if (mid == 0)
                    RC4GenRezFrequency[i]++;
            encryptedTextBuilder.append( (char) (mid ^ plainText.charAt(i)) );
        }

        encryptText = new String(encryptedTextBuilder);

        return encryptText;
    }

    public String decrypt() {
        var t = init();
        int a =0, b = 0;

        StringBuilder encryptedTextBuilder = new StringBuilder();
        for (int i = 0; i < encryptText.length(); i++) {
            a = (a + 1) % ASCII_LIMIT;
            b = (b + t[a]) % ASCII_LIMIT;
            int aux=t[a];
            t[a]=t[b];
            t[b]=aux;
            int mid = (t[ (t[a] + t[b]) % ASCII_LIMIT]);
            encryptedTextBuilder.append( (char) (mid ^ encryptText.charAt(i)) );
        }

        plainText = new String(encryptedTextBuilder);

        return plainText;
    }

}
