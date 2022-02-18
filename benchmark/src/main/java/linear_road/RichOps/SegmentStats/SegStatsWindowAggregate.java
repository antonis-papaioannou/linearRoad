package linear_road.RichOps.SegmentStats;

import org.apache.flink.api.common.functions.RichAggregateFunction;
import linear_road.Event;
import org.apache.flink.api.java.tuple.Tuple2;
import linear_road.EventTuple;
import linear_road.LRTuple2;

/**
* The accumulator is used to keep a running sum and a count. The {@code getResult} method
* computes the average.
*/
public class SegStatsWindowAggregate extends RichAggregateFunction<EventTuple, SegStatsAccumulator, LRTuple2<Double,Integer>> {
    @Override
	public SegStatsAccumulator createAccumulator() {
        return new SegStatsAccumulator();
    }
   
    @Override
    public SegStatsAccumulator merge(SegStatsAccumulator a, SegStatsAccumulator b) {
        a.count += b.count;
        a.sum += b.sum;
        return a;
    }
   
    @Override
    public SegStatsAccumulator add(EventTuple value, SegStatsAccumulator acc) {
        acc.addSpeed(value.vid(), value.speed());
        return acc;
    }
   
    @Override
    public LRTuple2<Double,Integer> getResult(SegStatsAccumulator acc) {
        return new LRTuple2<>(acc.avgVelocity(), acc.uniqueVehiclesCount());
    }
}
