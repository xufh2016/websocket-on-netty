//package com.coolfish.websocketonnetty;
//
//import com.alibaba.fastjson.JSONObject;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//
//import java.util.Base64;
//import java.util.Date;
//import java.util.Map;
//
//public class JWTUtils {
//
//	/**
//     * ����token
//     */
//    public static String createJWT(Map<String, Object> payload, String secretKey){
//        try {
//            String metaBaseEncodedSecretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
//            return Jwts.builder()
//                    .setHeaderParam("typ", "JWT")
//                    .setClaims(payload)
//                    .signWith(SignatureAlgorithm.HS256, metaBaseEncodedSecretKey)
//                    .setIssuedAt(new Date())
//                    .compact();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * ����token
//     */
//    public static String getTokenUrl(Integer dashboard,String secretKey){
//        int round = Math.round(System.currentTimeMillis() / 1000)+60 * 60  * 365 *50; // 10 minute expiration
//        JSONObject resource = new JSONObject();
//        resource.put("dashboard",dashboard);
//        JSONObject payload = new JSONObject();
//        payload.put("resource",resource);
//        payload.put("params",new JSONObject());
//        payload.put("exp",round);
//        String token = JWTUtils.createJWT(payload,secretKey);
//        return BI_SITE_URL + "/embed/dashboard/" + token + "#theme=night&bordered=false&titled=false";
//    }
//
//    private static final String BI_SITE_URL = "http://120.27.18.244:3000";
//
//    public static void main(String[] args) {
//        String secretKey = "8e636e43c8e649e1b2a6328a4e6a5e9cd980e70b11410ff35ff94d63cab760b8";
//        String url = getTokenUrl(1,secretKey);
//        System.out.println(url);
//    }
//
//}
