//package cn.com.laoying.yly.domain.common.util;
//
//import com.alibaba.fastjson.JSONObject;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//
//import java.util.Base64;
//import java.util.Date;
//import java.util.Map;
//
///**
// * @className: MetaBaseJWTUtils
// * @description: metabase连接工具
// * @author: xufh
// * @date: 2022/11/18
// */
////@Component
//public class MetabaseJwtUtils {
//    private static String BI_SITE_URL = "http://120.27.18.244/metabase";
//
//    /**
//     * 创建token
//     */
//    public static String createJWT(Map<String, Object> payload, String secretKey) {
//        try {
//            String metaBaseEncodedSecretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
//            return Jwts.builder()
//                    .setHeaderParam("typ", "JWT")
//                    .setClaims(payload)
//                    .signWith(SignatureAlgorithm.HS256, metaBaseEncodedSecretKey)
//                    .setIssuedAt(new Date())
//                    .compact();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * 创建token
//     */
//    public static String getTokenUrl(Integer dashboard, String secretKey, Integer nightMode, Long value, Integer orgDataAuthState, Integer level) {
//        // 10 minute expiration
//        long exp = Math.round(System.currentTimeMillis()/1000) + 5;
//        JSONObject resource = new JSONObject();
//        resource.put("dashboard", dashboard);
//        JSONObject payload = new JSONObject();
//        payload.put("resource", resource);
//        if (orgDataAuthState.equals(1)) {
//            if (level == 1) {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("text_contains_1", 1);
//                jsonObject.put("text", 1);
//                payload.put("params", jsonObject);
//            } else {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("text_contains_1", 1);
//                jsonObject.put("text", 1);
//                payload.put("params", jsonObject);
//            }
//        } else {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("text_contains_1", 1);
//            jsonObject.put("text", 1);
//            payload.put("params", jsonObject);
//        }
////        payload.put("exp", exp);
//        System.out.println("payload = " + payload);
//        String token = createJWT(payload, secretKey);
//        if (nightMode.equals(0)) {
//            //白天模式
//            return BI_SITE_URL + "/embed/dashboard/" + token + "#bordered=true&titled=true";
//        } else {
//            //夜间模式
//            return BI_SITE_URL + "/embed/dashboard/" + token + "#theme=night&bordered=true&titled=true";
//        }
//    }
//
//    public static void main(String[] args) {
//        String secretKey = "8e636e43c8e649e1b2a6328a4e6a5e9cd980e70b11410ff35ff94d63cab760b8";
//        String url = getTokenUrl(2, secretKey, 1, 1L, 1, 1);
//        System.out.println(url);
//    }
//}
