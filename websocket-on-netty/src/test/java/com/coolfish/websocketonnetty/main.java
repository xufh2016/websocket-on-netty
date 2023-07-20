package com.coolfish.websocketonnetty;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import java.util.List;

/**
 * @className: main
 * @description: TODO 类描述
 * @author: xufh
 * @date: 2022/10/24
 */
public class main {
    public static void main(String[] args) {
        String jsonStr = "[{\n" +
                "          \"processModelType\": \"model\",\n" +
                "          \"processModelKey\": \"aaa\",\n" +
                "          \"processModelValue\": [{\n" +
                "          \"processModelType\": \"String\",\n" +
                "          \"processModelKey\": \"bbb\",\n" +
                "          \"processModelValue\": \"xxx\"\n" +
                "        }]\n" +
                "        }]\n";
        test123(jsonStr);
    }

    public static void test123(String jsonStr) {
        List<ModelConvertorVo> modelConvertorVos = JSONUtil.toList(jsonStr, ModelConvertorVo.class);

        int id = 0;
        for (int i = 0; i < modelConvertorVos.size()-1; i++) {
            if (modelConvertorVos.get(i).getProcessModelType().equalsIgnoreCase("model")) {
                if (StrUtil.isNotBlank(modelConvertorVos.get(i).getProcessModelValue())) {
                    test123(modelConvertorVos.get(i).getProcessModelValue());
                }
                if (id > 0) {
                    modelConvertorVos.get(i).setProcessModelValue(id + "");
                }
                ++id;
            }
            System.out.println("item = " + modelConvertorVos.get(i));
        }

        /*modelConvertorVos.forEach(item -> {
            if (item.getProcessModelType().equalsIgnoreCase("model")) {
                if (StrUtil.isNotBlank(item.getProcessModelValue())) {
                    test123(item.getProcessModelValue());
                }
            }
            System.out.println("item = " + item);
        });*/
        /*int id = 0;
        for (int i = 0; i < modelConvertorVos.size(); i++) {
            if (id > 0) {
                modelConvertorVos.get(i).setProcessModelValue(id+"");
            }
            id++;
            System.out.println("modelConvertorVo = " + modelConvertorVos.get(i));
        }*/

    }


}
