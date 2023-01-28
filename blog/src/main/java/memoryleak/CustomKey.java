package blog.memoryleak;

import java.util.HashMap;
import java.util.Map;

public class CustomKey {
    public CustomKey(String name){
        this.name=name;
    }
    private String name;
    public static void main(String[] args) {
        Map<CustomKey,String> map = new HashMap<CustomKey,String>();
        map.put(new CustomKey("Shamik"), "Shamik Mitra");
        String val = map.get(new CustomKey("Shamik"));
        System.out.println("Missing equals and hascode so value is not accessible from Map " + val);
    }
}
