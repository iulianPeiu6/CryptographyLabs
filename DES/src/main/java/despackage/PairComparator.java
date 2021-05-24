package despackage;

import java.util.Comparator;

public class PairComparator implements Comparator<Pair> {

    @Override
    public int compare(Pair p1, Pair p2) {


        StringBuilder p1BitString= new StringBuilder(),
                p2BitString = new StringBuilder();

        for (var bit : p1.value1)
            p1BitString.append(bit? "1":"0");

        for (var bit : p2.value1)
            p2BitString.append(bit? "1":"0");

        return (new String(p1BitString).compareTo(new String(p2BitString)));
    }
}
