package cn.oneforce.javaagent.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by caoyanfei on 16/10/27.
 */
public class Main {
    public Main(){}
    public Main(String nam, String a){



    }
    public static void main(String[] args) {
//        new Main();
//        new Main("1","2");
//        System.out.println("test");
//        System.out.println("test");
//        System.out.println("test");
//        System.out.println("test");
//        try {
//            FileOutputStream os = new FileOutputStream(File.createTempFile("test","txt"));
//            FileWriter writer = new FileWriter(File.createTempFile("test","txt"));
//            ;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Package p = Main.class.getPackage();
        System.out.println(Arrays.stream(p.getName().split("\\.")).map(s -> s.substring(0,1)).collect(Collectors.joining(".")));
    }
}

