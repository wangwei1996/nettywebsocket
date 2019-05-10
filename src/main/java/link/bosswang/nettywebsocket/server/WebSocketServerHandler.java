package link.bosswang.nettywebsocket.server;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * WebSocket 服务端Handler
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final ChannelGroup CHANNELS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final Logger log = LoggerFactory.getLogger(WebSocketServerHandler.class);
    private static final String WEBSOCKET_PATH = "/websocket";


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        WebSocketServerHandler.CHANNELS.remove(ctx.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        WebSocketServerHandler.CHANNELS.add(ctx.channel());

        Iterator<Channel> iterator = WebSocketServerHandler.CHANNELS.iterator();
        while (iterator.hasNext()) {
            Channel channel = iterator.next();
            System.err.println("Channel id: " + channel.id());
        }
    }

    /**
     * 保存Channel
     */
    private static final List<Channel> SERVER_CHANNEL = new ArrayList<>(10);


    private WebSocketServerHandshaker webSocketServerHandshaker;


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if (o instanceof FullHttpRequest) {
            WebSocketServerHandler.log.info("FullHttpRequest: ------->");
            handlerHttpRequest(channelHandlerContext, (FullHttpRequest) o);
        } else if (o instanceof WebSocketFrame) {
            WebSocketServerHandler.log.info("WebSocketFrame: ------->");
            handlerWebSocketFrame(channelHandlerContext, (WebSocketFrame) o);
        }
    }

    private void handlerHttpRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest httpRequest) {
        WebSocketServerHandler.log.info("=================>{}", "HTTP 请求");
        if (httpRequest.decoderResult().isFailure()) {
            this.sendHttpResponse(channelHandlerContext, httpRequest, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        if (httpRequest.method() != HttpMethod.GET) {
            this.sendHttpResponse(channelHandlerContext, httpRequest, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
            return;
        }

        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(WebSocketServerHandler.getWebSocketLocation(httpRequest),
                null, true, 5 * 1024 * 1024);

        this.webSocketServerHandshaker = wsFactory.newHandshaker(httpRequest);
        if (this.webSocketServerHandshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channelHandlerContext.channel());
            return;
        }

        this.webSocketServerHandshaker.handshake(channelHandlerContext.channel(), httpRequest);
        WebSocketServerHandler.SERVER_CHANNEL.add(channelHandlerContext.channel());
    }

    private void handlerWebSocketFrame(ChannelHandlerContext channelHandlerContext, WebSocketFrame socketFrame) {

        if (socketFrame instanceof CloseWebSocketFrame) {//关闭请求
            this.webSocketServerHandshaker.close(channelHandlerContext.channel(), ((CloseWebSocketFrame) socketFrame).retain());
        } else if (socketFrame instanceof BinaryWebSocketFrame) {//二进制数据
            channelHandlerContext.write(socketFrame.retain());
        } else if (socketFrame instanceof ContinuationWebSocketFrame) {//包含二进制数据或者文本数据
            channelHandlerContext.write(socketFrame.retain());
        } else if (socketFrame instanceof TextWebSocketFrame) {  // 文本数据
            String mess = ((TextWebSocketFrame) socketFrame).text();
            WebSocketServerHandler.log.info("文本消息: {}------{}", mess, channelHandlerContext.channel().id());

            //广播给所有人
            WebSocketServerHandler.CHANNELS.writeAndFlush(new TextWebSocketFrame(channelHandlerContext.channel().id() + "发来消息: " + mess));
        } else if (socketFrame instanceof PingWebSocketFrame) { //Ping消息
            channelHandlerContext.write(new PongWebSocketFrame(socketFrame.content().retain()));
        } else if (socketFrame instanceof PongWebSocketFrame) {//Pong 消息
            channelHandlerContext.write(new PingWebSocketFrame(socketFrame.content().retain()));
        } else {
            throw new RuntimeException("未知消息");
        }
    }

    private static void sendHttpResponse(ChannelHandlerContext handlerContext, FullHttpRequest request, FullHttpResponse response) {
        if (response.status() != HttpResponseStatus.OK) {
            ByteBuf buff = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buff);
            buff.release();
            HttpUtil.setContentLength(response, response.content().readableBytes());
        }

        ChannelFuture channelFuture = handlerContext.channel().writeAndFlush(response);
        if (!HttpUtil.isKeepAlive(request) || response.status() != HttpResponseStatus.OK) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }


    private static String getWebSocketLocation(FullHttpRequest req) {
        String location = req.headers().get(HttpHeaderNames.HOST) + WebSocketServerHandler.WEBSOCKET_PATH;
        return "ws://" + location;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        WebSocketServerHandler.log.error("异常信息: {}", cause.getMessage());
    }
}
