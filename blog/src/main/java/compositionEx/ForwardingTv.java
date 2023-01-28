package blog.compositionEx;

import java.util.List;

public class ForwardingTv<E> implements TvInterface<E>{
    private final TvInterface<E> tv;

    public ForwardingTv(TvInterface<E> tv){
        this.tv = tv;
    }

    public void install(E e){
        tv.install(e);
    }

    public void installAll(List<E> eList){
        tv.installAll(eList);
    }
}
