package linear_road;

import org.apache.flink.api.java.tuple.Tuple3;

public class LRTuple3<S, D, I> extends Tuple3<String, Double, Integer>{

    public LRTuple3(String value0, Double value1, Integer value2) {
        super(value0, value1, value2);
    }
    
}
