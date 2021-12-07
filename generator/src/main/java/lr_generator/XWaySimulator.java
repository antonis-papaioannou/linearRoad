package lr_generator;

import java.util.ArrayList;
import lr_generator.Util.*;

public class XWaySimulator {

    private int numID;
    private ArrayList<Segment> segments = new ArrayList<Segment>();
    private Accident accident = null;
    private int vehiclesNum = 0;


    public XWaySimulator(int numID) {
        this.numID = numID;
        accident = new Accident();
    }

    public void moveCarsInAllSegment(int currentTime) {
        // for all segments
        for (int segIndex = 0; segIndex < segments.size(); segIndex++) {
            // System.out.println(LoggingUtil.pointInCode() + " modify segment " + segIndex);

            //  move cars in the segment
            //  if car crosses then remove car from this segment and add it to the next
            ArrayList<Vehicle> vehicles2delete = new ArrayList<Vehicle>();
            for (Vehicle v : segments.get(segIndex).getVehicles()) {
                boolean crossed = v.move(currentTime);

                if (crossed && (segIndex == GeneratorConfig.SEGMENTS_PER_XWAY-1) ) {
                    // System.out.println(LoggingUtil.pointInCode() + "XWAY: " + numID + " vid " + v.vid() + " crossed last segment (" + segments.get(segIndex).segNum() + ") @ " + currentTime);
                    vehicles2delete.add(v);
                    this.vehiclesNum--;
                    continue;
                }

                if (crossed) {
                    int nextSegment = segIndex + 1;
                    // System.out.println(LoggingUtil.pointInCode() + "XWAY: " + numID + " vid " + v.vid() + " crossed " + segments.get(segIndex).segNum() + " --> " + segments.get(nextSegment).segNum() + " @ " + currentTime);
                    vehicles2delete.add(v);
                    segments.get(nextSegment).addVehicle(v);
                }
            }
            segments.get(segIndex).deleteVehicles(vehicles2delete);
        }
    }

    public void detectAccident(int currentTime) {
        if ( (accident.isActive() == false) && (accident.bothVehiclesStopped()) ) {
            accident.setActive(currentTime);

            String msg = "DETECT " + "Xway " + numID + " segment " + accident.getSegment().segNum() +
                        " time: " + currentTime + 
                        " vehicles " + accident.getVehicle1().vid() + ", " + accident.getVehicle2().vid() +
                        " will clear at " + accident.getFutureClearTime();

            if (GeneratorConfig.PRINT_OUT) {
                System.out.println(LoggingUtil.pointInCode() + msg);
            }
            
            if (GeneratorConfig.PRINT_HINTS_FILE) {
                LRFileWriter hintsWriter = new LRFileWriter(GeneratorConfig.HINTS_FILE);
                hintsWriter.write(msg);
            }
        }
        
    }

    public void clearAccidentCheck(int currentTime) {
        boolean needClear = accident.needClear(currentTime);
        if (needClear == false) {
            return;
        }

        String msg = "CLEAR " + "Xway " + numID + " segment " + accident.getSegment().segNum() +
                    " time: " + currentTime + 
                    " vehicles " + accident.getVehicle1().vid() + ", " + accident.getVehicle2().vid();

        if (GeneratorConfig.PRINT_OUT) {
            System.out.println(LoggingUtil.pointInCode() + msg);
        }

        if (GeneratorConfig.PRINT_HINTS_FILE) {
            LRFileWriter hintsWriter = new LRFileWriter(GeneratorConfig.HINTS_FILE);
            hintsWriter.write(msg);
        }
        accident.clear(currentTime);
    }

