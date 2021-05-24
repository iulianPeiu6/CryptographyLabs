package despackage;

import java.util.List;

public class Pair {

    Boolean[] value0;
    List<Boolean> value1;

    Boolean[] first,second;

    public Pair(Boolean[] value0, List<Boolean> value1) {
        this.value0 = value0;
        this.value1 = value1;
    }

    public Pair(Boolean[] value0, Boolean[] value1) {
        this.first = value0;
        this.second = value1;
    }
}
