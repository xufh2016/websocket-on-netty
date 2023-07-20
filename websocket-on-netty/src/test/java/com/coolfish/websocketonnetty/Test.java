package com.coolfish.websocketonnetty;

/**
 * @className: Test
 * @description: TODO 类描述
 * @author: xufh
 * @date: 2022/9/22
 */

import java.io.File;
import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {
//        File dir = new File("D:\\test");
//        listAllFile(dir);
//        LanguageEntity languageEntity = new LanguageEntity();
//        languageEntity = null;
//        System.out.println("发生GC之前");
//        System.gc();
//        System.out.println("发生GC之后");
//        System.in.read();
//        String dateStr="2022-11-01 00:00:00~2022-11-02 00:00:00";
//        String[] split = dateStr.split("~");
//        System.out.println("split[0] = " + split[0]);
//        System.out.println("split[1] = " + split[1]);
        int i = 4;
        for (int j = 0; j < 10; j++) {
            if (i==j){
                return;
            }
            System.out.println("j = " + j);
        }

    }

    public static void listAllFile(File f) {
        File[] files = f.listFiles();
        for (File file : files) {
            System.out.println(file);
            if (file.isDirectory())
                listAllFile(file);
        }
    }
}
