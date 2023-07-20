package com.wm.file.util.testexcel;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StopWatch;

import java.io.FileOutputStream;
import java.io.IOException;

public class HugeExcelExportTest {
    public static int totalRowNumber = 100000; //写入的excel数据行数
    //public static int totalRowNumber = 10000; //写入的excel数据行数
    public static int totalCellNumber = 40; //excel每行共40列
    //普通的写入excel的方法，会消耗内存，写入的行数太大时，会报内存溢出
    public void writeNormalExcelTest() {
        Workbook wb = null;
        FileOutputStream out = null;
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("Sheet 1");
            //定义Row和Cell变量, Rows从0开始.
            Row row;
            Cell cell;
            for (int rowNumber = 0; rowNumber < totalRowNumber; rowNumber++) {
                row = sheet.createRow(rowNumber);
                for (int cellNumber = 0; cellNumber < totalCellNumber; cellNumber++) {
                    cell = row.createCell(cellNumber);
                    cell.setCellValue(Math.random()); //写入一个随机数
                }
                //打印测试，
                if (rowNumber % 10000 == 0) {
                    System.out.println(rowNumber);
                }
            }
            //Write excel to a file
            out = new FileOutputStream("/Users/zhaowanqi/Downloads/normalExcel_" + totalRowNumber + ".xlsx");
            wb.write(out);
            stopWatch.stop();
            System.out.println("process " + totalRowNumber + " spent time:" + stopWatch.getTotalTimeSeconds() + " ms.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (wb != null) wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    //结合临时文件压缩等写入excel，默认超过100行就写到临时文件，不会报内存溢出
    public static void writeHugeExcelTest() {
        SXSSFWorkbook wb = null;
        FileOutputStream out = null;
        try {

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            wb = new SXSSFWorkbook();//默认100行，超100行将写入临时文件
            wb.setCompressTempFiles(false); //是否压缩临时文件，否则写入速度更快，但更占磁盘，但程序最后是会将临时文件删掉的
            Sheet sheet = wb.createSheet("Sheet 1");
            //定义Row和Cell变量, Rows从0开始.
            Row row;
            Cell cell;
            for (int rowNumber = 0; rowNumber < totalRowNumber; rowNumber++) {

                row = sheet.createRow(rowNumber);
                for (int cellNumber = 0; cellNumber < totalCellNumber; cellNumber++) {
                    cell = row.createCell(cellNumber);
                    cell.setCellValue(Math.random()); //写入一个随机数
                }
                //打印测试，
                if (rowNumber % 10000 == 0) {
                    System.out.println(rowNumber);
                }
            }
            //Write excel to a file
            out = new FileOutputStream("D:\\hugeExcel_" + totalRowNumber + ".xlsx");
            wb.write(out);
            stopWatch.stop();
            System.out.println("process " + totalRowNumber + " spent time:" + stopWatch.getTotalTimeSeconds() + " s.");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (wb != null) {
                wb.dispose();// 删除临时文件，很重要，否则磁盘可能会被写满
            }
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (wb != null) wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        writeHugeExcelTest();
    }
}

