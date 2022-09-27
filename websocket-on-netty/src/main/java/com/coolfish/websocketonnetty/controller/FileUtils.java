package com.coolfish.websocketonnetty.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @className: FileUtils
 * @description: TODO 类描述
 * @author: xufh
 * @date: 2022/9/22
 */
public class FileUtils {
    public static List<String> getFiles(File file){
        List<String> files = new ArrayList<>();
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                files.add(tempList[i].toString());
            }
            if (tempList[i].isDirectory()) {
                getFiles(file);
            }
        }
        return  files;
    }
}
