package lr_generator;

public class GeneratorConfig {

    public static int XWAYS = 1;
    public static final int SEGMENTS_PER_XWAY = 100;
    public static int VEHICLES_PER_XWAY = 1000;

    public static int DURATION = 3600;

    // Kafka config
    public static String KAFKA_HOST = "localhost";
    public static String KAFKA_PORT = "9092";
    public static String KAFKA_TOPIC = "antonis";

    // Config options
    public static boolean PRINT_OUT = false;
    public static boolean PRINT_DATAFILE = false;
    public static String DATA_FILE = "data.txt";
    public static boolean SIMULATE = false;
    public static boolean PRINT_HINTS_FILE = true;
    public static String HINTS_FILE = "accidents.txt";
    
}
