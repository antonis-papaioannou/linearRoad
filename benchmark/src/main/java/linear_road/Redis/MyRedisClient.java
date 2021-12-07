package linear_road.Redis;

import java.util.Map;

public interface MyRedisClient {

    // HASH data structure for segment 
    /*
    * The caller detected a stopped car (indicates a possible accident if a 2nd stopped car will be detected) 
    */
    public void hset_firstStoppedCar(String key, int vid); 

    /*
    * The caller has detected a second stopped car --> this indicates an accident in the segment
    */
    public void hmset_accident(String key, int vid);

    public void hmset_clearAccident(String key);
    
    public void hmset_segstats(String key, double avv, int uniqueVehicles);
    public Map<String, String> hmget_segstats(String key);

    public void shutdown();

}