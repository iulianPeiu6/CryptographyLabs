package generators;

import java.math.BigInteger;
import java.util.Random;

public class BBSGenerator extends AbstractGenerator {

    public BBSGenerator(long seed){
        super(seed);
    }

    @Override
    protected BigInteger generateProperFactor(){

        BigInteger factor = BigInteger.probablePrime(BIT_LENGTH,new Random());

        while (!factor.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3)))
            factor = BigInteger.probablePrime(BIT_LENGTH, new Random());

        return factor;

    }

    @Override
    public String generateRandomBitString(int bitStringLength){
        StringBuilder bitStringBuilder = new StringBuilder();

        for (int bitIndex=0;bitIndex<bitStringLength;bitIndex++){
            bitStringBuilder.append(seed.mod(BigInteger.TWO).intValue());
            seed = seed.modPow(BigInteger.TWO,modularNum);
        }

        return new String(bitStringBuilder);
    }
}
