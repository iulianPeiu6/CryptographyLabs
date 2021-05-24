package generators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.math.BigInteger;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class BBSGeneratorTest {

    private static final int BIT_LENGTH = 512;

    private BBSGenerator bbsGenerator;

    @BeforeEach
    public void setUp(){
        bbsGenerator = new BBSGenerator((new Date()).getTime());
    }

    @Test
    @DisplayName("Seed was assigned")
    public void testSeedExists(){

        assertNotNull(bbsGenerator.getSeed(),"Seed should not be null");
    }

    @Test
    @DisplayName("Proper factors were created")
    public void testFactorsAreProper(){

        assertTrue(testFactorIsProper(bbsGenerator.getFirstFactor()),
                "First factor should be prime and congruent with 3 modulo 4");
        
        assertTrue(testFactorIsProper(bbsGenerator.getSecondFactor()),
                "Second factor should be prime and congruent with 3 modulo 4");
    }

    private Boolean testFactorIsProper(BigInteger factor){
        int PRIMALITY_CERTAINTY = 100;
        return factor.isProbablePrime(PRIMALITY_CERTAINTY)
                && factor.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3));
    }

    @Test
    @DisplayName("Uniform bit string")
    public void testUniformBitString() throws IOException {

        final int SAMPLE_SIZE = 1000;

        int numOfTrueBits=0,numOfFalseBits;

        new FileOutputStream("src\\test\\java\\BBS___bitString.txt").close();

        new BufferedWriter(new FileWriter("src\\test\\java\\BBS___onesBitString.txt"))
                .write(new String(new char[SAMPLE_SIZE*BIT_LENGTH]).replace("\0", "1"));

        for (int indexNum = 0; indexNum < SAMPLE_SIZE ; indexNum++)
            numOfTrueBits += getNumOfTrueBits();

        numOfFalseBits = SAMPLE_SIZE*BIT_LENGTH - numOfTrueBits;

        double errorPercentage = Math.abs((double)numOfFalseBits/(SAMPLE_SIZE*BIT_LENGTH)
                - (double)numOfTrueBits/(SAMPLE_SIZE*BIT_LENGTH) );

        //System.out.println(errorPercentage);

        assertTrue(errorPercentage<0.01);

        //  rata compresiei : 82511/145 = 569.04

    }

    private int getNumOfTrueBits(){
        String bitString = bbsGenerator.generateRandomBitString(BIT_LENGTH);

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
            File fileOutput = new File("src\\test\\java\\BBS___bitString.txt");

            BufferedWriter bufferWriter = new BufferedWriter(new FileWriter(fileOutput,true));
            bufferWriter.write(text);
            bufferWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}