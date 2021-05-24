package rc4;

import java.util.List;

public class State {
    int i;
    int j;
    List<Integer> permutation;

    public State(int i, int j, List<Integer> permutation) {
        this.i = i;
        this.j = j;
        this.permutation = permutation;
    }

    @Override
    public String toString() {
        return "State{" +
                "i=" + i +
                ", j=" + j +
                ", permutation=" + permutation +
                '}';
    }
}
