package blog.extendsEx;

import java.util.List;

public class CountTv<E> extends Tv<E>{
    int installNum; // 설치횟수

    @Override
    public void install(E e) {
        installNum++;
        super.install(e);
    }

    @Override
    public void installAll(List<E> list){
        installNum+=list.size();
        super.installAll(list);
    }

    public static void main(String[] args) {
        CountTv<Integer> tv = new CountTv();
        tv.install(1);
        System.out.println(tv.installNum); // 1

        tv.installAll(List.of(2, 3));
        System.out.println(tv.installNum); // 5
    }
}
