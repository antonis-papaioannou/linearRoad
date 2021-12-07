package lr_generator.Util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;

public class LRFileWriter {
    private String filename;
    private BufferedWriter bw = null;

    public LRFileWriter(String filename) {
        this.filename = filename;

        File file = new File(filename);
        FileWriter fw;
        try {
            fw = new FileWriter(file, true); // true for append
            bw = new BufferedWriter(fw);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* Optmization: no flush or close file
    * The caller MUST flush and close file at the end */
    public void write_unsafe(String msg) {
        try {
            bw.write(msg + "\n");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void write(String msg) {
        write_unsafe(msg);
        close();
    }

    public void close() {
        try {
            bw.flush();
            bw.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}