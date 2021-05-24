package v26package;

import java.io.FileWriter;
import java.io.IOException;

public class CipherCracker {

    private final int ALPHABET_SIZE;
    private final int MIN_ASCII_CODE;

    private int keyLength;
    private int[] key;
    private String encryptedText;
    private String decryptedText;

    public CipherCracker(int alphabet_size, String encryptedText) {
        ALPHABET_SIZE = alphabet_size;
        MIN_ASCII_CODE = 65;
        this.encryptedText = encryptedText;
    }


    private int[] getLettersFrequency(String text) {
        int[] lettersFrequency = new int[ALPHABET_SIZE];

        for (int i = 0; i < text.length(); i++)
            lettersFrequency[(int) text.charAt(i) - MIN_ASCII_CODE]++;

        return lettersFrequency;
    }

    private double indexOfCoincidence(String text) {
        double indexOfCoincidence = 0.0;
        int[] lettersFrequency = getLettersFrequency(text);

        for (int i = 0; i < ALPHABET_SIZE; i++)
            indexOfCoincidence += (((double) lettersFrequency[i] / text.length()) *
                    ((double) (lettersFrequency[i] - 1) / (text.length() - 1)));

        return indexOfCoincidence;

    }

    private String getSubstring(int i, int m) {
        StringBuilder substringBuilder = new StringBuilder();

        for (int k = i; k < encryptedText.length(); k += m)
            substringBuilder.append(encryptedText.charAt(k));

        return new String(substringBuilder);

    }

    private boolean bounded(double indexOfCoincidence) {

        return 0.060 < indexOfCoincidence && indexOfCoincidence < 0.070;
    }

    private void breakKeyLength() {
        boolean keyLengthFound = false;
        keyLength = 0;

        while (!keyLengthFound) {
            keyLengthFound = true;
            keyLength++;

            for (int k = 0; k < keyLength; k++) {
                if (!bounded(indexOfCoincidence(getSubstring(k, keyLength)))) {
                    keyLengthFound = false;
                    break;
                }
            }
        }
    }

    private String shiftString(String string, int shiftValue) {
        StringBuilder shiftStringBuilder = new StringBuilder();

        for (int i = 0; i < string.length(); i++)
            shiftStringBuilder.append((char) (((int) string.charAt(i) - MIN_ASCII_CODE + shiftValue) % ALPHABET_SIZE + MIN_ASCII_CODE));

        return new String(shiftStringBuilder);

    }

    private double getMutualIndexOfCoincidence(int j, int s) {

        double[] p = {0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015, 0.06094, 0.06966,
                0.00153, 0.00772, 0.04025, 0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987,
                0.06327, 0.09056, 0.02758, 0.00978, 0.02360, 0.00150, 0.01974, 0.00074};

        double mic = 0.0;
        int[] partialLettersFrequency = getLettersFrequency(shiftString(getSubstring(j, keyLength), s));

        for (int i = 0; i < ALPHABET_SIZE; i++) {
            mic += (p[i] * (((double) partialLettersFrequency[i] /
                    shiftString(getSubstring(j, keyLength), s).length())));
        }

        return mic;
    }

    private void findKey() {
        breakKeyLength();
        key = new int[keyLength];

        for (int j = 0; j < keyLength; j++) {

            int s = 0;

            while (!bounded(getMutualIndexOfCoincidence(j, s))) {
                s++;
            }

            key[j] = (ALPHABET_SIZE - s) % ALPHABET_SIZE;
        }
    }

    public void decrypt() {
        findKey();

        StringBuilder decryptedTextBuilder = new StringBuilder();

        for (int i = 0; i < encryptedText.length(); i++) {
            int currentAsciiChar = encryptedText.charAt(i);

            if (currentAsciiChar % MIN_ASCII_CODE - key[i % keyLength] < 0)
                currentAsciiChar = (ALPHABET_SIZE - (currentAsciiChar % MIN_ASCII_CODE - key[i % keyLength])) % ALPHABET_SIZE + MIN_ASCII_CODE;
            else
                currentAsciiChar = (currentAsciiChar % MIN_ASCII_CODE - key[i % keyLength]) % ALPHABET_SIZE + MIN_ASCII_CODE;

            char decryptedChar = (char) currentAsciiChar;

            decryptedTextBuilder.append(decryptedChar);
        }

        decryptedText = new String(decryptedTextBuilder);

    }

    public void writeDecryptedTextInFile(String filePath) {
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(decryptedText);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
