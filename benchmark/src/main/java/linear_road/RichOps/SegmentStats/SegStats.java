package linear_road.RichOps.SegmentStats;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple3;

import linear_road.Redis.MyRedisClient;
import linear_road.Redis.lettuce.*;
import linear_road.BenchmarkConfig;

/**
 *
 */
public class SegStats extends RichFlatMapFunction<Tuple3<String,Double, Integer>, Double> {

    private String redisHost;
    private boolean redisClusterMode;
    private MyRedisClient redis = null;

    public SegStats(BenchmarkConfig config) { 
        redisHost = config.redisHost;
        redisClusterMode = config.redisClusterMode;
        // System.out.println(LoggingUtil.pointInCode() + " redis cluste mode: " + redisClusterMode);
    }

    @Override
    public void open(Configuration config) { 
        if (redisClusterMode) {
            redis = new LettuceClusterClient(redisHost);
        } else {
            redis = new LettuceStandaloneClient(redisHost);
        }
    }


    @Override
    //Tuple3: f0: segID (key), f1: avg velocity, f2: unique vehicles
    public void flatMap(Tuple3<String,Double, Integer> value, Collector<Double> out) throws Exception {
        redis.hmset_segstats(value.f0, value.f1, value.f2);
        // System.out.println(LoggingUtil.timestamp() + " SegStats: " + value.f0 + " " + value.f1 + " " + value.f2);
        // out.collect(value.f1);
    }
}

