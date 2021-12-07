package linear_road.Redis.lettuce;

import java.util.HashMap;
import java.util.Map;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import linear_road.Redis.MyRedisClient;

public class LettuceStandaloneClient implements MyRedisClient {

    RedisClient redisClient = null;
    StatefulRedisConnection<String, String> connection = null;
    RedisCommands<String, String> sync = null;

    public LettuceStandaloneClient(String host) {
        // Syntax: redis://[password@]host[:port]
        redisClient = RedisClient.create("redis://" + host + ":6379");
        connection = redisClient.connect();

        sync = connection.sync();
        System.out.println("Connected to Redis single node on node " + host);
    }

    public void hmset_accident(String key, int vid) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("accident", String.valueOf(0));
        data.put("involvedCar2", String.valueOf(vid));

        sync.hmset(key, data);
    }

    public void hset_firstStoppedCar(String key, int vid) {
        sync.hset(key, "involvedCar1", Integer.toString(vid));
    }

    public void hmset_clearAccident(String key) {
        Map<String, String> data = new HashMap();
        data.put("accident", String.valueOf(0));
        data.put("involvedCar1", String.valueOf(-1));
        data.put("involvedCar2", String.valueOf(-1));

        sync.hmset(key, data);
    }

    public void hmset_segstats(String key, double avv, int uniqueVehicles) {
        Map<String, String> data = new HashMap();
        data.put("avv", String.valueOf(avv));
        data.put("vehicles", String.valueOf(uniqueVehicles));

        sync.hmset(key, data);
    }

    public Map<String, String>  hmget_segstats(String key) {
        return sync.hgetall(key);
    }

    public void shutdown() {
        if (connection != null) { 
            connection.close();
        }
        if (redisClient != null) {
            redisClient.shutdown();
        }
    }
}
