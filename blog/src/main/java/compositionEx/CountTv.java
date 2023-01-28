package blog.compositionEx;

import java.util.List;

public class CountTv<E> extends ForwardingTv<E> {
    int installNum; // 설치횟수

    public CountTv(TvInterface<E> tv) {
        super(tv);
    }

    @Override
    public void install(E e) {
        installNum++;
        System.out.println(installNum);
        super.install(e);
    }

    @Override
    public void installAll(List<E> list){
        installNum+=list.size();
        System.out.println(installNum);
        super.installAll(list);
    }
}
