package bgu.spl.mics;

public class ObjectsTres <T, E, K>{
    private T first;
    private  E second;
    private K third;

    public ObjectsTres(T first, E second, K third){
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T getFirst() {
        return  first;
    }

    public E getSecond() {
        return second;
    }

    public K getThird() { return third; }
}
