package blog.compositionEx;

import java.util.List;

public interface TvInterface<E>{
    void install(E e);
    void installAll(List<E> softwareList);
}
