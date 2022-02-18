package linear_road.RichOps;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;

import java.util.HashMap;

import linear_road.EventTuple;
import linear_road.Util.LoggingUtil;
import linear_road.Util.PerformanceReporter;
import linear_road.BenchmarkConfig;

/**
 *
 */
public class VehicleStateMemory extends RichMapFunction<EventTuple, EventTuple> {

    private HashMap<Integer, EventTuple> previousPositionReport;
    private PerformanceReporter performanceReporter = null;
    private boolean monitorOp;
    
    public VehicleStateMemory(BenchmarkConfig config) {
        this.monitorOp = config.monitorOps;
    }

    @Override
    public void open(Configuration config) {
        previousPositionReport = new HashMap<Integer, EventTuple>();
        if (monitorOp) {
            performanceReporter = new PerformanceReporter("VState");
        }
    }

    @Override
    public EventTuple map(EventTuple value) throws Exception {
        boolean debug = false;
        long start_ts_nano = System.nanoTime();

        EventTuple e = previousPositionReport.get(value.getVid());
            
        // New Vehicle (not seen before)
        if (e == null) {
            value.isCrossing = true;
            previousPositionReport.put(value.getVid(), value);
            return value;
        }

        boolean moving = true;
        if ( (e.segment() != value.segment()) || (e.lane() == 4) ) { // if the car exited after last position report
            value.isCrossing = true;
        } else {
            if ((e.xway() == value.xway()) && (e.lane() == value.lane()) &&
                (e.segment() == value.segment()) && (e.position() == value.position())) {
                e.samePositionCounter++;
                value.samePositionCounter = e.samePositionCounter;
                moving = false;

                if (debug) {
                    System.out.println(LoggingUtil.pointInCode() + " vid " + value.vid() + " samePositionCounter " + e.samePositionCounter  + " tuple " + value.toString());
                }
            }

            if (value.samePositionCounter >= 4) {
                value.isStopped = true;
                // System.out.println("Accident: " + value.toString());

                if (debug) {
                    System.out.println(LoggingUtil.pointInCode() + " vid " + value.vid() + 
                                    " stopped pos (" + value.xway() + "," + value.segment() + ") " +
                                    "samePosition " + value.samePositionCounter + 
                                    " currTime " + value.time());
                }
            }
        }

        // The vehicle was stopped and just started moving
        if ( (e.isStopped()) && (moving) ) {
            e.samePositionCounter = 0;
            value.setCleared(true);
            // value.cleared = true;

            if (debug) {
                System.out.println(LoggingUtil.pointInCode() + 
                                " Restart vid " + value.vid() + 
                                " pos (" + value.xway() + "," + value.segment() + ")" + 
                                " currTime " + value.time());
            }
        }

        value.samePositionCounter = e.samePositionCounter; 
        previousPositionReport.put(value.getVid(), value);

        if (monitorOp) {
            long now_nano = System.nanoTime();
            long now_ms = System.currentTimeMillis();
            performanceReporter.addLatency(now_nano - start_ts_nano, now_ms - value.ingestTime);
        }

        return value;
    }
}

