package linear_road;

import org.apache.flink.api.java.utils.ParameterTool;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Parse config file for user defined parameters
 */
public class BenchmarkConfig implements Serializable {

    // Kafka
    public final String kafkaTopic;

    // Redis
    public final String redisHost;
    // public final int redisDb;
    public final boolean redisClusterMode;

    // Other
    public final boolean monitorOps;

    public final ParameterTool parameters;


    /**
    * Create a config starting with an instance of ParameterTool
    */
    public BenchmarkConfig(ParameterTool parameterTool) {
        this.parameters = parameterTool;

        // Kafka
        this.kafkaTopic = parameterTool.getRequired("kafka.topic");

        // Redis
        this.redisHost = parameterTool.get("redis.host", "localhost");
        // this.redisDb = parameterTool.getInt("redis.db", 0);
        this.redisClusterMode = parameterTool.getBoolean("redis.cluster");

        // Other
        this.monitorOps = parameterTool.getBoolean("monitor.operators", false);
    }

    /**
     * Creates a config given a Yaml file
     */
    public BenchmarkConfig(String yamlFile) throws FileNotFoundException {
        this(yamlToParameters(yamlFile));
    }

    /**
     * Create a config directly from the command line arguments
     */
    public static BenchmarkConfig fromArgs(String[] args) throws FileNotFoundException {
        if (args.length < 1) {
            return new BenchmarkConfig("conf/benchmarkConf.yaml");
        } else {
            return new BenchmarkConfig(args[0]);
        }
    }

    /**
     * Get the parameters
     */
    public ParameterTool getParameters(){
        return this.parameters;
    }

    private static ParameterTool yamlToParameters(String yamlFile) throws FileNotFoundException {    
        // load yaml file
        Yaml yml = new Yaml(new SafeConstructor());
        Map<String, String> ymlMap = (Map) yml.load(new FileInputStream(yamlFile));

        ymlMap.put("bootstrap.servers", getKafkaBrokers(ymlMap));
        ymlMap.put("auto.offset.reset", "earliest");  //ANTONIS values can be: earliest, latest
        ymlMap.put("group.id", getKafkaClientGroupID(ymlMap));

        // Convert everything to strings
        for (Map.Entry e : ymlMap.entrySet()) {
            e.setValue(e.getValue().toString());
        }
        return ParameterTool.fromMap(ymlMap);
    }

    private static String getKafkaBrokers(Map conf) {
        if(!conf.containsKey("kafka.brokers")) {
            throw new IllegalArgumentException("No kafka brokers found!");
        }
        if(!conf.containsKey("kafka.port")) {
            throw new IllegalArgumentException("No kafka port found!");
        }
        return listOfStringToString((List<String>) conf.get("kafka.brokers"), String.valueOf(conf.get("kafka.port")), "");
    }

    private static String getKafkaClientGroupID(Map conf) {
        if (!conf.containsKey("kafka.group.id")) {
            return UUID.randomUUID().toString();
        }
        return conf.get("kafka.group.id").toString();
    }


    private static String listOfStringToString(List<String> list, String port, String path) {
        String val = "";
        for(int i=0; i<list.size(); i++) {
            val += list.get(i) + ":" + port + path;
            if(i < list.size()-1) {
                val += ",";
            }
        }
        return val;
    }

    public void printConfig() {
        System.out.println("| Config");
        System.out.println("|-- Kafka");
        System.out.println("|---- broker(s): " + parameters.get("kafka.brokers"));
        System.out.println("|---- port: " + parameters.get("kafka.port"));
        System.out.println("|---- topic: " + this.kafkaTopic);
        System.out.println("|---- auto.offset.reset: " + parameters.get("auto.offset.reset"));
        System.out.println("|---- group.id: " + parameters.get("group.id"));
        System.out.println("|-- Zookeeper");
        System.out.println("|---- server(s): " + parameters.get("zookeeper.servers"));
        System.out.println("|---- port: " + parameters.get("zookeeper.port"));
        System.out.println("|-- Redis");
        System.out.println("|---- host: " + this.redisHost);
        System.out.println("|---- cluster mode: " + this.redisClusterMode);
        System.out.println("|-- Other");
        System.out.println("|---- monitor operators: " + this.monitorOps);
    }

}
