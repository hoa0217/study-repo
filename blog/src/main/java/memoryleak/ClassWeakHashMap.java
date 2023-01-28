package blog.memoryleak;

import java.util.Map;
import java.util.WeakHashMap;

public class ClassWeakHashMap {
    public static class Referred {
        protected void finalize() {
            System.out.println("Good bye cruel world");
        }
    }

    /**
     * GC를 발생 시켜 메모리를 회수하는 코드
     * System.gc()가 잘 동작할지는 모르겠다.
     */
    public static void collect() throws InterruptedException {
        System.out.println("Suggesting collection");
        System.gc();
        System.out.println("Sleeping");
        Thread.sleep(5000);
    }

    public static void main(String args[]) throws InterruptedException {
        System.out.println("Creating weak references");

        // This is now a weak reference.
        // The object will be collected only if no strong references.
        Referred strong = new Referred(); //Strong Reference로 하나 추가

        //Weak Reference를 이용한 WeakHashMap에 엔트리를 추가하여
        //Weak Reference 추가
        Map<Referred, String> metadata = new WeakHashMap<>();
        metadata.put(strong, "WeakHashMap's make my world go around");

        // Attempt to claim a suggested reference.
        ClassWeakHashMap.collect();
        //여기서는 gc가 발생해도 GC대상이 아니게 된다.
        //strong이라는 변수를 통해 Strong Reference를 가지므로 GC 대상이 아니다.
        System.out.println("Still has metadata entry? " + (metadata.size() == 1));
        System.out.println("Removing reference");

        // The object may be collected.
        //Strong Reference를 끊었다.
        strong = null;

        //여기서는 Weak Reference만 남아 있기 때문에 GC대상이 된다.
        ClassWeakHashMap.collect();
        System.out.println("Still has metadata entry? " + (metadata.size() == 1));
        System.out.println("Done");
    }
}
