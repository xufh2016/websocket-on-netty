package com.coolfish.websocketonnetty;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest
class WebsocketOnNettyApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void test1() {
        String str = "ws://127.0.0.1:8082/ws/1/2/3";
        String[] split = str.split("/ws/");
        System.out.println(Arrays.toString(split));
        System.out.println("split.length = " + split.length);
        List<String> list = new LinkedList<>();
        list.addAll(Arrays.asList(split[1].split("/")));
        list.forEach(item -> System.out.println("item = " + item));
        System.out.println(list.get(0));
    }

    @Test
    public void test2() {
        String aa = "12";

//        System.out.println("decimalFormat.format(new Long(Long.parseLong(aa))) = " + decimalFormat.format(new Long(Long.parseLong(aa))));
        System.out.println("Double.parseDouble(aa) = " + Double.parseDouble(aa));
        DecimalFormat decimalFormat = new DecimalFormat("#");
//        decimalFormat.setRoundingMode(RoundingMode.FLOOR);
//        decimalFormat.setMaximumFractionDigits(0);
        double v = Double.parseDouble(aa);
        String format = decimalFormat.format(v);
        System.out.println("format = " + format);
        System.out.println("Long.parseLong(aa) = " + new Double(Double.parseDouble(format)).intValue());
    }

    @Test
    public void test01() {
        Snowflake snowflake = IdUtil.getSnowflake();
        long l = snowflake.nextId();
        System.out.println("(l+\"\").length() = " + (l + "").length());
        System.out.println("l = " + l);
    }

    @Test
    public void test02() {
        System.out.println("System.currentTimeMillis() = " + System.currentTimeMillis());
        long timeMillis = System.currentTimeMillis();
        String string = Long.toString(timeMillis, 36);
        System.out.println("string = " + string);
    }

    @Test
    public void testReadFile() throws IOException {
        String urlPath = "http://120.27.18.244:81/demo-code/c-v1.0-demo.doc";
        String code = getCode(urlPath);
        System.out.println("=======================C???????????????=======================");
        System.out.print(code);
    }

    private String getCode(String urlPath) throws IOException {
        // ????????????
        URL url = new URL(urlPath);
        // ??????????????????????????????
        URLConnection urlConnection = url.openConnection();
        // http????????????
        HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
        //????????????
        httpURLConnection.setConnectTimeout(1000 * 5);
        //??????????????????????????????GET
//        httpURLConnection.setRequestMethod("Get");
        // ??????????????????
        httpURLConnection.setRequestProperty("Charset", "UTF-8");
        // ???????????? URL????????????????????????????????????????????????????????????????????????
        httpURLConnection.connect();
        // ????????????
        int fileLength = httpURLConnection.getContentLength();

        System.out.println("fileLength = " + fileLength);
        // ???????????????????????????
//        System.out.println("??????????????????????????????:" + fileLength / (1024 * 1024) + "MB");

        // ????????????????????????????????????
        url.openConnection();
        InputStream is = httpURLConnection.getInputStream();
        WordExtractor re = new WordExtractor(is);
        return re.getText();
        /* BufferedInputStream bufferedInput = new BufferedInputStream(httpURLConnection.getInputStream());
        byte[] buffer = new byte[1024];
        int bytesRead;
        StringBuilder bu = new StringBuilder();
        while ((bytesRead = bufferedInput.read(buffer)) != -1) {
            String chunk = new String(buffer, 0, bytesRead);
            bu.append(chunk);
        }
        return bu.toString();*/
    }

    @Test
    public void testReadWord() {
        String urlPath = "http://120.27.18.244:81/demo-code/c-v1.0-demo.doc";
        String textContent = null;
        try {
            textContent = readDoc(urlPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(textContent);
    }

    public static String readDoc(String path) throws IOException {
        String resullt = "";
        //???????????????????????????doc/docx
        try {
            if (path.endsWith(".doc")) {
                InputStream is = new FileInputStream(new File(path));
                WordExtractor re = new WordExtractor(is);
                resullt = re.getText();
                re.close();
            } else if (path.endsWith(".docx")) {
                OPCPackage opcPackage = POIXMLDocument.openPackage(path);
                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                resullt = extractor.getText();
                extractor.close();
            } else {
                System.out.println("???????????????word??????");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resullt;
    }
    
    @Test
    public void test001(){
        DecimalFormat format = new DecimalFormat("#");
        String  resistance1 = "25";
        double resistance = Double.parseDouble(resistance1);
        String s = format.format(resistance);
        int res = new Double(Double.parseDouble(s)).intValue();
        System.out.println("res = " + res);
    }

    @Test
    public void test04(){
        String json = "{\n" +
                "    \"c\":[\n" +
                "        {\n" +
                "            \"version\":\"v1.0.0.1\",\n" +
                "            \"procedureCode\":\"http://120.27.18.244:81/demo-code/c-v1.0-demo.doc\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONObject jsonObject = JSONUtil.parseObj(json);
        System.out.println("jsonObject = " + jsonObject);
        List<Object> c = (List<Object>) jsonObject.get("c");
        c.forEach(item-> System.out.println("item = " + item));
    }


    @Test
    public void testFile() throws IOException {
        File file = new File("D:\\test\\test.doc");
        file.createNewFile();
    }



    @Test
    public void test22(){
        ArrayList<Object> list = new ArrayList<>();
        list.add("1");
        list.add("aaa");
        list.add("bbb");
        list.add("cc");

        list.forEach(item->{
            System.out.println("item = " + item);
        });
        System.out.println("--------------------------------------------------------------------");
        boolean cc = list.remove("cc");
        System.out.println("cc = " + cc);
        list.forEach(item->{
            System.out.println("item1 = " + item);
        });
    }






























}
