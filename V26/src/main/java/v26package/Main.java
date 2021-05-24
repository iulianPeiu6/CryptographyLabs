package v26package;

public class Main {

    public static void runTest() {
        Cipher cipher = new Cipher(26);
        cipher.setKey(new int[]{1, 2, 3, 4, 5});

        cipher.setTextFromFile("src\\main\\java\\txtpackage\\Pillar-of-Fire");

        cipher.filterPlainText();
        cipher.writeFilteredTextInFile("src\\main\\java\\txtpackage\\[filtered]Pillar-of-Fire");

        cipher.encryptFilteredText();
        cipher.writeEncryptedTextInFile("src\\main\\java\\txtpackage\\[encrypted]Pillar-of-Fire");

        CipherCracker cipherCracker = new CipherCracker(26, cipher.getEncryptedText());
        cipherCracker.decrypt();
        cipherCracker.writeDecryptedTextInFile("src\\main\\java\\txtpackage\\[decrypted]Pillar-of-Fire");

    }

    public static void main(String[] args) {

        runTest();

    }
}
