package linear_road.RichOps;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;

import linear_road.Event;
import linear_road.BenchmarkConfig;
import linear_road.Redis.MyRedisClient;
import linear_road.Redis.lettuce.*;
import linear_road.Util.LoggingUtil;
import linear_road.Util.PerformanceReporter;
import linear_road.EventTuple;

import java.util.Map;

public class AccidentManager extends RichMapFunction<EventTuple, EventTuple> {
    private String opName = "AccManager";
    private String redisHost;
    private boolean redisClusterMode;
    private MyRedisClient redis = null;
    private boolean monitorOp;
    private PerformanceReporter performanceRepoerter = null;

    public AccidentManager(BenchmarkConfig config) { 
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
            performanceRepoerter = new PerformanceReporter(opName);
        }
    }

    @Override
    public EventTuple map(EventTuple value) throws Exception {
        long start_ts_nano = System.nanoTime();

        int accident = 0;
        int involvedCar1 = -1;
        int involvedCar2 = -1;

        if (value.isStopped() || value.cleared) {
            Map<String, String> accidentData = redis.hmget_segstats(value.segID());
            accident = Integer.parseInt(accidentData.getOrDefault("accident", "0"));
            involvedCar1 = Integer.parseInt(accidentData.getOrDefault("involvedCar1", "-1"));
            involvedCar2 = Integer.parseInt(accidentData.getOrDefault("involvedCar2", "-1"));
        }

        // accident dectected
        if (value.isStopped) {
            // System.out.println(LoggingUtil.pointInCode() + " Stopped " + " vid " + value.vid + " currTime: " + value.time + " samePosition " + value.samePositionCounter);

            String key = value.segID();

            if (involvedCar1 == -1) { // set the 1st stopped car of the segment (a 2nd stopped car will result in accident in the future)
                redis.hset_firstStoppedCar(key, value.vid());
                // System.out.println(LoggingUtil.pointInCode() + " first involved vid " + value.vid + " seg " + value.segID + " currTime: " + value.time);
            } else {    // this is the second stopped car detected = accident
                if (value.vid() == involvedCar1) { //sanity check
                    System.out.println(LoggingUtil.pointInCode() + " WARN vid " + value.vid() + " (involved1 " + involvedCar1 + ") already stopped at seg " + value.segID() + " currTime: " + value.time() + " samePosition " + value.samePositionCounter);
                } else {
                    long emit = System.currentTimeMillis() - value.ingestTime;
                    System.out.println(LoggingUtil.timestamp() + " Accident detected: " + " emit = " + emit + " vid1 = " + involvedCar1 + " v2 " + value.vid() + " time: " + value.time() + " seg " + value.segID() + " AccidentManager");
                    redis.hmset_accident(key, value.vid());
                }
            }
        }

        if ( (value.cleared) && (accident != 0) ) {
            // System.out.println(LoggingUtil.pointInCode() + " Cleared vid " + value.vid + " currTime: " + value.time + " samePosition " + value.samePositionCounter + " seg " + value.segID());
            //clear accident on the previous position (we detected that the vehicle has been moved from the accident position)
            String key = value.segID();
            redis.hmset_clearAccident(key);
            long emit = System.currentTimeMillis() - value.ingestTime;
            System.out.println(LoggingUtil.timestamp() + " Accident cleared: "+ " emit = " + emit + " vid = " + value.vid() + " time: " + value.time());
        }

        if (monitorOp) {
            long now_nano = System.nanoTime();
            long db_latency = now_nano - start_ts_nano;
            performanceRepoerter.addLatency(db_latency, (System.currentTimeMillis() - value.ingestTime) );
        }

        return value;
    }

    @Override
    public void close() {
        if (redis != null) {
            redis.shutdown();
        }
    }

}
