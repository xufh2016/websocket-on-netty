package com.coolfish.websocketonnetty.handler;

import com.coolfish.websocketonnetty.constants.IWebsocketSpecialCode;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @className: InitChannelHandler
 * @description: TODO 类描述
 * @author: xufh
 * @date: 2022/1/19
 */
@Component
@ChannelHandler.Sharable
@Scope("prototype")
@Slf4j
public class InitChannelHandler extends ChannelInitializer<SocketChannel> {

    private WebsocketChannelIdleHandler websocketChannelIdleHandler;
    private NettyWebSocketHandler nettyWebSocketHandler;
    private ExceptionHandler exceptionHandler;

    @Autowired
    public InitChannelHandler(WebsocketChannelIdleHandler websocketChannelIdleHandler
            , NettyWebSocketHandler nettyWebSocketHandler
            , ExceptionHandler exceptionHandler) {
        this.websocketChannelIdleHandler = websocketChannelIdleHandler;
        this.nettyWebSocketHandler = nettyWebSocketHandler;
        this.exceptionHandler = exceptionHandler;
    }


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        System.out.println("ctx.channel().id().asShortText()= " + ch.id().asShortText());
        // HttpServerCodec：将请求和应答消息解码为HTTP消息
        pipeline.addLast("Http-codec", new HttpServerCodec());
        // HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
        pipeline.addLast("Aggregator", new HttpObjectAggregator(65535));
        // ChunkedWriteHandler：向客户端发送HTML5文件,文件过大会将内存撑爆
        pipeline.addLast("Http-chunked", new ChunkedWriteHandler());
        pipeline.addLast("WebSocketAggregator", new WebSocketFrameAggregator(65535));
        //超过半分钟未发生写事件，则关闭连接 写空闲 600秒
        pipeline.addLast(new IdleStateHandler(0, 600, 0, TimeUnit.SECONDS));
        pipeline.addLast("WebsocketChannelIdleHandler", websocketChannelIdleHandler);
        pipeline.addLast("WebsocketHandler", nettyWebSocketHandler);
        //用于处理websocket, /ws为访问websocket时的uri
        pipeline.addLast("ProtocolHandler", new WebSocketServerProtocolHandler(IWebsocketSpecialCode.WS_PROTOCOL_SUFFIX, null, true, 65535));
        //设置最后的异常处理handler
        pipeline.addLast("ExceptionHandler", exceptionHandler);
    }

}
