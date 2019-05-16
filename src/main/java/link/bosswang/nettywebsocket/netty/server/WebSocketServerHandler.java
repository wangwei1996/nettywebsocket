package link.bosswang.nettywebsocket.netty.server;


import com.alibaba.fastjson.JSONObject;
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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * WebSocket 服务端Handler
 *
 * @author wei
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
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //每当有一个新的用户添加到聊天室，就广播给大家
        System.err.println("Hello 欢迎");
        WebSocketServerHandler.CHANNELS.writeAndFlush(new TextWebSocketFrame("欢迎" + ctx.channel().id() + "加入聊天室"));
        WebSocketServerHandler.CHANNELS.add(ctx.channel());
        ctx.fireChannelActive();
    }

    private WebSocketServerHandshaker webSocketServerHandshaker;


    /**
     * 要求：不阻塞当前io线程
     *
     * @param channelHandlerContext
     * @param o
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        ChannelPipeline pipeline = channelHandlerContext.channel().pipeline();
        Iterator<Map.Entry<String, ChannelHandler>> iterator = pipeline.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ChannelHandler> next = iterator.next();
            System.err.println("======>>>>" + next.getValue().getClass());
        }
        //WebSocket连接握手发送的http请求
        if (o instanceof FullHttpRequest) {
            WebSocketServerHandler.log.info("FullHttpRequest: ------->");
            handlerHttpRequest(channelHandlerContext, (FullHttpRequest) o);
        } else if (o instanceof WebSocketFrame) {
            //WebSocket连接已经建立了
            WebSocketServerHandler.log.info("WebSocketFrame: ------->");
            handlerWebSocketFrame(channelHandlerContext, (WebSocketFrame) o);
        }
    }

    private void handlerHttpRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest httpRequest) {
        WebSocketServerHandler.log.info("=================>{}", "HTTP 请求");
        if (httpRequest.decoderResult().isFailure()) {
            sendHttpResponse(channelHandlerContext, httpRequest, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        //当存在协议提升时才去进行协议提升，建立websocket连接,否则就返回错误
        if (httpRequest.method() != HttpMethod.GET || !httpRequest.headers().contains("Upgrade")) {
            sendHttpResponse(channelHandlerContext, httpRequest, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
            return;
        }


        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(WebSocketServerHandler.getWebSocketLocation(httpRequest),
                null, true, 5 * 1024 * 1024);

        this.webSocketServerHandshaker = wsFactory.newHandshaker(httpRequest);
        if (this.webSocketServerHandshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(channelHandlerContext.channel());
            return;
        }

        ChannelFuture handshake = this.webSocketServerHandshaker.handshake(channelHandlerContext.channel(), httpRequest);
        Channel wsChannel = channelHandlerContext.channel();
    }

    private void handlerWebSocketFrame(ChannelHandlerContext channelHandlerContext, WebSocketFrame socketFrame) {
        //关闭请求
        if (socketFrame instanceof CloseWebSocketFrame) {
            this.webSocketServerHandshaker.close(channelHandlerContext.channel(), ((CloseWebSocketFrame) socketFrame).retain());
        } else if (socketFrame instanceof BinaryWebSocketFrame) {
            System.err.println(" 二进制消息: " + socketFrame.content().toString(CharsetUtil.UTF_8));
            //二进制数据
            String mess = socketFrame.content().toString(CharsetUtil.UTF_8);
            mess = mess.replaceAll("[\u0000]", "");
            JSONObject json = JSONObject.parseObject(mess);
            Map<String, Object> map = new LinkedHashMap<>(3);
            String type = json.get("type").toString();
            map.put("type", type);
            map.put("url", json.get("url"));
            map.put("user", channelHandlerContext.channel().id().toString());
            switch (type) {
                case "1": {
                    Iterator<Channel> channelIterator = WebSocketServerHandler.CHANNELS.iterator();
                    ChannelId id = channelHandlerContext.channel().id();
                    while (channelIterator.hasNext()) {
                        Channel channel = channelIterator.next();
                        if (channel.id().equals(id)) {
                            //不给自己发消息
                            continue;
                        }
                        channel.writeAndFlush(
                                new BinaryWebSocketFrame(Unpooled.copiedBuffer(JSONObject.toJSONString(map), CharsetUtil.UTF_8)));
                    }
                }
                break;
                case "3": {
                    channelHandlerContext.writeAndFlush(new BinaryWebSocketFrame(Unpooled.copiedBuffer(JSONObject.toJSONString(map), CharsetUtil.UTF_8)));
                }
                break;
            }

        } else if (socketFrame instanceof ContinuationWebSocketFrame) {
            //包含二进制数据或者文本数据
            channelHandlerContext.write(socketFrame.retain());
        } else if (socketFrame instanceof TextWebSocketFrame) {
            // 文本数据
            String mess = ((TextWebSocketFrame) socketFrame).text();
            WebSocketServerHandler.log.info("文本消息: {}------{}", mess, channelHandlerContext.channel().id());
            //消息群发
            Iterator<Channel> channelIterator = WebSocketServerHandler.CHANNELS.iterator();
            ChannelId id = channelHandlerContext.channel().id();
            while (channelIterator.hasNext()) {
                Channel channel = channelIterator.next();
                if (channel.id().equals(id)) {
                    //不给自己发消息
                    continue;
                }
                channel.writeAndFlush(
                        new TextWebSocketFrame(channelHandlerContext.channel().id() + "," + ((TextWebSocketFrame) socketFrame).text()));
            }
        } else if (socketFrame instanceof PingWebSocketFrame) {
            //Ping消息
            channelHandlerContext.write(new PongWebSocketFrame(socketFrame.content().retain()));
            System.err.println("客户端发来Ping消息");
        } else if (socketFrame instanceof PongWebSocketFrame) {
            //Pong 消息
            System.err.println("客户端发来Pong消息");
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
        WebSocketServerHandler.log.error("异常信息: {}", cause.getMessage());
        ctx.close();
    }
}
