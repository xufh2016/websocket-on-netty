package com.coolfish.websocketonnetty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author xfh
 */
@Slf4j
@Component
@Scope("prototype")
@ChannelHandler.Sharable
public class WebsocketChannelIdleHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("ctx.channel().id().asShortText()= " + ctx.channel().id().asShortText());
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            log.info("触发事件：", event.state().toString());
            switch (event.state()) {
                case READER_IDLE:
//                  发生读事件空闲，则告诉数据库及客户端设备已离线
                    break;
                case WRITER_IDLE:
                    log.warn("-------------发生写空闲，关闭连接-------------" + IdleState.WRITER_IDLE);
                    ctx.channel().close();
                    break;
                default:
                    break;
            }
        }  else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("WebsocketChannelIdleHandler =============================== handlerAdded" );
        super.handlerAdded(ctx);
    }
}
