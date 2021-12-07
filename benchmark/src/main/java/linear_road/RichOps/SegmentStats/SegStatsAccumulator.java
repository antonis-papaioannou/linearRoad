package linear_road.RichOps.SegmentStats;

import java.util.Set;
import java.util.HashSet;

public class SegStatsAccumulator {
    public long count;
    public long sum;
    private Set<Integer> uniqueVid; //contains unique vehicle ids (integer ids)

    public SegStatsAccumulator() { 
        uniqueVid = new HashSet<Integer>();
    }

    public void addSpeed(int vid, int speed) {
        sum += speed;
        count++;
        uniqueVid.add(vid);
    }

    public double avgVelocity() {
        return sum / (double) count;
    }

    public int uniqueVehiclesCount() {
        return this.uniqueVid.size();
    }

    public String toString() {
        return "(" + this.sum + "," + this.count + ")";
    }
}