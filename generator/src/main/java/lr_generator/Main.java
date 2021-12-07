package lr_generator;

import java.util.ArrayList;
import lr_generator.Util.LoggingUtil;
import lr_generator.KafkaClient;

public class Main {

    public static void main(String[] args) {
        
        parseCommandLineArguments(args);
        printConfig();

        if (GeneratorConfig.SIMULATE == false) {
            SharedState.KAFKA = new KafkaClient(GeneratorConfig.KAFKA_HOST, GeneratorConfig.KAFKA_PORT, GeneratorConfig.KAFKA_TOPIC);
        }

        XWaySimulator xways[] = new XWaySimulator[GeneratorConfig.XWAYS];
        for (int i = 0; i < GeneratorConfig.XWAYS; i++) {
            xways[i] = new XWaySimulator(i+1);
            xways[i].initSegments();
            xways[i].addNewVehicles(0);
            xways[i].printAllPositionReports(0);
        }

        int totalVehiclesRequested = GeneratorConfig.XWAYS * GeneratorConfig.VEHICLES_PER_XWAY;

        //for time
        int interval_produced_tuples = 0;
        for (int currentTime = 1; currentTime < GeneratorConfig.DURATION; currentTime++) {
            // System.out.println(" ------------------ START sec " + currentTime + " -------------------------");

            int totalVehiclesAllXways = 0;
            for (int x = 0; x < GeneratorConfig.XWAYS; x++) {
                xways[x].prepareAccident(currentTime);
                xways[x].clearAccidentCheck(currentTime);
                
                xways[x].moveCarsInAllSegment(currentTime);                
                xways[x].addNewVehicles(currentTime);

                xways[x].printAllPositionReports(currentTime);
                
                totalVehiclesAllXways += xways[x].getVehiclesNum();
                
                xways[x].detectAccident(currentTime);
            }

            // System.out.println(" ------------------ END sec " + currentTime + 
            //         " vehicles: " + totalVehiclesAllXways + "/" + totalVehiclesRequested + 
            //         " ----------------------------");

            if ( (currentTime % 200) == 0 ) {
                interval_produced_tuples = SharedState.TOTAL_POSITION_REPORTS - interval_produced_tuples;
                System.out.println(LoggingUtil.timestamp() + " simulated sec " + currentTime + " / " + GeneratorConfig.DURATION + 
                        " produced tuples " + interval_produced_tuples + " (total " + SharedState.TOTAL_POSITION_REPORTS + ")");
            }
        }

        if (GeneratorConfig.SIMULATE == false) {
            SharedState.KAFKA.flushAndClose();
        }
        System.out.println("Total position reports produced: " + SharedState.TOTAL_POSITION_REPORTS);
    }


    private static void parseCommandLineArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            int extra_params_consumed = parseArgument(args, i);
            i += extra_params_consumed;
        }
    }

    private static int parseArgument(String[] args, int position) {
        // System.out.println("parsing arg " + position);
        int extra_params_consumed = 0;
        switch(args[position]) {
            case "-d":  // duration
                // +1 used as hack to extend duaration and allow generator to print stats up to "duration" time
                GeneratorConfig.DURATION = Integer.parseInt(args[position+1]) + 1;
                extra_params_consumed = 1;
                break;
            case "-x":
                GeneratorConfig.XWAYS = Integer.parseInt(args[position+1]);
                extra_params_consumed = 1;
                break;
            case "-v":  // vehicles per xway
                GeneratorConfig.VEHICLES_PER_XWAY = Integer.parseInt(args[position+1]);
                extra_params_consumed = 1;
                break;
            case "-k":  // kafka host/ip
                GeneratorConfig.KAFKA_HOST = args[position+1];
                extra_params_consumed = 1;
                break;
            case "-f":  // persist data set to a file
                GeneratorConfig.PRINT_DATAFILE = true;
                GeneratorConfig.DATA_FILE = args[position+1];
                extra_params_consumed = 1;
                break;
            case "-s":  // simulate: produce data to the specified file wihtout importing data into kafka
                GeneratorConfig.SIMULATE = true;
                break;
            case "-p":  // print on stdout
                GeneratorConfig.PRINT_OUT = true;
                break;
            case "-h":  // print help
                printHelp();
                System.exit(0);
            default:
                printHelp();
                System.exit(0);
        }
        return extra_params_consumed;
    }

    private static void printConfig() {
        System.out.println("---- Generator Config -----");
        System.out.println("- XWays: " + GeneratorConfig.XWAYS);
        System.out.println("- Duration: " + GeneratorConfig.DURATION);
        System.out.println("- Vehicles per Xway: " + GeneratorConfig.VEHICLES_PER_XWAY);
        System.out.println("- Simulation mode: " + GeneratorConfig.SIMULATE);
        System.out.println("- Output: " +
                            "\n  |--- Terminal: " + GeneratorConfig.PRINT_OUT +
                            "\n  |--- hint file: " + GeneratorConfig.PRINT_HINTS_FILE +
                            "\n  |--- data file: " + GeneratorConfig.PRINT_DATAFILE);
        System.out.println("- KAFKA" + 
                            "\n  |--- host: " + GeneratorConfig.KAFKA_HOST +
                            "\n  |--- port: " + GeneratorConfig.KAFKA_PORT +
                            "\n  |--- topic: " + GeneratorConfig.KAFKA_TOPIC);
        
        System.out.println();
    }

    private static void printHelp() {
        System.out.println("Optional parameters:" +
                            "\n\t-d <positive integer>\tSimulation duration" +
                            // "\n\t-v <number>\tNumber of vehicles" +
                            "\n\t-x <positive integer>\tNumber of expressways" +
                            "\n\t-k <host>\t\tKafka host\n" +
                            "\n\t-f <datafile>\t\tPersist data to logfile" +
                            "\n\t-s <datafile>\t\tLog data into datafile but do not put it into Kafka" +
                            "\n\t-h\t\tThis help msg");

        System.out.println("Execution through manven: mvn exec:java -Dxec.args='-d 3600 ...'");
    }

}
