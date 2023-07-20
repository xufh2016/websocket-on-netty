package com.coolfish.websocketonnetty.boot;

import com.coolfish.websocketonnetty.handler.InitChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.logging.LogLevel;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @className: WebsocketRunner
 * @description: websocket的服务端实现
 * @author: xufh
 * @date: 2022/9/8
 */
@Component
@Slf4j
public class WebsocketRunner implements ApplicationRunner {
    @Value("${netty.socket.port}")
    private int port;
    private InitChannelHandler initChannelHandler;
    private TaskExecutor taskExecutor;
    NioEventLoopGroup bossGroup;
    NioEventLoopGroup workerGroup;

    @Autowired
    public WebsocketRunner(InitChannelHandler initChannelHandler, @Qualifier(value = "taskExecutor") TaskExecutor taskExecutor) {
        this.initChannelHandler = initChannelHandler;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        taskExecutor.execute(this::bootStrap);
    }

    /**
     * websocket服务具体实现方法
     */
    public void bootStrap() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(port)
                    .handler(new LoggingHandler(String.valueOf(LogLevel.INFO)))
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(initChannelHandler)
                    .bind().sync();
            if (channelFuture.isSuccess()) {
                log.info("Websocket端口已启动，端口号是：{}", port);
            } else {
                log.info("Websocket启动失败");
            }
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.info("-------------InterruptedException-------------{}", e.getMessage());
        }
    }

    @PreDestroy
    public void shutdownLoopGroup() throws InterruptedException {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully().sync();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully().sync();
        }
    }
}
