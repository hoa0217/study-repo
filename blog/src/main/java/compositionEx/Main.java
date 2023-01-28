package blog.compositionEx;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        CountTv<Integer> countTv = new CountTv(new Tv<>());
        countTv.install(1);
        countTv.installAll(List.of(2, 3));

        countTv = new CountTv(new StringTv(new Tv<>()));
        countTv.install(1);
        countTv.installAll(List.of(2, 3));
    }
}
