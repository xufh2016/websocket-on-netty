package com.coolfish.websocketonnetty;



import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * @className: CreateMultipleSheet
 * @description: TODO 类描述
 * @author: xufh
 * @date: 2022/12/13
 */
public class CreateMultipleSheet {
    public static void main(String[] args) {
        //处理器核心数
        int processor = Runtime.getRuntime().availableProcessors();
        //HSSFWorkbook 一个sheet页只能写入六万多条数据
        HSSFWorkbook workBook = new HSSFWorkbook();
        //创建格式
        CellStyle style = workBook.createCellStyle();
        //居中格式
        style.setAlignment(HorizontalAlignment.CENTER);
        //创建sheet页
        HSSFSheet sheet = workBook.createSheet();
        //创建一行
        HSSFRow hssfRow = sheet.createRow(0);
        HSSFCell hssfCell = hssfRow.createCell(0);
        hssfCell.setCellStyle(style);
        hssfCell.setCellValue("第" + 1 + "个sheet页，第一行，第一个单元格");

        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellStyle(style);
        hssfCell.setCellValue("第" + 1 + "个sheet页，第一行，第二个单元格");

        hssfCell = hssfRow.createCell(2);
        hssfCell.setCellStyle(style);
        hssfCell.setCellValue("第" + 1 + "个sheet页，第一行，第三个单元格");

        //手工创建线程池
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(
                processor,
                processor * 2,
                3,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(processor * 2),//这个值可以根据压测得到的最大值
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        executorService.allowCoreThreadTimeOut(true);
        //计数器 等待线程池中的线程执行完毕
        CountDownLatch countDownLatch = new CountDownLatch(processor);
        for (int i = 1; i <= processor; i++) {
            int start = (i - 1) * 100 + 1;
            int end = i * 100;
            //放入线程池中
            executorService.execute(() -> createRows(sheet, start, end, countDownLatch));
        }
        try {
            //等待所有线程执行完毕
            countDownLatch.await();
            //关闭线程池
            executorService.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FileOutputStream fou = null;
        try {
            fou = new FileOutputStream("D:\\multiSheet.xls");
            workBook.write(fou);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fou != null) {
                try {
                    fou.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void createRows(HSSFSheet hSSFSheet, int startRow, int endRow, CountDownLatch countDownLatch) {
        HSSFRow hssfRows;
        HSSFCell hSSFCells;
        int i = startRow;
        try {
            while (i <= endRow) {
                hssfRows = getRows(hSSFSheet, i);
                hSSFCells = hssfRows.createCell(0);
                hSSFCells.setCellValue("第" + (i + 1) + "行，第一个单元格");

                hSSFCells = hssfRows.createCell(1);
                hSSFCells.setCellValue("第" + (i + 1) + "行，第一个单元格");

                hSSFCells = hssfRows.createCell(2);
                hSSFCells.setCellValue("第" + (i + 1) + "行，第一个单元格");
                ++i;
            }
        } finally {
            countDownLatch.countDown();
        }
    }

    /**
     * 创建表格 这里要加锁
     *
     * @param hSSFSheet
     * @param row
     * @return
     */
    private static HSSFRow getRows(HSSFSheet hSSFSheet, int row) {
        synchronized (Object.class) {
            return hSSFSheet.createRow(row);
        }
    }
}
