package blog.extendsEx;

import java.util.ArrayList;
import java.util.List;

public class Tv <E>{
    List<E> softwareList;

    public Tv() {
        softwareList = new ArrayList<>();
    }

    public void install(E e){
        softwareList.add(e);
    }

    public void installAll(List<E> softwareList){
        softwareList.stream().forEach(e -> this.install(e));
    }
}


