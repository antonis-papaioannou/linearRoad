package linear_road;

import org.apache.flink.api.java.tuple.Tuple8;
import java.util.Objects;


public class VehicleTuple extends Tuple8<Integer,Integer,Integer,Integer,Integer,Integer,Integer,Boolean>{

    public VehicleTuple(int vid, int reports, int latestXWay, int latestLane, int latestDir, 
            int latestSeg, int latestPos, boolean uniquePosition) {
        super(vid, reports, latestXWay, latestLane, latestDir, latestSeg, latestPos, uniquePosition);
    }
    public int getVid() {
        return super.f0;
      }
    
      public int getReports() {
        return super.f1;
      }
    
      public int getLatestXWay() {
        return super.f2;
      }
    
      public int getLatestLane() {
        return super.f3;
      }
    
      public int getLatestDir() {
        return super.f4;
      }
    
      public int getLatestSeg() {
        return super.f5;
      }
    
      public int getLatestPos() {
        return super.f6;
      }
    
      public boolean isUniquePosition() {
        return super.f7;
      }

}
