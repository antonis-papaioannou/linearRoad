package linear_road.Util;

import java.util.ArrayList;

public class PerformanceReporter {

    private String operatorName;
    private long opLatencySum = 0L; //latency of the operator (start to end of the function)
    private long opMinLatecy = Long.MAX_VALUE;
    private long opMaxLatency = Long.MIN_VALUE;
    private long pipelineLatencySum = 0L;  //latency from the ingestion
    private long count = 0L;
    private long lastReport_sec = System.currentTimeMillis() / 1000;

    //DEBUG
    private ArrayList<Long> pipeLatencies = new ArrayList(100000);

    public PerformanceReporter(String operatorName) {
        this.operatorName = operatorName;
    } 

    public void addLatency(long opLatencyP, long pipelineLatencyP) {
        //DEBUG
        this.pipeLatencies.add(pipelineLatencyP);

        this.opLatencySum += opLatencyP;
        if (opLatencyP > opMaxLatency) {
            opMaxLatency = opLatencyP;
        }
        if (opLatencyP < opMinLatecy) {
            opMinLatecy = opLatencyP;
        }
        this.pipelineLatencySum += pipelineLatencyP;
        this.count += 1;

        long now_sec = System.currentTimeMillis() / 1000;
        if (now_sec > lastReport_sec) {
            String ts = LoggingUtil.timestamp();
            long opLatency = opLatencySum / count;
            long pipelineLatency = pipelineLatencySum / count;

            String logLine = (ts + " op: " + operatorName + " thput = " + count +
                            " pipeLat = " + pipelineLatency + " (total=" + pipelineLatencySum + " )" +
                            " opLat = " + opLatency + " (total=" + opLatencySum +
                            " opMin=" + opMinLatecy + " opMax=" + opMaxLatency+ " )");
            
            logLine += " " + stdDev(pipelineLatency);

            System.out.println(logLine);
            
            this.lastReport_sec = now_sec;
            this.count = 0L;
            this.opLatencySum = 0L;
            this.pipelineLatencySum = 0L;
            this.opMaxLatency = 0L;
            this.opMinLatecy = 0L;

            //clear arraylist used for STDDEV
            pipeLatencies.clear();
        }
    }

    public String stdDev(double pipeLatencyAVG) {
        double varianceSum = 0;
        for (long l : pipeLatencies) {
            double diff = (double)l - pipeLatencyAVG;
            varianceSum += diff*diff;
        }
        double variance = varianceSum / pipeLatencies.size();
        double stddev = Math.sqrt(variance);
        int percent = (int) ((stddev*100) / pipeLatencyAVG);
        return "stddev = " + stddev + " ( " + percent + " %)"; // + " values: " + pipeLatencies.size();
    }
    
}
