package blog.memoryleak;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
@Getter
@Setter
@ToString
public class MutableCustomKey {
    private String name;

    public MutableCustomKey(String name) {
        this.name = name;
    }

    public static void main(String[] args) {
        MutableCustomKey key = new MutableCustomKey("key");

        Map<MutableCustomKey, String> map = new HashMap<>();
        map.put(key, new String("value"));
        String val = map.get(key);
        System.out.println("Value Found " + val);
        val.replace('v','b');

        String val1 = map.get(key);
        System.out.println("Due to MutableKey value not found " + val1);
        System.out.println(map);
    }
}
