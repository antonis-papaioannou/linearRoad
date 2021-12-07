package linear_road.Util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.text.SimpleDateFormat;  
import java.util.Date; 


public class LoggingUtil {
    // return the <Class>::<functions>::<line> of the caller
    public static String pointInCode() {
        String filename = Thread.currentThread().getStackTrace()[2].getFileName();
        String functionName = Thread.currentThread().getStackTrace()[2].getMethodName();
        int line = Thread.currentThread().getStackTrace()[2].getLineNumber();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        Date date = new Date();  

        return "[" + formatter.format(date) + "][" + filename + "::" + functionName + "::" + line + "]";
    }

    public static String timestamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        Date date = new Date();  

        return formatter.format(date);
    }

}
