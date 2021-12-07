package lr_generator;

import lr_generator.KafkaClient;

public class SharedState {
    
    public static KafkaClient KAFKA = null;

    public static int CURRENT_MAX_VID = 1;
    public static int TOTAL_POSITION_REPORTS = 0;
}
