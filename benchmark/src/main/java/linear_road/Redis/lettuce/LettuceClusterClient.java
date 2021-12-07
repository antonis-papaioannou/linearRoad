package linear_road.Redis.lettuce;

import java.util.HashMap;
import java.util.Map;

import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import linear_road.Redis.MyRedisClient;

public class LettuceClusterClient implements MyRedisClient {

    RedisClusterClient redCluster = null;
    StatefulRedisClusterConnection<String, String> connection = null;
    RedisAdvancedClusterCommands<String, String> sync = null;

    public LettuceClusterClient(String host) {
        // Syntax: redis://[password@]host[:port]
        redCluster = RedisClusterClient.create("redis://" + host + ":6379");
        connection = redCluster.connect();

        System.out.println("Connected to Redis cluster on node " + host);

        sync = connection.sync();   // synchronus api
    }
    
    public void hmset_accident(String key, int vid) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("accident", String.valueOf(1));
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

    public Map<String, String> hmget_segstats(String key) {
        return sync.hgetall(key);
    }

    public void shutdown() {
        if (connection != null) {
            connection.close();
        }
        if (redCluster != null) {
            redCluster.shutdown();
        }
    }

    public long key2Slot(String key) {
        return sync.clusterKeyslot(key);
    }
    
}
