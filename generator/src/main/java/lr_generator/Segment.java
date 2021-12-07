package lr_generator;

import lr_generator.Vehicle;
import lr_generator.Util.LoggingUtil;
import lr_generator.Util.APair;
import lr_generator.SharedState;

import java.util.ArrayList;

public class Segment {
    private int segNum = -1;
    private int xway = -1;  // the xway this segment belongs
    private ArrayList<Vehicle> vehicles;

    public Segment(int id, int xway) { 
        this.segNum = id;
        this.xway = xway;
        vehicles = new ArrayList<Vehicle>();
    }

    public int segNum() {
        return segNum;
    }

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public int getVehiclesNum() {
        return vehicles.size();
    }

    // used to add existing vehicles that have crossed to this segment
    public void addVehicle(Vehicle v) { 
        this.vehicles.add(v);
        // SharedState.TOTAL_VEHICLES++;
    }

    // add a new vehicle that first enters the xway from this segment
    public void addNewVehicle(int currentTime) {
        vehicles.add(new Vehicle(SharedState.CURRENT_MAX_VID++, segNum, this.xway, currentTime));
        // SharedState.TOTAL_VEHICLES++;
    }

    // return the number of vehicles added
    public int addRandomNumberNewVehicles(int currentTime) {
        int idealVehiclesPerSegment = GeneratorConfig.VEHICLES_PER_XWAY/GeneratorConfig.SEGMENTS_PER_XWAY;
        // at least 2 vehicles
        idealVehiclesPerSegment = Math.min(2, idealVehiclesPerSegment);
        int randomNumOfVehicles = LoggingUtil.getRandomNumberUsingNextInt((idealVehiclesPerSegment/2), idealVehiclesPerSegment);
        for (int i = 0; i < randomNumOfVehicles; i++) {
            //When first initialize the segment before the simulation begins, the current time for all vehicles is -1
            vehicles.add(new Vehicle(SharedState.CURRENT_MAX_VID++, segNum, this.xway, currentTime));
            // SharedState.TOTAL_VEHICLES++;
        }

        return randomNumOfVehicles;
    }

    public void deleteVehicles(ArrayList<Vehicle> vs) {
        this.vehicles.removeAll(vs);
        // SharedState.TOTAL_VEHICLES -= vs.size();
    }

    public APair<Vehicle, Vehicle> get2RandomVehicles() {
        int randomVid1 = LoggingUtil.getRandomNumberUsingNextInt(0, vehicles.size());
        int randomVid2 = randomVid1;
        int tries = 0;
        while ( (randomVid1 == randomVid2) && (tries < (2*vehicles.size()) ) ) {
            randomVid2 = LoggingUtil.getRandomNumberUsingNextInt(0, vehicles.size());
            tries++;
        }

        if (randomVid1 == randomVid2) {
            return null;
        }

        return (new APair<Vehicle, Vehicle>(vehicles.get(randomVid1), vehicles.get(randomVid2)));
    }

    // DEBUG
    public void printVehicles() {
        for (Vehicle v : vehicles) {
            System.out.println(v.toString());
        }
    }
    
}
