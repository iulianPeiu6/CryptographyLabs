package despackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DESCipher {

    static final int BIT_PLAIN_TEXT_LENGTH = 64;
    static final int NUM_OF_ROUNDS = 16;
    static final int KEY_LENGTH = 64 - 8;

    private List<Boolean> plainTextBits;
    private final List<Boolean> key;
    private List<Boolean> encryptedTextBits;
    private List<Integer> initialPermutation, firstPC, secondPC, expandSelectionBits, permutationP, inverseIP;
    private List<List<Integer>> sBoxes;
    private List<List<Boolean>> left32, right32;
    private List<List<Boolean>> first28;
    private List<List<Boolean>> last28;
    private List<List<Boolean>> keySchedule;


    public DESCipher(List<Boolean> plainTextBits, List<Boolean> key) {
        this.plainTextBits = plainTextBits;
        this.key = key;

        setUp();
    }

    public DESCipher(List<Boolean> key) {
        this.key = key;
        setUp();
    }

    public void setEncryptedTextBits(List<Boolean> encryptedTextBits) {
        this.encryptedTextBits = encryptedTextBits;
    }

    public void setUp(){
        left32 = new ArrayList<>();
        right32 = new ArrayList<>();
        first28 = new ArrayList<>();
        last28 = new ArrayList<>();
        sBoxes = new ArrayList<>();

        setInitialPermutation();
        setInverseIP();
        setFirstPC();
        setSecondPC();
        setExpandSelectionBits();
        setPermutationP();
        setSBoxes();
        setKeys(key);
    }

    private void setKeys(List<Boolean> key) {
        keySchedule = new ArrayList<>();

        var permutedKey = permute(key, firstPC);

        keySchedule.add(permutedKey);

        first28.add(permutedKey.subList(0, KEY_LENGTH / 2));
        last28.add(permutedKey.subList(KEY_LENGTH / 2, KEY_LENGTH));

        for (int roundIndex = 0; roundIndex < NUM_OF_ROUNDS; roundIndex++) {
            findNextKey(roundIndex);
            printKeySchedule(roundIndex + 1);
        }

    }

    private void findNextKey(int lastKeyIndex) {
        List<Boolean> currentFirst28, currentLast28;

        currentFirst28 = first28.get(lastKeyIndex);
        currentLast28 = last28.get(lastKeyIndex);

        first28.add(leftShift(currentFirst28, lastKeyIndex +1));
        last28.add(leftShift(currentLast28, lastKeyIndex +1));

        List<Boolean> newKey;

        newKey = first28.get(lastKeyIndex + 1);
        newKey.addAll(last28.get(lastKeyIndex + 1));
        newKey = permute(newKey, secondPC);

        keySchedule.add(newKey);

    }

    public List<Boolean> encrypt() {

        left32 = new ArrayList<>();
        right32 = new ArrayList<>();

        applyIP(plainTextBits);

        for (int roundIndex = 1; roundIndex <= NUM_OF_ROUNDS; roundIndex++) {
            left32.add(right32.get(roundIndex-1));

            right32.add( xor(left32.get(roundIndex-1),
                            computeNewRight32(right32.get(roundIndex-1),keySchedule.get(roundIndex))));

            printLeft32(roundIndex);
            printRight32(roundIndex);
        }

        encryptedTextBits = new ArrayList<>();

        encryptedTextBits.addAll(right32.get(NUM_OF_ROUNDS));
        encryptedTextBits.addAll(left32.get(NUM_OF_ROUNDS));

        encryptedTextBits = permute(encryptedTextBits,inverseIP);

        return encryptedTextBits;
    }

    public List<Boolean> decrypt(){
        return decrypt(encryptedTextBits);
    }

    public List<Boolean> decrypt(List<Boolean> encryptedTextBits){
        left32 = new ArrayList<>();
        right32 = new ArrayList<>();

        applyIP(encryptedTextBits);


        for (int roundIndex = NUM_OF_ROUNDS; roundIndex >= 1; roundIndex--) {
            left32.add(right32.get(NUM_OF_ROUNDS - roundIndex));

            right32.add( xor(left32.get(NUM_OF_ROUNDS - roundIndex),
                    computeNewRight32(right32.get(NUM_OF_ROUNDS - roundIndex),keySchedule.get(roundIndex))));

            printLeft32(NUM_OF_ROUNDS - roundIndex+1);
            printRight32(NUM_OF_ROUNDS - roundIndex+1);
        }

        encryptedTextBits = new ArrayList<>();

        encryptedTextBits.addAll(left32.get(NUM_OF_ROUNDS));
        encryptedTextBits.addAll(right32.get(NUM_OF_ROUNDS));


        encryptedTextBits = permute(encryptedTextBits,inverseIP);

        return encryptedTextBits;
    }

    private List<Boolean> computeNewRight32(List<Boolean> bitList, List<Boolean> currentKey){

        List<Boolean> result = new ArrayList<>();
        List<Boolean> expandedBitList = expand(bitList);
        List<Boolean> b = xor(expandedBitList, currentKey);

        for (int i=0;i<8;i++){
            result.addAll(getPartC(b.subList(i*6,i*6+6),i));
        }

        result = permute(result,permutationP);

        return result;
    }

    private List<Boolean> getPartC(List<Boolean> b, int i){

        List<Boolean> result = new ArrayList<>();

        int r = boolToInt(b.get(0))*2 + boolToInt(b.get(5));
        int c = boolToInt(b.get(1))*8 + boolToInt(b.get(2))*4 +boolToInt(b.get(3))*2 +boolToInt(b.get(4));

        int val = sBoxes.get(i).get(r*16+c);
        var valInBase2 = Integer.parseInt(Integer.toString(val,2));

        for (int j=3;j>=0;j--){
            int digit = (valInBase2/(int)Math.pow(10,j))%10;
            result.add(digit != 0);
        }

        return result;
    }

    private int boolToInt(Boolean b) {
        return b ? 1 : 0;
    }

    private void applyIP(List<Boolean> text) {
        List<Boolean> permutedPlainTextBits = permute(text, initialPermutation);

        left32.add(permutedPlainTextBits.subList(0, BIT_PLAIN_TEXT_LENGTH / 2));
        right32.add(permutedPlainTextBits.subList(BIT_PLAIN_TEXT_LENGTH / 2, BIT_PLAIN_TEXT_LENGTH));

        printLeft32(0);
        printRight32(0);
    }

    private List<Boolean> permute(List<Boolean> bitList, List<Integer> permutation) {
        List<Boolean> permutedList = new ArrayList<>();

        for (Integer permIndex : permutation)
            permutedList.add(bitList.get(permIndex - 1));

        return permutedList;

    }

    private List<Boolean> leftShift(List<Boolean> bitList, int index) {

        List<Boolean> result = new ArrayList<>(bitList);

        result.add(result.get(0));
        result.remove(0);

        if (!(index == 1 || index == 2 || index == 9 || index == 16)) {
            result.add(result.get(0));
            result.remove(0);
        }

        return result;

    }

    private List<Boolean> expand(List<Boolean> bitList) {
        List<Boolean> result = new ArrayList<>();

        for (Integer expandSelectionBit : expandSelectionBits) result.add(bitList.get(expandSelectionBit - 1));

        return result;
    }

    private List<Boolean> xor(List<Boolean> firstBits, List<Boolean> secondBits) {

        List<Boolean> result = new ArrayList<>();

        for (int i = 0; i < firstBits.size(); i++)
            if (firstBits.get(i).equals(secondBits.get(i)))
                result.add(false);
            else
                result.add(true);

        return result;
    }


    private void printLeft32(int fromIndex) {

        List<Boolean> left32FromIndex = left32.get(fromIndex);

        System.out.print("L" + fromIndex + ":");

        for (Boolean bit : left32FromIndex)
            System.out.print(bit ? 1 : 0);

        System.out.print("\n");

    }

    private void printRight32(int fromIndex) {

        List<Boolean> right32FromIndex = right32.get(fromIndex);

        System.out.print("R" + fromIndex + ":");

        for (Boolean bit : right32FromIndex)
            System.out.print(bit ? 1 : 0);

        System.out.print("\n");
    }

    private void printKeySchedule(int fromIndex) {
        List<Boolean> keyScheduleFromIndex = keySchedule.get(fromIndex);

        System.out.print("K" + fromIndex + ":");

        for (Boolean bit : keyScheduleFromIndex)
            System.out.print(bit ? 1 : 0);

        System.out.print("\n");
    }

    private void printExpandedBitList(List<Boolean> expandedRight32, int index) {

        System.out.print("E(R" + index + "):");

        for (Boolean bit : expandedRight32)
            System.out.print(bit ? 1 : 0);

        System.out.print("\n");
    }

    private void setInitialPermutation() {

        initialPermutation = Arrays.asList(58, 50, 42, 34, 26, 18, 10, 2,
                60, 52, 44, 36, 28, 20, 12, 4,
                62, 54, 46, 38, 30, 22, 14, 6,
                64, 56, 48, 40, 32, 24, 16, 8,
                57, 49, 41, 33, 25, 17, 9, 1,
                59, 51, 43, 35, 27, 19, 11, 3,
                61, 53, 45, 37, 29, 21, 13, 5,
                63, 55, 47, 39, 31, 23, 15, 7);
    }

    private void setFirstPC() {

        firstPC = Arrays.asList(57, 49, 41, 33, 25, 17, 9,
                1, 58, 50, 42, 34, 26, 18,
                10, 2, 59, 51, 43, 35, 27,
                19, 11, 3, 60, 52, 44, 36,
                63, 55, 47, 39, 31, 23, 15,
                7, 62, 54, 46, 38, 30, 22,
                14, 6, 61, 53, 45, 37, 29,
                21, 13, 5, 28, 20, 12, 4);

    }

    private void setSecondPC() {

        secondPC = Arrays.asList(14, 17, 11, 24, 1, 5,
                3, 28, 15, 6, 21, 10,
                23, 19, 12, 4, 26, 8,
                16, 7, 27, 20, 13, 2,
                41, 52, 31, 37, 47, 55,
                30, 40, 51, 45, 33, 48,
                44, 49, 39, 56, 34, 53,
                46, 42, 50, 36, 29, 32);

    }

    private void setExpandSelectionBits() {

        expandSelectionBits = Arrays.asList(32, 1, 2, 3, 4, 5,
                4, 5, 6, 7, 8, 9,
                8, 9, 10, 11, 12, 13,
                12, 13, 14, 15, 16, 17,
                16, 17, 18, 19, 20, 21,
                20, 21, 22, 23, 24, 25,
                24, 25, 26, 27, 28, 29,
                28, 29, 30, 31, 32, 1);
    }

    private void setPermutationP() {

        permutationP = Arrays.asList(16, 7, 20, 21,
                29, 12, 28, 17,
                1, 15, 23, 26,
                5, 18, 31, 10,
                2, 8, 24, 14,
                32, 27, 3, 9,
                19, 13, 30, 6,
                22, 11, 4, 25);
    }

    private void setInverseIP() {

        inverseIP = Arrays.asList(40, 8, 48, 16, 56, 24, 64, 32,
                39, 7, 47, 15, 55, 23, 63, 31,
                38, 6, 46, 14, 54, 22, 62, 30,
                37, 5, 45, 13, 53, 21, 61, 29,
                36, 4, 44, 12, 52, 20, 60, 28,
                35, 3, 43, 11, 51, 19, 59, 27,
                34, 2, 42, 10, 50, 18, 58, 26,
                33, 1, 41, 9, 49, 17, 57, 25);
    }

    private void setSBoxes() {

        sBoxes.add(Arrays.asList(
                14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
                0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
                4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
                15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13));

        sBoxes.add(Arrays.asList(
                15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
                3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
                0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
                13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9));

        sBoxes.add(Arrays.asList(
                10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
                13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
                13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
                1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12));

        sBoxes.add(Arrays.asList(
                7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
                13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
                10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
                3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14));

        sBoxes.add(Arrays.asList(
                2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
                14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
                4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
                11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3));

        sBoxes.add(Arrays.asList(
                12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
                10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
                9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
                4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13));

        sBoxes.add(Arrays.asList(
                4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
                13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
                1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
                6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12));

        sBoxes.add(Arrays.asList(
                13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
                1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
                7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
                2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11));
    }

}
