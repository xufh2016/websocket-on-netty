package com.coolfish.websocketonnetty.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @className: b
 * @description: TODO 类描述
 * @author: xufh
 * @date: 2022/9/22
 */
@RestController
public class FileUploadController {
    @RequestMapping(value = "zipDownload", method = RequestMethod.GET)
    public String zipDownload(HttpServletRequest request, HttpServletResponse response) {
        String path = "D:\\test";
        File file = new File(path);
        List<File> files = new ArrayList<>();
        List<String> files1 = FileUtils.getFiles(file);
        files1.forEach(item->{
            File file1 = new File(item);
            files.add(file1);
        });
        ZipDownloadUtil.zipDownload(response, "demo.zip", files);
        return "download success";
    }
}