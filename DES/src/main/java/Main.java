import despackage.DESCipher;
import despackage.MITMAttack;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args){

        DESCipher desCipher = new DESCipher(getAPlainText(), getAKey());

        System.out.println(desCipher.encrypt());
        System.out.println(desCipher.decrypt());

    }

    private static List<Boolean> getAPlainText(){

        return  Arrays.asList(false, false, false, false, false, false, false, true,
                false, false, true, false, false, false, true, true,
                false, true, false, false, false, true, false, true,
                false, true, true, false, false, true, true, true,
                true, false, false, false, true, false, false, true,
                true, false, true, false, true, false, true, true,
                true, true, false, false, true, true, false, true,
                true, true, true, false, true, true, true, true);
    }

    private static List<Boolean> getAKey(){

        //0001 001 0011 010 0101 011 0111 100 1001101 1011 110 1101 111 1111 000
        return  Arrays.asList(false,false,false,true, false,false,true,true,
                false,false,true,true, false,true,false,false,
                false,true,false,true, false,true,true,true,
                false,true,true,true, true,false,false,true,
                true,false,false,true, true,false,true,true,
                true,false,true,true, true,true,false,false,
                true,true,false,true, true,true,true,true,
                true,true,true,true, false,false,false,true);
    }

}
