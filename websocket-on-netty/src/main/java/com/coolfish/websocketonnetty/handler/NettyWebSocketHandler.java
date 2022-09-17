package com.coolfish.websocketonnetty.handler;

import com.coolfish.websocketonnetty.config.NettyConfig;
import com.coolfish.websocketonnetty.util.RequestUriUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author xfh
 * websocket处理器
 */
@Slf4j
@Component
@Scope("prototype")
@ChannelHandler.Sharable
public class NettyWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        // 根据请求数据类型进行分发处理
        if (frame instanceof PingWebSocketFrame) {
            pingWebSocketFrameHandler(ctx, (PingWebSocketFrame) frame);
        } else if (frame instanceof TextWebSocketFrame) {
            textWebSocketFrameHandler(ctx, (TextWebSocketFrame) frame);
        } else if (frame instanceof CloseWebSocketFrame) {
            closeWebSocketFrameHandler(ctx, (CloseWebSocketFrame) frame);
        } else if (frame instanceof PongWebSocketFrame) {
            System.out.println(" pong frame");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("客户端请求数据类型：{}", msg.getClass());
        if (msg instanceof FullHttpRequest) {
            fullHttpRequestHandler(ctx, (FullHttpRequest) msg);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerAdded被调用" + ctx.channel().id().asLongText());
        NettyConfig.getChannelGroup().add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("移除webSocket连接，ChannelId：" + ctx.channel().id().asLongText());
        NettyConfig.getChannelGroup().remove(ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("webSocket建立连接");

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("webSocket断开连接");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        if (!channel.isActive()) {
            log.debug("客户端 " + channel.remoteAddress() + " 断开了连接！");
            log.debug(cause.getMessage());
            // 删除通道
            NettyConfig.getChannelGroup().remove(ctx.channel());
            ctx.close();
        } else {
            ctx.fireExceptionCaught(cause);
            log.debug("-------------------------------------{}", cause);
        }
    }

    /**
     * 处理连接请求，客户端WebSocket发送握手包时会执行这一次请求
     *
     * @param ctx
     * @param request
     */
    private void fullHttpRequestHandler(ChannelHandlerContext ctx, FullHttpRequest request) {
        List<Map.Entry<String, String>> entries = request.headers().entries();
        entries.forEach(item -> {
            System.out.println("item.getKey() = " + item.getKey());
            System.out.println("item.getValue() = " + item.getValue());
        });
        String uri = request.uri();
        Map<String, String> params = RequestUriUtils.getParams(uri);
        log.debug("客户端请求参数：{}", params);
        // 判断请求路径是否跟配置中的一致
        List<String> pathParams = RequestUriUtils.getPathParams(uri);
        pathParams.forEach(item -> {
            System.out.println("item = " + item);
        });
        if ("?".contains(RequestUriUtils.getBasePath(uri)) || "/ws".contains(RequestUriUtils.getPathParamBasePath(uri))) {
            // 因为有可能携带了参数，导致客户端一直无法返回握手包，因此在校验通过后，重置请求路径
            request.setUri("/ws");
        } else {
            ctx.close();
        }
    }

    /**
     * 客户端发送断开请求处理
     *
     * @param ctx
     * @param frame
     */
    private void closeWebSocketFrameHandler(ChannelHandlerContext ctx, CloseWebSocketFrame frame) {
        ctx.close();
    }

    /**
     * 创建连接之后，客户端发送的消息都会在这里处理
     *
     * @param ctx
     * @param frame
     */
    private void textWebSocketFrameHandler(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        // 客户端发送过来的内容不进行业务处理，原样返回
        ctx.channel().writeAndFlush(frame.retain());
    }

    /**
     * 处理客户端心跳包
     *
     * @param ctx
     * @param frame
     */
    private void pingWebSocketFrameHandler(ChannelHandlerContext ctx, PingWebSocketFrame frame) {
        ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
    }

}
