package linear_road;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import linear_road.RichOps.*;
import linear_road.RichOps.SegmentStats.*;

import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.TimeCharacteristic;

/**
 * 	Antonis Papaioannou
 */
public class StreamingJob {

	private static final int TYPE_POSITION_REPORT            = 0;
	private static final int TYPE_ACCOUNT_BALANCE_REQUEST    = 2;
	private static final int TYPE_DAILY_EXPENDITURE_REQUEST  = 3;

	public static void main(String[] args) throws Exception {
		BenchmarkConfig config = BenchmarkConfig.fromArgs(args);
		config.printConfig(); // print config options on stdout of the client terminal that submit the job
		
		// set up the streaming execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

		// The source overwrites the event timestamp from the time field in the payload
		DataStream<String> rawMessageStream = streamSource(config, env);

		DataStream<Event> tuples = rawMessageStream
				.flatMap(new InputParser()).name("deserialize");

		DataStream<Event> vehicleState = tuples
				.filter(s -> s.getType() == TYPE_POSITION_REPORT) 
				.keyBy(s -> s.vid)
				.map(new VehicleStateMemory(config)).name("VStateMemory");

	    // Segment statistics
		vehicleState
				.keyBy(s -> s.segID())
				.window(SlidingEventTimeWindows.of(Time.seconds(300), Time.seconds(60)))
				.aggregate(new SegStatsWindowAggregate(), new SegStatsProcessWindow())
				.flatMap(new SegStats(config)).name("SegStats");

		
		DataStream<Event> partitionedStateOnSegment = vehicleState
				.keyBy(s -> s.segID);

		// Accident Notification
		partitionedStateOnSegment
				.map(new AccidentManager(config)).name("AccManager");

		// Toll notification
		partitionedStateOnSegment
				.filter(s -> s.isCrossing)
				.flatMap(new TollCalculator(config)).name("Toll");

		// execute program
		env.execute("Antonis LinearRoad");
	}

	/**
	* Choose source - Kafka
	*/
  	private static DataStream<String> streamSource(BenchmarkConfig config, StreamExecutionEnvironment env) {
		// System.out.println("kafka ingestion topic: " + config.kafkaTopic);

		FlinkKafkaConsumer<String> source = new FlinkKafkaConsumer<>(
															config.kafkaTopic,
															new SimpleStringSchema(),
															config.getParameters().getProperties());
	
		WatermarkStrategy<String> wmStrategy = WatermarkStrategy
          			.<String>forMonotonousTimestamps()
					.<String>withTimestampAssigner((event, timestamp) -> ((Long.valueOf(event.split(",")[1]))*1000) ); //convert sec to ms

		source.assignTimestampsAndWatermarks(wmStrategy);
									
		return env.addSource(source, "kafkaSrc");
	}

}
