package rsacipher;

import java.math.BigInteger;
import java.util.Random;

public class RSACipher {
    public static final int FACTOR_BIT_LENGTH = 512, EXPONENT_BIT_LENGTH = 31;

    public RSACipher() {
        setNewKeys();
    }

    public void setNewKeys() {
        setNewPublicKey();
        setNewPrivateKey();
    }

    private BigInteger n, e;
    private BigInteger p,q, d, phi;

    public void setNewPublicKey() {
        Random r = new Random();
        p = BigInteger.probablePrime(FACTOR_BIT_LENGTH, r);
        q = BigInteger.probablePrime(FACTOR_BIT_LENGTH, r);
        n = p.multiply(q);
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        e = BigInteger.probablePrime(EXPONENT_BIT_LENGTH, r);

        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0)
            e.add(BigInteger.ONE);

    }

    public void setNewPrivateKey() {
        d = e.modInverse(phi);
    }

    public static String bytesToString(byte[] encrypted) {
        String result = "";

        for (byte b : encrypted)
            result += Byte.toString(b);

        return result;
    }

    public byte[] encrypt(byte[] message) {
        return (new BigInteger(message)).modPow(e, n).toByteArray();
    }

    public byte[] decrypt(byte[] message) {
        return (new BigInteger(message)).modPow(d, n).toByteArray();
    }

    public byte[] decryptViaCRT(byte[] message){
        BigInteger firstBase = new BigInteger(message).mod(p);
        BigInteger firstExponent = new BigInteger(d.toString()).mod(p.subtract(BigInteger.ONE));

        BigInteger secondBase = new BigInteger(message).mod(q);
        BigInteger secondExponent = new BigInteger(d.toString()).mod(q.subtract(BigInteger.ONE));

        BigInteger firstRemainder = firstBase.modPow(firstExponent, p);
        BigInteger secondRemainder = secondBase.modPow(secondExponent, q);
        BigInteger result = q.multiply(q.modInverse(p)).multiply(firstRemainder)
                .add(p.multiply(p.modInverse(q)).multiply(secondRemainder));

        return  result.mod(n).toByteArray();
    }


}
