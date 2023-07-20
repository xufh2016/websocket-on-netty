package com.coolfish.websocketonnetty;

import lombok.Data;

import java.io.Serializable;

/**
 * @className: ModelConvertorVo
 * @description: TODO 类描述
 * @author: xufh
 * @date: 2022/10/17
 */
@Data
public class ModelConvertorVo implements Serializable {
    /**
     * 模型的key parm type
     */
    private String processModelType;
    /**
     * 原型中的模型参数2
     */
    private String processModelKey;
    /**
     * 表达式等，原型中的输入框
     */
    private String processModelValue;

//    ModelConvertorVo modelConvertorVo;
}
