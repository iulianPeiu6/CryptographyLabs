package despackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MITMAttack {
    private final List<Boolean> plainTextBits,encryptedTextBits;
    private List<Boolean[]> allPossibleKeys;
    private DESCipher desCipher;
    private List<Pair> firstEncryption, secondEncryption;
    private List<Pair> firstDecryption, secondDecryption;
    private List<Pair> keys1,keys2;

    public MITMAttack(List<Boolean> plainTextBits, List<Boolean> encryptedTextBits) {

        this.plainTextBits = plainTextBits;
        this.encryptedTextBits = encryptedTextBits;

        keys1 = new ArrayList<>();
        keys2 = new ArrayList<>();

    }

    public void run(){
        findAllPossibleKeys();

        setFirstEncryption();
        setSecondDecryption();

        for (var state : firstEncryption){
            updateFirstDecryption(state);
            updateSecondEncryption(state);
            findKeys();
        }

    }

    private void findAllPossibleKeys(){

        allPossibleKeys = new ArrayList<>();

        Boolean[] key = new Boolean[64];
        Arrays.fill(key,false);
        generateAllBinaryStrings(64,key,0);
    }

    private void generateAllBinaryStrings(int n, Boolean[] key, int i) {
        if (i == n) {
            allPossibleKeys.add(key);
            return;
        }

        key[i] = false;
        generateAllBinaryStrings(n, key, i + 1);

        key[i] = true;
        generateAllBinaryStrings(n, key, i + 1);
    }

    private void setFirstEncryption(){

        firstEncryption = new ArrayList<>();

        for (var key : allPossibleKeys){
            desCipher = new DESCipher(plainTextBits,Arrays.asList(key));
            Pair result = new Pair(key,desCipher.encrypt());
            firstEncryption.add(result);
        }

        firstEncryption.sort(new PairComparator());
    }

    private void setSecondDecryption(){
        secondDecryption = new ArrayList<>();
        for (var key : allPossibleKeys){
            desCipher = new DESCipher(Arrays.asList(key));
            desCipher.setEncryptedTextBits(encryptedTextBits);
            Pair result = new Pair(key,desCipher.decrypt());
            firstEncryption.add(result);
        }

        firstEncryption.sort(new PairComparator());

    }

    private void updateFirstDecryption(Pair state) {
        firstDecryption = new ArrayList<>();
        for (var key : allPossibleKeys){
            desCipher = new DESCipher(Arrays.asList(state.value0));
            desCipher.setEncryptedTextBits(state.value1);
            Pair result = new Pair(key,desCipher.decrypt());
            firstDecryption.add(result);
        }

        firstDecryption.sort(new PairComparator());
    }

    private void updateSecondEncryption(Pair state) {
        secondEncryption = new ArrayList<>();
        for (var key : allPossibleKeys){
            desCipher = new DESCipher(state.value1, Arrays.asList(state.value0));
            Pair result = new Pair(key,desCipher.encrypt());
            secondEncryption.add(result);
        }

        secondEncryption.sort(new PairComparator());
    }

    private void findKeys(){
        searchForKeys(firstEncryption,firstDecryption,keys1);
        searchForKeys(secondEncryption,secondDecryption,keys2);
    }

    private void searchForKeys(List<Pair> encryption, List<Pair> decryption,List<Pair> keys){
        for (var pair : encryption){
            int index = Arrays.binarySearch(new List[]{decryption},pair.value0);

            if (index>=0)
                keys.add(new Pair(pair.value0,decryption.get(index).value1));
        }
    }

}
