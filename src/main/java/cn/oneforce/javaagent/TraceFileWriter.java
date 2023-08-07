package cn.oneforce.javaagent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by caoyanfei on 16/10/27.
 */
public class TraceFileWriter {
    private FileWriter fileWriter;
    private TraceFileWriter(){

        try {
            File file = File.createTempFile("trace",".txt");
            System.out.println("trace file save at : " + file);
            fileWriter = new FileWriter(file);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        TraceFileWriter.instance.fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
    public static TraceFileWriter instance = new TraceFileWriter();
//    public static void write(String content) {
//        try {
//            instance.fileWriter.write(content);
//            instance.fileWriter.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    public static void write(Object content) {
        System.out.println(content == null ? "": content.toString());

//        try {
//            instance.fileWriter.write(content == null ? "": content.toString());
//            instance.fileWriter.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