    //TODO: Support more than 1 active accidents
    public void prepareAccident(int currentTime) {
        // if we have prepared and accident do nothing
        if ( accident.isPending() || accident.isActive() ) {
            // System.out.println(LoggingUtil.pointInCode() + " already pending/active accident");
            return;
        }
    
        // if no active or pending accident, 20% probability to schedule a new accident
        int accidentProbability = LoggingUtil.getRandomNumberUsingNextInt(0, 101);
        if (accidentProbability > 20) {
            return;
        }
    
        //select a random segment with more than 2 vehicles to "set up" an accident in the future
        int tries = 0;
        APair<Vehicle, Vehicle> involvedVehicles = null;
        Segment seg = null;
        while (tries < segments.size()) {   // do not try to find segment with more than two cars indefinetely
            int randomSegIndex = LoggingUtil.getRandomNumberUsingNextInt(0, segments.size());
            if (segments.get(randomSegIndex).getVehiclesNum() > 2) {
                involvedVehicles = segments.get(randomSegIndex).get2RandomVehicles();
                seg = segments.get(randomSegIndex);
                break;
            }
            tries++;
        }
    
        // mark 2 random vehicles of the segment as potential accident involvies
        if (involvedVehicles != null) {
            String msg = "PENDING " + "XWAY " + numID + " seg " + seg.segNum() + 
                        " vids " + involvedVehicles.getFirst().vid() + 
                        " , " + involvedVehicles.getSecond().vid() + 
                        " currTime: " + currentTime;
            if (GeneratorConfig.PRINT_OUT) {           
                System.out.println(LoggingUtil.pointInCode() + msg);
            }

            accident.setPending(seg, involvedVehicles.getFirst(), involvedVehicles.getSecond());
        } else {    // should be an extremely rare case
            String msg = "WARN PENDING " + "XWAY " + numID + 
                        " could not find segment with more than 2 vehivles to schedule an accident" + 
                        " (currTime " + currentTime + ")";
            System.out.println(LoggingUtil.pointInCode() + msg);

            if (GeneratorConfig.PRINT_HINTS_FILE) {
                LRFileWriter hintsWriter = new LRFileWriter(GeneratorConfig.HINTS_FILE);
                hintsWriter.write(msg);
            }
        }
    }
    
    public void addNewVehicles(int currentTime) {
        int vehiclesNeeded = GeneratorConfig.VEHICLES_PER_XWAY - this.vehiclesNum; //SharedState.TOTAL_VEHICLES;
            
        for (int i = 0; i < vehiclesNeeded; i++) {
            // select random segment to insert new vehicle
            // The segment should be in the first half of the xway (to have more segments ahead to cross in the future)
            int randomSegmentIndex = LoggingUtil.getRandomNumberUsingNextInt(0, GeneratorConfig.SEGMENTS_PER_XWAY);
            segments.get(randomSegmentIndex).addNewVehicle(currentTime);
            // System.out.println(LoggingUtil.pointInCode() + "XWAY " + numID + " new vehicle into segment " + segments.get(randomSegmentIndex).segNum());
        }

        vehiclesNum += vehiclesNeeded;
    }


    // return the number of position reports = number of vehicles in the xways (including the accident cars)
    public void printAllPositionReports(int currentTime) {
        LRFileWriter dataWriter = null;
        if (GeneratorConfig.PRINT_DATAFILE) {
            dataWriter = new LRFileWriter(GeneratorConfig.DATA_FILE);
        }

        for (Segment seg : segments) {
            boolean segHasAccident = false;
            if ( accident.isActive() && (accident.getSegment() == seg) ) {
                segHasAccident = true;
            } 

            // System.out.println(LoggingUtil.pointInCode() + "XWAY " + numID + 
            //                 " segment = " + seg.segNum() + " time = " + currentTime + 
            //                 " vehicles: " + seg.getVehiclesNum() + " accident: " + segHasAccident);
            for (Vehicle v : seg.getVehicles()) {
                if (v.isInAccident()) {
                    // System.out.println("Ignore: " + v.toString());
                    continue;
                }
                // System.out.println(v.toString());
                if (GeneratorConfig.PRINT_DATAFILE) {
                    dataWriter.write_unsafe(v.toString());
                }

                // if (GeneratorConfig.PRINT_OUT) {
                //     System.out.println(v.toString());
                // }

                if (GeneratorConfig.SIMULATE == false) {
                    SharedState.KAFKA.insertRecord(v.vid(), v.toString());
                }
                
                SharedState.TOTAL_POSITION_REPORTS++;
            }
        }

        if (GeneratorConfig.PRINT_DATAFILE) {
            dataWriter.close();
        }
    }

    public void initSegments() {
        for (int i = 0; i < GeneratorConfig.SEGMENTS_PER_XWAY; i++) {
            Segment seg = new Segment(i+1, this.numID);
            int vehiclesAdded = seg.addRandomNumberNewVehicles(0); //before simulation starts, time is -1
            segments.add(seg);
            vehiclesNum += vehiclesAdded;
        }
    }

    public int getVehiclesNum() {
        return vehiclesNum;
    }
    
}
