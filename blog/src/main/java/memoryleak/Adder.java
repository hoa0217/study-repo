package blog.memoryleak;

public class Adder {
    public long add(long l){
        Long sum = 0L;
        sum += l;
        return sum;
    }
    public static void main(String[] args) {
        Adder adder = new Adder();
        for (int i = 0; i < 1000; i++) {
            adder.add(i);
        }
    }
}
