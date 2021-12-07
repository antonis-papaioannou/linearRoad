package lr_generator;

import lr_generator.Util.LoggingUtil;

public class Accident {

    private int detected_time = -1;
    private int future_clear_time = -1;    // the time in the future we will clear the accident
    private boolean pending = false;
    private boolean active = false;
    private Segment seg = null;
    private Vehicle v1 = null;
    private Vehicle v2 = null;

    public Accident() { }

    public void setPending(Segment seg, Vehicle v1, Vehicle v2) {
        this.seg = seg;
        this.v1 = v1;
        this.v2 = v2;

        v1.setFutureAccident();
        v2.setFutureAccident();
        pending = true;

        // System.out.println(LoggingUtil.pointInCode() + " pending accident of " + this.v1.vid() + " and " + this.v2.vid());
    }

    public void setActive(int currentTime) {
        active = true;
        pending = false;
        detected_time = currentTime;
        v1.setInAccident();
        v2.setInAccident();

        // select a random time in the next 2 to 5 minutes in the future
        int randomAccidentDuration = LoggingUtil.getRandomNumberUsingNextInt(600, 1200);
        future_clear_time = detected_time + randomAccidentDuration;

        // System.out.println(LoggingUtil.pointInCode() + " detected accident at " + currentTime + " between " + v1.vid() + " and " + v2.vid() + " until " + future_clear_time);
    }

    public boolean needClear(int currentTime) {
        if ( (future_clear_time > currentTime) || (future_clear_time == -1) ) {
            // System.out.println(LoggingUtil.pointInCode() + " not time to clean accident");
            return false;
        }
        return true;
    }

    // return true if accident cleared, otherwise false
    public boolean clear(int currentTime) {
        if (needClear(currentTime) == false) {
            return false;
        }

        // System.out.println(LoggingUtil.pointInCode() + "clean accident of " + v1.vid() + " and " + v2.vid());
        v1.cleanAccident();
        v2.cleanAccident();
        detected_time = -1;
        future_clear_time = -1;
        pending = false;
        active = false;
        seg = null;
        v1 = null;
        v2 = null;

        return true;
    }

    public boolean bothVehiclesStopped() {
        if ( (v1 == null) || (v2 == null) ) {
            return false;
        }
        return (v1.isStopped() && v2.isStopped());
    }

    public boolean isPending() {
        return this.pending;
    }

    public boolean isActive() {
        return this.active;
    }

    public Vehicle getVehicle1() {
        return this.v1;
    }

    public Vehicle getVehicle2() {
        return this.v2;
    }

    public Segment getSegment() {
        return this.seg;
    }

    public int getFutureClearTime() {
        return this.future_clear_time;
    }
    
}
