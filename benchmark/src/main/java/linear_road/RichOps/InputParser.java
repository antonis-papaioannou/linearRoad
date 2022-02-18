package linear_road.RichOps;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

// import linear_road.Event;
import linear_road.EventTuple;

public class InputParser extends RichFlatMapFunction<String, EventTuple> {

    @Override
    public void open(Configuration config) { }

    @Override
    public void flatMap(String input, Collector<EventTuple> out) throws Exception {
        if ( (input == null) || (input.isEmpty()) ) {
            return;
        }
        EventTuple e = parseFromString(input);

        out.collect(e);
    }


    private EventTuple parseFromString(String s) {
        String[] arr = s.split(",");

        int type = Integer.parseInt(arr[0]);
        short time = Short.parseShort(arr[1]);
        int vid = Integer.parseInt(arr[2]);
        int speed = Integer.parseInt(arr[3]);
        int xway = (Integer.parseInt(arr[4]));
        int lane = (Integer.parseInt(arr[5]));
        int direction = (Integer.parseInt(arr[6]));
        int segment = (Integer.parseInt(arr[7]));
        int position = (Integer.parseInt(arr[8]));
        String qid = (arr[9]);
        int day = (Integer.parseInt(arr[14]));
        int minute = (time / 60 + 1); 

        EventTuple e = new EventTuple(type, time, vid, speed, xway, lane, direction, segment, position, qid, day, minute);
        e.ingestTime = System.currentTimeMillis();
        return e;
      }
 }