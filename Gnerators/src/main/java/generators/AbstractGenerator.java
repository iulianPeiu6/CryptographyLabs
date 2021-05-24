package generators;

import java.math.BigInteger;
import java.util.Random;

public abstract class AbstractGenerator {
    protected static int BIT_LENGTH = 512;

    protected BigInteger firstFactor;

    protected BigInteger secondFactor;

    protected BigInteger modularNum;

    protected BigInteger seed;

    public AbstractGenerator(long seed) {
        this.seed = BigInteger.valueOf(seed);

        firstFactor = generateProperFactor();

        secondFactor = generateProperFactor();

        modularNum = firstFactor.multiply(secondFactor);
    }

    protected abstract BigInteger generateProperFactor();

    public abstract String generateRandomBitString(int bitStringLength);

    public BigInteger getSeed() {
        return seed;
    }

    public BigInteger getFirstFactor() {
        return firstFactor;
    }

    public BigInteger getSecondFactor() {
        return secondFactor;
    }

}
