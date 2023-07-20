package com.coolfish.websocketonnetty.handler;

import com.coolfish.websocketonnetty.config.NettyConfig;
import com.coolfish.websocketonnetty.constants.IWebsocketSpecialCode;
import com.coolfish.websocketonnetty.service.PushService;
import com.coolfish.websocketonnetty.util.RequestUriUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author xfh
 * websocket处理器
 * 注意：@ChannelHandler.Sharable：这个注解是为了线程安全，如果你不在乎是否线程安全，不加也可以；
 */
@Slf4j
@Component
@Scope("prototype")
@ChannelHandler.Sharable
public class NettyWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    String instModel = "";
    String instId = "";
    String username = "";

    @Resource
    private PushService pushService;
    /**
     * 该方法是protected修饰的方法，只能被当前类或其子类访问
     *
     * @param ctx
     * @param frame
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        System.out.println("channelRead0 ============================ ");
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

    /**
     * 该方法是public修饰的方法，可以被外部访问。channelRead方法中底层调用了channelRead0方法，其会先做消息类型检查，判断当前msg是否需要传递给下一个handler
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("ctx.channel().pipeline() = " + ctx.channel());
        System.out.println("channelRead ============================ ");
        log.info("客户端请求数据类型：{}", msg.getClass());
        if (msg instanceof FullHttpRequest) {
            fullHttpRequestHandler(ctx, (FullHttpRequest) msg);
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("2. NettyWebSocketHandler =============================== handlerAdded");
        log.info("handlerAdded被调用" + ctx.channel().id().asLongText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("2. NettyWebSocketHandler =============================== handlerRemoved");
        //NettyConfig.removeChannel(ctx.channel());
        NettyConfig.removeKeyChannel(instModel + ":" + instId + ":" + username, ctx.channel());
        System.out.println("------------------------移除连接通道---------------------");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("2. NettyWebSocketHandler =============================== channelActive");
        log.info("webSocket准备建立连接，channelActive被调用");
    }

    /*@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("2. NettyWebSocketHandler =============================== channelInactive");
        log.info("webSocket断开连接，channelInactive被调用");
    }*/

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        if (!channel.isActive()) {
            log.debug("客户端 " + channel.remoteAddress() + " 断开了连接！");
            log.debug(cause.getMessage());
            // 删除通道
            NettyConfig.removeKeyChannel(instModel + ":" + instId + ":" + username, channel);
            //NettyConfig.getChannelGroup().remove(ctx.channel());
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
        System.out.println("ctx.channel().id().asShortText()= " + ctx.channel().id().asShortText());
        List<Map.Entry<String, String>> entries = request.headers().entries();
        HttpHeaders headers = request.headers();
        String url = headers.get("Origin");
        //假如前端在请求时将Authentication放在headers中
        String authentication = headers.get("Authentication");
        System.out.println("url = " + url);
        entries.forEach(item -> {
            System.out.println("header.getKey() = " + item.getKey());
            System.out.println("header.getValue() = " + item.getValue());
        });
        //假如用户名和密码客户端封装在headers中，那么可以做如下操作
        //1. 认证用户
        //todo：从前端握手请求中提取用户相关信息
        //2. 将通过认证的用户存储到集合中
        String uri = request.uri();

        //if (StrUtil.isBlank(authentication)) {
        if (url.contains(IWebsocketSpecialCode.QUESTION_MARK)) {
            uri = url.split(IWebsocketSpecialCode.WS_PROTOCOL_SUFFIX)[1];
            Map<String, String> params = RequestUriUtils.getParams(uri);
            instModel = params.get("instModel");
            instId = params.get("instId");
            username = params.get("username");
            log.info("instModel------------>{},instId----------------->{},username----->{}", instModel, instId, username);
        } else if (uri.contains(IWebsocketSpecialCode.QUESTION_MARK)) {
            Map<String, String> params = RequestUriUtils.getParams(uri);
            instModel = params.get("instModel");
            instId = params.get("instId");
            username = params.get("username");
            log.info("instModel------------>{},instId----------------->{},username----->{}", instModel, instId, username);
        } else {
            // 判断请求路径是否跟配置中的一致
            List<String> pathParams = RequestUriUtils.getPathParams(uri);
            instModel = pathParams.get(0);
            instId = pathParams.get(1);
            username = pathParams.get(2);
            log.info("instModel------------>{},instId----------------->{},username----->{}", instModel, instId, username);
        }
        //}
        //todo :获取用户相关信息后进行认证，认证通过则连接，不通过则不连接
        if (IWebsocketSpecialCode.QUESTION_MARK.contains(RequestUriUtils.getBasePath(uri)) || IWebsocketSpecialCode.WS_PROTOCOL_SUFFIX.contains(RequestUriUtils.getPathParamBasePath(uri))) {
            // 因为有可能携带了参数，导致客户端一直无法返回握手包，因此在校验通过后，重置请求路径
            request.setUri(IWebsocketSpecialCode.WS_PROTOCOL_SUFFIX);
            // 连接成功后将该通道添加到map中
            //NettyConfig.addChannel(ctx.channel());
            NettyConfig.addKeyChannel(instModel + ":" + instId + ":" + username, ctx.channel());
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
        NettyConfig.removeKeyChannel(instModel + ":" + instId + ":" + username, ctx.channel());
        ctx.close();
    }

    /**
     * 创建连接之后，客户端发送的消息都会在这里处理
     *
     * @param ctx
     * @param frame
     */
    private void textWebSocketFrameHandler(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        // todo： 可以根据客户端发送过来的内容进行业务处理,如下
        // 获取前端发送过来的指令，根据前端发送的指令进行数据处理
        /*String wsKey=instModel + ":" + instId + ":" + username;
        String frontPageOrder = frame.text();
        if (frontPageOrder.equalsIgnoreCase("xxxx")){
            pushService.pushMsgToOneByKey(wsKey,"xxxx");
            pushService.pushMsgToAll("处理后的数据");
        }*/

        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(frame.text() + "--cloud");
        ctx.channel().writeAndFlush(textWebSocketFrame.retain());
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
