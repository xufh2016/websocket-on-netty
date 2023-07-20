package com.coolfish.websocketonnetty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

/**
 * @className: CheckJsonUtil
 * @description: TODO 类描述
 * @author: xufh
 * @date: 2022/11/29
 */
public class CheckJsonUtils {
    public static boolean isJson(String jsonStr){
        try {
            JSON.parse(jsonStr);
            return true;
        }catch (JSONException e){
            System.out.println("e = " + e.getMessage().split("fastjson-version 2.0.19")[0]);
//            System.out.println("e.getLocalizedMessage() = " + e.getLocalizedMessage());
            return false;
        }
    }
}
