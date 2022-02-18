package linear_road;

import org.apache.flink.api.java.tuple.Tuple12;
import java.util.Objects;

public class EventTuple extends Tuple12<Integer, Short, Integer, Integer, Integer, Integer, Integer, Integer, Integer, String, Integer, Integer>{

    public String segID;
    public Long ingestTime = -1L;
    public Boolean isCrossing = Boolean.FALSE;
    public Boolean isStopped = Boolean.FALSE;
    public Integer samePositionCounter = 1;
    public boolean cleared = false;
    
    public EventTuple(int type, short time, int vid, int speed, int xway, int lane,
    int direction, int segment, int position, String qid,
    int day, int minute) {
        super(type, time, vid, speed, xway, lane, direction, segment, position, qid, day, minute);
        this.segID = xway + "_" + segment;
    }

    public void setCleared(Boolean status){
        this.cleared = status;
    }
    
    public int getType() { 
        return this.f0; 
    }

    public short time() {
        return this.f1;
    }
  
    public int getVid() {
        return this.f2;
    }

    public int vid() {
        return this.f2;
    }

    public int speed() {
        return this.f3;
    }

    public int xway() {
        return this.f4;
    }
  
    public int lane() {
        return this.f5;
    }

    public int direction() {
        return this.f6;
    }

    public int segment() {
        return this.f7;
    }

    public int position() {
        return this.f8;
    }

    public String qid() {
        return this.f9;
    }

    public int day() {
        return this.f10;
    }

    public int minute() {
        return this.f11;
    }

    public boolean isStopped() {
        return this.isStopped;
    }
  
    public boolean isCrossing() {
        return isCrossing;
    }
  
    public String segID() {
          return this.segID;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %d, %d, %d, %d, %d, %d, %d)", 
                f0, f1, f2, f3, f4, f5, f6, f7, f8);
    }

    
}
