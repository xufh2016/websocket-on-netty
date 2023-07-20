package com.coolfish.websocketonnetty;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.coolfish.websocketonnetty.constants.Constants;
import com.coolfish.websocketonnetty.service.PushService;
import com.coolfish.websocketonnetty.util.WsChannelKeyUtil;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
        String string1 = UUID.randomUUID(true).toString();
        String substring = string1.substring(0, 4);
        System.out.println("substring = " + substring);
        long timeMillis = System.currentTimeMillis();
        String string = Long.toString(timeMillis, 36);
        String netCardNum = (substring + string).toUpperCase();
        System.out.println("netCardNum = " + netCardNum);
    }

    @Test
    public void testReadFile() throws IOException {
        String urlPath = "http://120.27.18.244:81/demo-code/c-v1.0-demo.doc";
        String code = getCode(urlPath);
        System.out.println("=======================C语言代码是=======================");
        System.out.print(code);
    }

    private String getCode(String urlPath) throws IOException {
        // 统一资源
        URL url = new URL(urlPath);
        // 连接类的父类，抽象类
        URLConnection urlConnection = url.openConnection();
        // http的连接类
        HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
        //设置超时
        httpURLConnection.setConnectTimeout(1000 * 5);
        //设置请求方式，默认是GET
//        httpURLConnection.setRequestMethod("Get");
        // 设置字符编码
        httpURLConnection.setRequestProperty("Charset", "UTF-8");
        // 打开到此 URL引用的资源的通信链接（如果尚未建立这样的连接）。
        httpURLConnection.connect();
        // 文件大小
        int fileLength = httpURLConnection.getContentLength();

        System.out.println("fileLength = " + fileLength);
        // 控制台打印文件大小
//        System.out.println("您要下载的文件大小为:" + fileLength / (1024 * 1024) + "MB");

        // 建立链接从请求中获取数据
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
        //首先判断文件中的是doc/docx
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
                System.out.println("此文件不是word文件");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resullt;
    }

    @Test
    public void test001() {
        DecimalFormat format = new DecimalFormat("#");
        String resistance1 = "25";
        double resistance = Double.parseDouble(resistance1);
        String s = format.format(resistance);
        int res = new Double(Double.parseDouble(s)).intValue();
        System.out.println("res = " + res);
    }

    @Test
    public void test04() {
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
        c.forEach(item -> System.out.println("item = " + item));
    }


    @Test
    public void testFile() throws IOException {
        File file = new File("D:\\test\\test.doc");
        file.createNewFile();
    }


    @Test
    public void test22() {
        ArrayList<Object> list = new ArrayList<>();
        list.add("1");
        list.add("aaa");
        list.add("bbb");
        list.add("cc");

        list.forEach(item -> {
            System.out.println("item = " + item);
        });
        System.out.println("--------------------------------------------------------------------");
        boolean cc = list.remove("cc");
        System.out.println("cc = " + cc);
        list.forEach(item -> {
            System.out.println("item1 = " + item);
        });
    }

    @Test
    public void testGraphQL() {
        String str = "[{\"paramName\":\"asd78\",\"dataType\":\"string\",\"unitOrModel\":\"\",\"description\":\"\",\"show\":false},{\"startByte\":\"0\",\"field\":\"name\",\"remark\":\"名称\",\"dataType\":\"int\",\"show\":true,\"paramName\":\"12\",\"unitOrModel\":\"111\"}]";
        JSONArray jsonArray = JSONUtil.parseArray(str);
        System.out.println(jsonArray.toStringPretty());
    }

    @Test
    public void test() {
        String ids = "1;2;3;";
        String[] split = ids.split(";");
        List<String> strings = Arrays.asList(split);
        strings.forEach(s -> {
            System.out.println("s = " + s);
        });
    }

    @Test
    public void test11() {
        String jsonStr = "[{\"startByte\":\"0\",\"field\":\"name\",\"remark\":\"名称\",\"dataType\":\"int\",\"show\":true,\"paramName\":\"df\",\"unitOrModel\":\"df\",\"description\":\"df\"}]";
        List<ModelBodyVo> modelBodyVos = JSONUtil.toList(jsonStr, ModelBodyVo.class);
        modelBodyVos.forEach(modelBodyVo -> {
            System.out.println("modelBodyVo = " + modelBodyVo);
        });
    }


    @Test
    public void test12() {
        Integer i = 1;
        System.out.println(i * -1);
    }

    @Test
    public void test123() {
        String jsonStr = "[{\n" +
                "          \"processModelType\": \"model\",\n" +
                "          \"processModelKey\": \"aaa\",\n" +
                "          \"processModelValue\": [{\n" +
                "          \"processModelType\": \"String\",\n" +
                "          \"processModelKey\": \"bbb\",\n" +
                "          \"processModelValue\": \"xxx\"\n" +
                "        }]\n" +
                "        }]\n";
        List<ModelConvertorVo> modelConvertorVos = JSONUtil.toList(jsonStr, ModelConvertorVo.class);
        modelConvertorVos.forEach(modelConvertorVo -> System.out.println("modelConvertorVo = " + modelConvertorVo));
        modelConvertorVos.forEach(item -> {
            if (item.getProcessModelType().equalsIgnoreCase("model")) {

            }
        });
    }


    @Test
    public void test211() {
        String str = "null";
        String[] split = str.split(";");
        for (String s : split) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void test4433() {
        ModelConvertorVo modelConvertorVo = new ModelConvertorVo();
        modelConvertorVo.setProcessModelValue("1");
        modelConvertorVo.setProcessModelKey("age");
        modelConvertorVo.setProcessModelType("string");
//        modelConvertorVo.setModelConvertorVo(modelConvertorVo);
        System.out.println("modelConvertorVo = " + modelConvertorVo);
    }


    @Test
    public void test333() {
        String str = "data_format#数据格式#";
        int i = str.indexOf("#");
        String substring = str.substring(0, i);
        System.out.println("substring = " + substring);
    }

    @Test
    public void test222() throws JsonProcessingException {
        String jsonStr1 = "[{\"startByte\":\"0\",\"field\":\"name\",\"remark\":\"名称\",\"dataType\":\"string\",\"show\":\"false\"},{\"startByte\":\"9\",\"field\":\"name\",\"remark\":\"名称\",\"dataType\":\"string\",\"show\":\"false\"},{\"startByte\":\"12\",\"field\":\"name2\",\"remark\":\"名称\",\"dataType\":\"string\",\"show\":\"true\"}]";
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        JsonNode rootNode = mapper.readTree(jsonStr1);

        Iterator<JsonNode> elements = rootNode.elements();
        while (elements.hasNext()) {
            JsonNode next = elements.next();
            JsonNode field = next.get("field");
            System.out.println("field = " + field);
        }

    }

    @Test
    public void test1333() throws ParseException {
//        Date date = new Date();
//        System.out.println("date = " + date);
//        String format = DateUtil.format(date, "yyyy-MM-dd HH:mm:ss.SSS");
//        DateTime dateTime = DateUtil.parseDate(format);
//        long l = System.currentTimeMillis();
//        System.out.println("l = " + l/1000);
//        time = 1671069600000
        Date date = new Date(1671159600000L);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
        String format1 = format.format(date);
        System.out.println("format1 = " + format1);
        long time = format.parse(format1).getTime();
        System.out.println("time = " + time);

    }

    /**
     * 使用hutool工具包中的工具类进行html的转义和反转义
     */
    @Test
    public void testHtml() {
        String html = "<p><b>1、什么是Vue?</b></p><p>vue真的太好用了，是前后段分离必不可少的开发框架之一……</p><p><br></p><p><i><u>2、Vue能干什么？</u></i></p><p>模拟数据</p><p><br></p>";
        String escape = HtmlUtil.escape(html);
        System.out.println("escape = " + escape);
        String unescape = HtmlUtil.unescape(escape);
        System.out.println("unescape = " + unescape);
    }

    @Test
    public void mergeCollection() {
        ArrayList<Object> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add("aaaaaa");
        list.add("bbbbbbb");
        list.add("nnnnnnnn");
        ArrayList<Object> list1 = new ArrayList<>();
        list1.add(11);
        list1.add(21);
        list1.add("aaaaaa1111");
        list1.add("bbbbbbb1111");
        list1.add("nnnnnnnn111");
        list.addAll(list1);
        System.out.println("list = " + list);
    }

    @Test
    public void testDate() throws ParseException {
        String beginTime = "2022-11-01 00:00:00";
        String endTime = "2022-12-01 10:04:15";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDate parse = LocalDate.parse(beginTime, dateTimeFormatter);
        LocalDate parse1 = LocalDate.parse(endTime, dateTimeFormatter);

        long between = ChronoUnit.DAYS.between(parse, parse1);
        System.out.println("between = " + between);
      /*  final long time = DateUtil.parse(beginTime).getTime() / 1000;
        final long time1 = DateUtil.parse(endTime).getTime() / 1000;
        long abs = Math.abs(time1 - time);
        System.out.println("abs = " + abs);*/

    }

    //@Test
    /*public  void test111(){
        //生成DCC记录:检定项目(是否合格),false为合格 true为不合格
        //一条DCC记录-中的全量检定项目数据
        //检定item
        Map<String, Boolean> itemsMap = new HashMap<>();
        itemsMap.put("DynamicPressure", true);
        itemsMap.put("FrontTemp", false);
        itemsMap.put("GasTemp", true);
        itemsMap.put("MomentFlowrate", false);
        itemsMap.put("StaticPressure", true);
        //--------------map转成dcc数据格式--------------
        //所有标定项目(包括合格和不合格)
        com.alibaba.fastjson.JSONObject dccDataJsonObj = new com.alibaba.fastjson.JSONObject();
        //每个标定项目
        com.alibaba.fastjson.JSONObject dccDataObj = null;
        //每个标定项目中标定数据数组
        com.alibaba.fastjson.JSONArray itemDatasArray = null;
        //每条标定数据值
        com.alibaba.fastjson.JSONObject itemDataObj = null;
        for (Map.Entry<String, Boolean> entry : itemsMap.entrySet()) {
            dccDataObj = new com.alibaba.fastjson.JSONObject();

            itemDataObj = new com.alibaba.fastjson.JSONObject();
            itemDatasArray = new com.alibaba.fastjson.JSONArray();
            itemDatasArray.add(itemDataObj);
            //初始化标定状态为0,0-未开始，1-标定完成2-标定失败3-标定中
            //是否合格，即是否需要标定,检定项目(是否合格),false为合格 true为不合格
            //dccDataObj.put("needCalibratio", entry.getValue());
            //dccDataObj.put("calibrationData", itemDatasArray);
            dccDataJsonObj.put(entry.getKey(), dccDataObj);
        }
        System.out.println("dccDataJsonObj = " + dccDataJsonObj);
    }*/

    @Test
    public void test22222() {
        DecimalFormat decimalFormat = new DecimalFormat("0.0000");
        Double a = 1.123123123;
        System.out.println("decimalFormat.format(a) = " + decimalFormat.format(a));
    }

    @Test
    public void test444() {
        String aa = "4C5952FEFFFFFF020000000103E0A44F633F2685833F8E2D703FE776773FCEC988BE0CBAA83FF8A131BD23CD8A3F259A7C3FE3C7783F0000803F0000803FC3D37B3F0000803FE3A57B3F0000803F0000803F366C49BE1FDDA23FC44D0BBD0000803FA332813F0000803F0000803F0000803F0000803F0000803F0000803F0000803FBA47823F5F442EB9CD77983FB4233CC1F8D0C23F0000803F0000803F0000803F0000803F0000803F1F4F7E3F0000803F0000803F0000803F1C2B65B9B579963FBEA14BC115A5D33F0000803F0000803FD28A843F0000803F0000803F08D87D3F0000803F0000803F0000803F41";
        StringBuilder stringBuilder = new StringBuilder(aa);
        stringBuilder.replace(6, 14, "02000000");
        stringBuilder.replace(14, 22, "FEFFFFFF");
        System.out.println("stringBuilder.toString() = " + stringBuilder.toString());

    }

    /*
    @Test
    public void test0000(){
        Map<String, Boolean> itemsMap = new LinkedHashMap<>(4);
        itemsMap.put("flowValueErrorChannelA", false);
        itemsMap.put("flowValueErrorChannelB", false);
        itemsMap.put("flowValueErrorChannelC", false);
        itemsMap.put("flowValueErrorChannelD", true);
        System.out.println("itemsMap = " + itemsMap);
        //所有标定项目(包括合格和不合格)
        com.alibaba.fastjson.JSONObject dccDataJsonObj = new com.alibaba.fastjson.JSONObject(true);
        //每个标定项目
        com.alibaba.fastjson.JSONObject dccDataObj;
        //每个标定项目中标定数据数组
        com.alibaba.fastjson.JSONArray itemDataArray;
        //每条标定数据值
        com.alibaba.fastjson.JSONObject itemDataObj;
        for (Map.Entry<String, Boolean> entry : itemsMap.entrySet()) {
            dccDataObj = new com.alibaba.fastjson.JSONObject();
            itemDataObj = new com.alibaba.fastjson.JSONObject();
            itemDataArray = new com.alibaba.fastjson.JSONArray();
            itemDataArray.add(itemDataObj);
            //初始化标定状态为0,0-未开始，1-标定完成2-标定失败3-标定中
            //是否合格，即是否需要标定,检定项目(是否合格),false为合格 true为不合格
            dccDataObj.put("needCalibratio", entry.getValue());
            dccDataObj.put("calibrationData", itemDataArray);
            dccDataJsonObj.put(entry.getKey(), dccDataObj);
        }
        System.out.println("dccDataJsonObj = " + dccDataJsonObj);
    }*/

    @Test
    public void test0001() {
        List<String> hadNoloadList = Constants.con;
        List<String> hadLoadList = Constants.con;
        hadLoadList.remove("300.0");
        System.out.println("hadLoadList = " + hadLoadList);
        System.out.println("hadNoloadList = " + hadNoloadList);
    }

    @Test
    public void jiami() {
        String pwd = "123asd";
        AES aes = SecureUtil.aes();
        String password = aes.encryptHex(pwd);
        System.out.println("password = " + password);
        String str = aes.decryptStr(password);
        System.out.println("str = " + str);
    }

    @Test
    public void test999() {
        String orgPath = "/111";
        if (orgPath.startsWith("/")) {
            orgPath = orgPath.substring(1);
            System.out.println("orgPath = " + orgPath);
        }

    }

    @Test
    public void test888() {
        short i = 1;
        //i=i+1;
        String str = "4C5952FEFFFFFF020000000102610000000000000000000000000040CEBB0000000000000000000000000000C0A33B0000" +
                "000000000000000000000000A0F0BD000000000000000000000000000090ED3C00000000000000000000000000001003BD0000000000CDCCBC410A57CD42F6";
        System.out.println("str.length() = " + str.length());
        String substring = str.substring(22, 26);
        System.out.println("substring = " + substring);
    }

    @Autowired
    private PushService pushService;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    /*@Before
    public void setUp(){
        request = new MockHttpServletRequest();
        request.setCharacterEncoding("UTF-8");
        response = new MockHttpServletResponse();
    }*/

    @Test
    public void testPushMsg() {
        int remotePort = request.getRemotePort();
        pushService.pushMsgToOneByKey("wang:0x7761" +":"+ request.getRemoteHost(), "hello client");
        System.out.println("remotePort = " + remotePort);
    }

    @Test
    public void test777(){
        String string = WsChannelKeyUtil.generateWsChannelKey("ly2028", "1Q000001", "zhangsan");
        System.out.println("string = " + string);
    }

    @Test
    public void test889998(){
        String jsonStr = "{\"动压\":12.2,\"全压\":21.2,\"烟尘流压\":11.09,\"烟尘计压\":31,\"烟气分析流压\":22.22,\"烟气流压\":21.12,\"烟气计压\":24.12}";
        JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
        jsonObject.remove("动压");
        System.out.println("jsonObject = " + jsonObject);
    }

}
