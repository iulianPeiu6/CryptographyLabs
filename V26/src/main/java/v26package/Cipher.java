package v26package;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Cipher {

    private final int ALPHABET_SIZE;
    private final int MIN_ASCII_CODE;

    private int keyLength;
    private int[] key;
    private String plainText;
    private String filteredText;
    private String encryptedFilteredText;

    public Cipher(int alphabetSize) {
        ALPHABET_SIZE = alphabetSize;
        MIN_ASCII_CODE = 65;
    }

    public void setKey(int[] key) {
        this.keyLength = key.length;
        this.key = key;
    }

    public void setTextFromFile(String filePath) {
        StringBuilder plainTextBuilder = new StringBuilder();

        try {
            File fileInput = new File(filePath);

            Scanner reader = new Scanner(fileInput);

            while (reader.hasNextLine()) {
                String textLine = reader.nextLine();
                plainTextBuilder.append(textLine);
                plainTextBuilder.append('\n');
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        plainText = new String(plainTextBuilder);
    }

    public void filterPlainText() {
        StringBuilder filteredTextBuilder = new StringBuilder();

        for (int i = 0; i < plainText.length(); i++)
            if (Character.isAlphabetic(plainText.charAt(i)))
                filteredTextBuilder.append(Character.toUpperCase(plainText.charAt(i)));

        filteredText = new String(filteredTextBuilder);
    }

    public void encryptFilteredText() {
        StringBuilder encryptedTextBuilder = new StringBuilder();

        for (int i = 0; i < filteredText.length(); i++) {
            int currentAsciiChar = (int) filteredText.charAt(i);
            currentAsciiChar = (currentAsciiChar % MIN_ASCII_CODE + key[i % keyLength]) % ALPHABET_SIZE + MIN_ASCII_CODE;

            char encryptedChar = (char) currentAsciiChar;

            encryptedTextBuilder.append(encryptedChar);
        }

        encryptedFilteredText = new String(encryptedTextBuilder);
    }

    public void writeFilteredTextInFile(String filePath) {
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(filteredText);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeEncryptedTextInFile(String filePath) {
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(encryptedFilteredText);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getEncryptedText() {
        return encryptedFilteredText;
    }

}
