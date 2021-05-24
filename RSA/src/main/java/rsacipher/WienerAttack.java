package rsacipher;

import math.Fraction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class WienerAttack {
    public List<BigInteger> q = new ArrayList<>();
    public List<Fraction> r = new ArrayList<>();
    public List<BigInteger> n = new ArrayList<>();
    private List<BigInteger> d = new ArrayList<>();
    public Fraction kDdg = new Fraction(BigInteger.ZERO, BigInteger.ONE);

    private BigInteger e;
    private BigInteger N;

    public WienerAttack(BigInteger e, BigInteger N){
        this.e = e;
        this.N = N;
    }

    public BigInteger attack(){
        int i=0;
        BigInteger privateKey;

        while((privateKey = getCurrentIterationResult(i)) == null)
            i++;

        return privateKey;
    }

    private BigInteger getCurrentIterationResult(int iteration){
        if(iteration == 0){
            Fraction ini = new Fraction(e,N);
            q.add(ini.floor());
            r.add(ini.remainder());
            n.add(q.get(0));
            d.add(BigInteger.ONE);
        }
        else if (iteration == 1){
            Fraction temp2 = new Fraction(r.get(0).denominator, r.get(0).numerator);
            q.add(temp2.floor());
            r.add(temp2.remainder());
            n.add((q.get(0).multiply(q.get(1))).add(BigInteger.ONE));
            d.add(q.get(1));
        }
        else{
            if(r.get(iteration-1).numerator.equals(BigInteger.ZERO))
                return BigInteger.ONE.negate();

            Fraction temp3 = new Fraction(r.get(iteration-1).denominator, r.get(iteration-1).numerator);
            q.add(temp3.floor());
            r.add(temp3.remainder());
            n.add((q.get(iteration).multiply(n.get(iteration-1)).add(n.get(iteration-2))));
            d.add((q.get(iteration).multiply(d.get(iteration-1)).add(d.get(iteration-2))));
        }

        if(iteration % 2 == 0){
            if(iteration == 0)
                kDdg = new Fraction(q.get(0).add(BigInteger.ONE), BigInteger.ONE);
            else
                kDdg = new Fraction((q.get(iteration).add(BigInteger.ONE)).multiply(n.get(iteration-1)).add(n.get(iteration-2)),
                        (q.get(iteration).add(BigInteger.ONE)).multiply(d.get(iteration-1)).add(d.get(iteration-2)));
        }

        else
            kDdg = new Fraction(n.get(iteration), d.get(iteration));

        BigInteger edg = this.e.multiply(kDdg.denominator);

        BigInteger fy = (new Fraction(this.e, kDdg)).floor();
        BigInteger g = edg.mod(kDdg.numerator);

        BigDecimal pAqD2 = (new BigDecimal(this.N.subtract(fy))).add(BigDecimal.ONE).divide(new BigDecimal("2"));
        if(!pAqD2.remainder(BigDecimal.ONE).equals(BigDecimal.ZERO))
            return null;

        BigInteger pMqD2s = pAqD2.toBigInteger().pow(2).subtract(N);
        BigInteger pMqD2 = sqrt(pMqD2s);
        if(!pMqD2.pow(2).equals(pMqD2s))
            return null;

        BigInteger privateKey = edg.divide(e.multiply(g));
        return privateKey;

    }

    public static BigInteger sqrt(BigInteger paramBigInteger){
        BigInteger localBigInteger1 = BigInteger.valueOf(0L);
        BigInteger localBigInteger2 = localBigInteger1.setBit(2 * paramBigInteger.bitLength());
        do
        {
            BigInteger localBigInteger3 = localBigInteger1.add(localBigInteger2);
            if (localBigInteger3.compareTo(paramBigInteger) != 1) {
                paramBigInteger = paramBigInteger.subtract(localBigInteger3);
                localBigInteger1 = localBigInteger3.add(localBigInteger2);
            }
            localBigInteger1 = localBigInteger1.shiftRight(1);
            localBigInteger2 = localBigInteger2.shiftRight(2);
        }while (localBigInteger2.bitCount() != 0);
        return localBigInteger1;
    }
}
