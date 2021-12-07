package linear_road.RichOps.SegmentStats;

import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;

public class SegStatsProcessWindow extends ProcessWindowFunction<Tuple2<Double,Integer>, Tuple3<String, Double, Integer>, String, TimeWindow> {

    public void process(String key,
                        Context context,
                        Iterable<Tuple2<Double,Integer>> averages,
                        Collector<Tuple3<String, Double, Integer>> out) {
        Tuple2<Double, Integer> result = averages.iterator().next();
        double average = result.f0;
        int unique = result.f1;
        out.collect(new Tuple3<>(key, average, unique));
    }
}