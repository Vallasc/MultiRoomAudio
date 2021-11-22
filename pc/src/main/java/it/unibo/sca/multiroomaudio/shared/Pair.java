package it.unibo.sca.multiroomaudio.shared;

public class Pair<T1, T2> {

    private final T1 u;
    private final T2 v;

    public Pair(T1 u, T2 v) {
        this.u = u;
        this.v = v;
    }

    public T1 getU(){
        return this.u;
    }

    public T2 getV(){
        return v;
    }
}
