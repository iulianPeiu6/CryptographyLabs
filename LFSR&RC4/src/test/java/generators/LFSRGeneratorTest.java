package generators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LFSRGeneratorTest {

    private LFSRGenerator lfsrGenerator;

    @BeforeEach
    public void setUp(){
        lfsrGenerator = new LFSRGenerator(16);
    }

    @Test
    @DisplayName("Generator should work correctly, as intended!")
    public void testCorrectness(){

        var bitList = lfsrGenerator.generateRandomBitList(32);

        System.out.println(bitList);

        assertTrue(bitList.equals(Arrays.asList(
                true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true,
                false, false, true, false, true, false, false, true, true, true, true, true, false, false, true, false
        )));
    }

    @Test
    @DisplayName("Time!")
    public void testTime(){
        final int BIT_LENGTH = 1_000_000;

        printTimeBBSGenerator(BIT_LENGTH);
        printTimeJacobiGenerator(BIT_LENGTH);
        printTimeLFSRGenerator(BIT_LENGTH);

    }

    private void printTimeLFSRGenerator(int bit_length) {
        long startTime = System.nanoTime();
        LFSRGenerator lfstGenerator = new LFSRGenerator(4,
                Arrays.asList(4,1),
                Arrays.asList(false, true, true, false));
        lfstGenerator.generateRandomBitString(bit_length);
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        System.out.println("Execution time(milliseconds) for LFST: " + timeElapsed / 1000000);
    }

    private void printTimeJacobiGenerator(int bit_length) {
        long startTime = System.nanoTime();
        JacobiGenerator jacobiGenerator = new JacobiGenerator((new Date()).getTime());
        jacobiGenerator.generateRandomBitString(bit_length);
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        System.out.println("Execution time(milliseconds) for Jacobi: " + timeElapsed / 1000000);
    }

    private void printTimeBBSGenerator(int bit_length) {
        long startTime = System.nanoTime();
        BBSGenerator bbsGenerator = new BBSGenerator((new Date()).getTime());
        bbsGenerator.generateRandomBitString(bit_length);
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        System.out.println("Execution time(milliseconds) for BBS: " + timeElapsed / 1000000);
    }

    @Test
    public void maxPeriodTest(){
        var period = lfsrGenerator.getPeriod();

        assertEquals(65535, period);

    }

}