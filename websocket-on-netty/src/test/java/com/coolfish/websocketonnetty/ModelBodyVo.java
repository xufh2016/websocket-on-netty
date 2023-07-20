package com.coolfish.websocketonnetty;

import lombok.Data;

import java.io.Serializable;

/**
 * @className: ModelBodyVo
 * @description: TODO 类描述
 * @author: xufh
 * @date: 2022/10/17
 */
@Data
public class ModelBodyVo implements Serializable {
    private String paramName;
    private String dataType;
    private String unitOrModel;
}
