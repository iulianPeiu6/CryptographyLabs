package generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LFSRGenerator {
    private final int baseLength;
    private List<Integer> connectionPoly;
    private List<Boolean> initialState;

    public LFSRGenerator(int baseLength, List<Integer> connectionPoly, List<Boolean> initialState) {
        this.baseLength = baseLength;
        this.connectionPoly = connectionPoly;
        this.initialState = initialState;
    }

    public LFSRGenerator(int baseLength) {
        this.baseLength = baseLength;
        setDefaultConnectionPoly();
        setDefaultInitialState();
    }

    private void setDefaultConnectionPoly() {
        connectionPoly = Arrays.asList(16, 5, 3, 2);
    }

    private void setDefaultInitialState() {
        initialState = new ArrayList<>();
        for (int i=0;i<16;i++)
            initialState.add(true);
    }

    public List<Boolean> generateRandomBitList(int ofLength){
        List<Boolean> bitList = new ArrayList<>();
        var lastState = initialState;
        Boolean currentBit;

        for (int i=0;i<ofLength;i++){
            currentBit = lastState.get(lastState.size()-1);
            bitList.add(currentBit);
            lastState = getNextState(lastState);

        }
        return bitList;
    }

    public String generateRandomBitString(int ofLength){
        var bitStringBuilder = new StringBuilder();
        var lastState = initialState;
        Boolean currentBit;

        for (int i=0;i<ofLength;i++){
            currentBit = lastState.get(lastState.size()-1);
            bitStringBuilder.append(currentBit?"1":"0");
            lastState = getNextState(lastState);

        }
        return new String(bitStringBuilder);
    }

    private List<Boolean> getNextState(List<Boolean> lastState) {
        Boolean newFirstBit;

        if (connectionPoly.size() == 2){
            int m = connectionPoly.get(0)-1;
            int y = connectionPoly.get(1)-1;
            newFirstBit = xor(lastState.get(m), lastState.get(y));
            lastState = shift(lastState);
            lastState.set(0,newFirstBit);
            return lastState;
        }
        else if (connectionPoly.size() == 4){
            int m = connectionPoly.get(0)-1;
            int y1 = connectionPoly.get(1)-1;
            int y2 = connectionPoly.get(2)-1;
            int y3 = connectionPoly.get(3)-1;
            newFirstBit = xor(xor(xor(lastState.get(m),
                    lastState.get(y1)),
                    lastState.get(y2)),
                    lastState.get(y3));
            lastState = shift(lastState);
            lastState.set(0,newFirstBit);
            return lastState;
        }

        return lastState;
    }

    private Boolean xor(Boolean firstBoolean, Boolean secondBoolean) {
        return firstBoolean != secondBoolean;
    }

    private List<Boolean> shift(List<Boolean> bitList){
        for (int i=bitList.size()-1;i>0;i--)
            bitList.set(i,bitList.get(i-1));

        return bitList;
    }

    public int getPeriod(){
        int period = 0, maxPeriod = (int) Math.pow(2,baseLength);
        Boolean[] frequency = new Boolean[maxPeriod];
        Arrays.fill(frequency, false);

        var currentState = initialState;

        for (int i=1;i<maxPeriod;i++){
            int currentNumber = Integer.parseInt(listToString(currentState),2);
            if (frequency[currentNumber].equals(false)){
                period++;
                frequency[currentNumber] = true;
            }
            currentState = getNextState(currentState);
        }

        return period;
    }

    private String listToString(List<Boolean> currentState) {
        StringBuilder bitStringBuilder = new StringBuilder();

        for (Boolean bit : currentState){
            bitStringBuilder.append(bit?"1":"0");
        }

        return new String(bitStringBuilder);
    }
    
}
