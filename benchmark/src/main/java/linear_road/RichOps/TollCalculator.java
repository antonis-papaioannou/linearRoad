package linear_road.RichOps;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.configuration.Configuration;

import linear_road.EventTuple;
import linear_road.BenchmarkConfig;
import linear_road.Redis.MyRedisClient;
import linear_road.Redis.lettuce.*;
import linear_road.Util.PerformanceReporter;

import java.util.Map;

public class TollCalculator extends RichFlatMapFunction<EventTuple, EventTuple> {

    private String opName = "Toll";
    private String redisHost;
    private boolean redisClusterMode;
    private MyRedisClient redis = null;
    private PerformanceReporter performanceReporter = null;
    private boolean monitorOp;

    public TollCalculator(BenchmarkConfig config) { 
        redisHost = config.redisHost;
        redisClusterMode = config.redisClusterMode;
        this.monitorOp = config.monitorOps;
    }

    @Override
    public void open(Configuration config) { 
        if (redisClusterMode) {
            redis = new LettuceClusterClient(redisHost);
        } else {
            redis = new LettuceStandaloneClient(redisHost);
        }

        if (monitorOp) {
            performanceReporter = new PerformanceReporter(opName);
        }
    }

    @Override
    public void flatMap(EventTuple value, Collector<EventTuple> out) throws Exception {
        long start_ts_nano = System.nanoTime();

        String key = value.segID();
        
        Map<String, String> segStats = redis.hmget_segstats(key);

        int accident = Integer.parseInt(segStats.getOrDefault("accident", "0"));

        double toll = 0;
        if (accident != 0) {
            //lav: latest average velocity
            double lav = Double.parseDouble(segStats.getOrDefault("avv", "-1.0"));
            int vehicles = Integer.parseInt(segStats.getOrDefault("vehicles", "-1"));
            if ( (lav >= 80) || (vehicles <= 50) ) {
                toll = 0;
            } else {
                toll = 2 * (Math.pow((vehicles-50), 2));
            }
        } else {
            toll = 0;
        }

        if (monitorOp) {
            long now_nano = System.nanoTime();
            long now_ms = System.currentTimeMillis();
            long emit = now_ms - value.ingestTime;
            long db_latency = now_nano - start_ts_nano;
            performanceReporter.addLatency(db_latency, emit);
            // System.out.println(LoggingUtil.timestamp() + " " + key + " Toll: " + toll + " latency = " + emit);
        }
        // System.out.println(LoggingUtil.timestamp() + " " + key + " Toll: " + toll);
    }

    @Override
    public void close() { 
        if (redis != null) {
            redis.shutdown();
        }
    }
    
}
