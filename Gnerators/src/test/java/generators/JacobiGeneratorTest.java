package generators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.math.BigInteger;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JacobiGeneratorTest {

    private static final int BIT_LENGTH = 512;

    private JacobiGenerator jacobiGenerator;

    @BeforeEach
    public void setUp(){
        jacobiGenerator = new JacobiGenerator((new Date()).getTime());
    }

    @Test
    @DisplayName("First Jacobi symbol test")
    public void jacobiSymbolTest(){

        BigInteger a = BigInteger.valueOf(1236);

        BigInteger n = BigInteger.valueOf(20003);

        assertEquals(1, jacobiGenerator.computeJacobiSymbol(a,n).intValue());

    }

    @Test
    @DisplayName("Second Jacobi symbol test")
    public void jacobiSymbolTest2(){

        BigInteger a = BigInteger.valueOf(12345);

        BigInteger n = BigInteger.valueOf(2021027);

        assertEquals(1, jacobiGenerator.computeJacobiSymbol(a,n).intValue());

    }

    @Test
    @DisplayName("Third Jacobi symbol test")
    public void jacobiSymbolTest3(){

        BigInteger a = BigInteger.valueOf(12346);

        BigInteger n = BigInteger.valueOf(2021027);

        assertEquals(-1, jacobiGenerator.computeJacobiSymbol(a,n).intValue());

    }

    @Test
    @DisplayName("Uniform bit string")
    public void testUniformBitString() throws IOException {

        final int SAMPLE_SIZE = 1000;

        int numOfTrueBits=0,numOfFalseBits;

        new FileOutputStream("src\\test\\java\\Jacobi___bitString.txt").close();

        new BufferedWriter(new FileWriter("src\\test\\java\\Jacobi___onesBitString.txt"))
                .write(new String(new char[SAMPLE_SIZE*BIT_LENGTH]).replace("\0", "1"));

        for (int indexNum = 0; indexNum < SAMPLE_SIZE ; indexNum++)
            numOfTrueBits += getNumOfTrueBits();

        numOfFalseBits = SAMPLE_SIZE*BIT_LENGTH - numOfTrueBits;

        double errorPercentage = Math.abs((double)numOfFalseBits/(SAMPLE_SIZE*BIT_LENGTH)
                - (double)numOfTrueBits/(SAMPLE_SIZE*BIT_LENGTH) );

        //System.out.println(errorPercentage);

        assertTrue(errorPercentage<0.01);

        //  rata compresiei : 82148/148 = 558.16

    }

    private int getNumOfTrueBits(){
        String bitString = jacobiGenerator.generateRandomBitString(BIT_LENGTH);

        assertEquals(BIT_LENGTH, bitString.length());

        addBitStringInFile(bitString);

        return bitString.chars()
                .filter(ch -> ch == '1')
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString()
                .length();
    }

    private void addBitStringInFile(String text){

        try{
            File fileOutput = new File("src\\test\\java\\Jacobi___bitString.txt");

            BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(fileOutput,true));
            bufferWriter.write(text);
            bufferWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}