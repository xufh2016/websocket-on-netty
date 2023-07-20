package com.coolfish.websocketonnetty;

/**
 * @className: LanguageEntity
 * @description: TODO 类描述
 * @author: xufh
 * @date: 2022/9/17
 */
public class LanguageEntity {
    private String version;
    private String codeUrl;

    /**
     * 该方法会在发生GC之后调用
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("finalize 方法被调用了@！！！！！ " );
    }
}
