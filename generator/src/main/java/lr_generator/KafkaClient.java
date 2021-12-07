package lr_generator;

import java.util.concurrent.ExecutionException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.errors.TimeoutException;

public class KafkaClient {
    private String host = "localhost";
    private String port = "9092";
    private String connectionURI = host + ":" + port;
    private String topic = "antonis";

    private boolean isAsync = true; //TODO: use user param

    private KafkaProducer<Integer, String> producer;

    public KafkaClient(String host, String port, String topic) {
        this.host = host;
        this.port = port;
        this.connectionURI = host + ":" + port;
        this.topic = topic;

        this.configureKafkaProducer(null, false, -1);
        System.out.println("Kafka client config: " + this.connectionURI + " " + this.topic + " syncMode:" + !isAsync);
    }

    private void configureKafkaProducer(final String transactionalId,
                                    final boolean enableIdempotency,
                                    final int transactionTimeoutMs) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, connectionURI);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "AntonisLRGenerator_v2");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        if (transactionTimeoutMs > 0) { // int
            props.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, transactionTimeoutMs);
        }
        if (transactionalId != null) {   //String
            props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionalId);
        }
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotency);  //boolean

        producer = new KafkaProducer<>(props);
    }


    public void insertRecord(int key, String record) {
        if (isAsync) { // Send asynchronously
            producer.send(new ProducerRecord<>(topic, key, record));
        } else { // Send synchronously
            try {
                // System.out.println("sync");
                producer.send(new ProducerRecord<>(topic, key, record)).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    public void flushAndClose() {
        producer.flush();
        producer.close();
    }

}
