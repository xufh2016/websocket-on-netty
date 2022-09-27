package com.coolfish.websocketonnetty;

/**
 * @className: Test
 * @description: TODO 类描述
 * @author: xufh
 * @date: 2022/9/22
 */

import java.io.File;

public class Test {

    public static void main(String[] args) {
        File dir = new File("D:\\test");
        listAllFile(dir);
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
