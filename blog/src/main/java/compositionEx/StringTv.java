package blog.compositionEx;

import java.util.List;

public class StringTv<E> extends ForwardingTv<E>{

    public StringTv(TvInterface<E> tv) {
        super(tv);
    }

    @Override
    public void install(E e) {
        System.out.println("SW 설치");
        super.install(e);
    }

    @Override
    public void installAll(List<E> list){
        System.out.println("SW 전체 설치");
        super.installAll(list);
    }
}
