package generators;

import java.math.BigInteger;
import java.util.Random;

public class JacobiGenerator extends AbstractGenerator {

    public JacobiGenerator(long seed) {
        super(seed);
    }

    @Override
    protected BigInteger generateProperFactor(){

        BigInteger factor = BigInteger.probablePrime(BIT_LENGTH,new Random());

        return factor;
    }

    @Override
    public String generateRandomBitString(int bitStringLength) {
        StringBuilder bitStringBuilder = new StringBuilder();

        for (int bitIndex=0;bitIndex<bitStringLength;bitIndex++){
            int bit = computeJacobiSymbol(seed,modularNum).intValue() == -1 ? 0 : 1;
            bitStringBuilder.append(bit);
            seed = seed.add(BigInteger.ONE);
        }

        return new String(bitStringBuilder);
    }


    public BigInteger computeJacobiSymbol(BigInteger a, BigInteger n){

        BigInteger b = a.mod(n);
        BigInteger c = n;
        BigInteger s = BigInteger.ONE;

        while (b.compareTo(BigInteger.ONE)==1){
            while (b.mod(BigInteger.valueOf(4)).equals(BigInteger.ZERO))
                b = b.divide(BigInteger.valueOf(4));

            if (b.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
                if (c.mod(BigInteger.valueOf(8)).equals(BigInteger.valueOf(3)) ||
                        c.mod(BigInteger.valueOf(8)).equals(BigInteger.valueOf(5)))
                    s = BigInteger.ZERO.subtract(s);
                b = b.divide(BigInteger.TWO);
            }

            if (b.equals(BigInteger.ONE))
                break;

            if (b.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3)) &&
                    c.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3)))
                s = BigInteger.ZERO.subtract(s);

            BigInteger b_cpy = b;
            b=c.mod(b);
            c=b_cpy;

        }

        return s.multiply(b);

    }

}
