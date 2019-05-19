package link.bosswang.nettywebsocket.netty.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 打印消息
 */
public class ShowMessageHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger log = LoggerFactory.getLogger(ShowMessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        Attribute<String> id = channelHandlerContext.channel().attr(AttributeKey.<String>newInstance("id"));
        if (o instanceof WebSocketFrame) {
            System.err.println("ShowMessageHandler =====> WebSocket 发来消息: " + ((TextWebSocketFrame) o).text() + "id: " + id.get());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("ShowMessageHandler 处理出现异常。异常信息: " + cause.getMessage());
        ctx.close();
    }
}
