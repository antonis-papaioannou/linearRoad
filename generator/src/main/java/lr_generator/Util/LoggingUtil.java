package lr_generator.Util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.text.SimpleDateFormat;  
import java.util.Date; 
import java.util.Random;


public class LoggingUtil {

    // min: inclusive max: exclusive
    public static int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static void write2file(String msg) {
        BufferedWriter bw = null;
        try {
            // File file = new File("/media/localdisk/antonis/flink/log/mylog.txt");
            File file = new File("/home/user/flink/log/mylog.txt");

            /* This logic will make sure that the file 
            * gets created if it is not present at the
            * specified location*/
            // if (!file.exists()) {
            //     file.createNewFile();
            // }

            FileWriter fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
            bw.write(msg + "\n");
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // return the <Class>::<functions>::<line> of the caller
    public static String pointInCode() {
        String filename = Thread.currentThread().getStackTrace()[2].getFileName();
        String functionName = Thread.currentThread().getStackTrace()[2].getMethodName();
        int line = Thread.currentThread().getStackTrace()[2].getLineNumber();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        Date date = new Date();  

        return "[" + formatter.format(date) + "][" + filename + "::" + functionName + "::" + line + "] ";
    }

    public static String timestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        Date date = new Date();  

        return formatter.format(date);
    }

}
