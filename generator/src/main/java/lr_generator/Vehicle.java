package lr_generator;

import lr_generator.Util.LoggingUtil;

public class Vehicle {

    private int qid = -1, sinit = -1, send = -1;
    private int dow = -1, tod = -1, day = -1;
    private int type = 0;   // position report

    private int vid = -1;
    private int xway = -1;
    private int direction;// = 1;
    private int currentSegment, currentPosition;
    private int lane = 2;
    private int speed;
    private int currentTime = -1;

    // If the vehicle is marked to be involved in an accident in the future 
    // (will report the same position report from now on)
    private boolean futureAccident = false;

    private boolean isInAccident = false;

    private int samePositionReports = 1;
    private boolean stopped = false;

    public Vehicle(int vid, int segmentNum, int xway, int currentTime) {
        this.vid = vid;
        this.xway = xway;
        this.currentSegment = segmentNum;
        this.currentPosition = segmentNum*10;
        this.direction = LoggingUtil.getRandomNumberUsingNextInt(0, 2); //2 is exclusive
        this.speed = LoggingUtil.getRandomNumberUsingNextInt(3, 16) * 10;
        this.currentTime = currentTime;
    }

    // return true if crossed segment, else false
    public boolean move(int time) {

        if (stopped) {
            // System.out.println(LoggingUtil.pointInCode() + " Vehicle " + vid + " is stopped. No move!");
            samePositionReports = 1;
            return false;
        }

        // if already moved the vehicle in this time interval (e.g. just crossed to current segment)
        if (this.currentTime == time) {
            return false;
        }

        this.currentTime = time;

        // report the same position again
        if (futureAccident) {
            samePositionReports++;
            // System.out.println(LoggingUtil.pointInCode() + " vid " + vid + " samePositionReports " + samePositionReports + " time " + time);
            if (samePositionReports == 4) {
                stopped = true;
            }

            //sanity check
            if (samePositionReports > 4) {
                throw new RuntimeException(LoggingUtil.pointInCode() + " Same position report is > 4");
            }

            return false;
        }

        int crossSegmentProbability = LoggingUtil.getRandomNumberUsingNextInt(0, 101);
        if (crossSegmentProbability < 10) {
            this.currentSegment++;
            this.currentPosition = this.currentSegment * 10;
            this.speed = LoggingUtil.getRandomNumberUsingNextInt(3, 16) * 10;
            return true;
        }

        this.currentPosition += 10;
        return false;
    }

    public void setFutureAccident() {
        this.futureAccident = true;
    }

    public void setInAccident() {
        this.isInAccident = true;
        this.futureAccident = false;
    }

    public boolean isInAccident() {
        return this.isInAccident;
    }

    public void cleanAccident() {
        this.isInAccident = false;
        this.stopped = false;
    }

    public int vid() {
        return this.vid;
    }

    public boolean isStopped() {
        return stopped;
    }

    public String toString() {
        String ret = type + "," + currentTime + "," + vid + "," + speed + "," + xway + 
                "," + lane + "," + direction + "," + currentSegment + "," + currentPosition + 
                "," + qid + "," + sinit + "," + send + "," + dow + "," + tod +
                "," + day;
        return ret;
    }
    
}
