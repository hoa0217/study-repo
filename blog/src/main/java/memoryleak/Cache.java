package blog.memoryleak;

import java.util.HashMap;
import java.util.Map;

public class Cache {
    private Map<String, String> map = new HashMap<>();
    public void initCache(){
        map.put("Anil", "Work as Engineer");
        map.put("Shamik", "Work as Java Engineer");
        map.put("Ram", "Work as Doctor");
    }
    public Map<String,String> getCache(){
        return map;
    }
    public void forEachDisplay(){
        for(String key : map.keySet()){
            String val = map.get(key);
            System.out.println(key + " :: "+ val);
        }
    }
    public static void main(String[] args) {
        Cache cache = new Cache();
        cache.initCache();
        cache.forEachDisplay();
    }
}
