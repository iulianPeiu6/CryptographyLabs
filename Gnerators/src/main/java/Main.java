import generators.BBSGenerator;

import java.util.Date;
import java.util.Random;

public class Main {
    public static void main(String[] args){
        BBSGenerator bbsGenerator = new BBSGenerator((new Date()).getTime());

        System.out.println(bbsGenerator.getSeed());
        System.out.println(bbsGenerator.getFirstFactor());

    }
}
