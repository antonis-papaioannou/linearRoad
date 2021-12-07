package linear_road;

/**
 * POJO holding input events.
 */
public class Event {

public Integer type;
public short time;
public int vid;
public Integer speed;
public Integer xway;
public Integer lane;
public Integer direction;
public int segment;
public Integer position;
public String qid;
public Integer day;
public Integer minute; 
public String segID;

public Long ingestTime = -1L;
public Boolean isCrossing = Boolean.FALSE;
public Boolean isStopped = Boolean.FALSE;
public Integer samePositionCounter = 1;

public boolean cleared = false;

public Event() { }

public Event(int type, short time, int vid, int speed, int xway, int lane,
                        int direction, int segment, int position, String qid,
                        int day, int minute) {
    this.type = type;
    this.time = time;
    this.vid = vid;
    this.speed = speed;
    this.xway = xway;
    this.lane = lane;
    this.direction = direction;
    this.segment = segment;
    this.position = position;
    this.qid = qid;
    this.day = day;
    this.minute = minute;
    this.segID = xway + "_" + segment;
}

  public int getType() { 
      return this.type; 
  }

  public int getVid() {
      return this.vid;
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

  public String toString() {
      return String.format("(%d, %d, %d, %d, %d, %d, %d, %d, %d)", 
              type, time, vid, speed, xway, lane, direction, segment, position);
  }

}
