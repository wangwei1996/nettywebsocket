package link.bosswang.nettywebsocket.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author wei
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //将字节解码为 HttpRequest 、 HttpContent 和 LastHttp-Content 。并将 HttpRequest 、 HttpContent 和 Last-HttpContent 编码为字节
        pipeline.addLast(new HttpServerCodec());
        //安装了这个之后,ChannelPipeline 中的下一个 ChannelHandler 将只会收到完整的 HTTP 请求或响应
        //aggregates an HttpMessage and its following HttpContents into a single FullHttpRequest or FullHttpResponse
        // (depending on if it used to handle requests or responses) with no following HttpContents
        pipeline.addLast(new HttpObjectAggregator(65535));
        pipeline.addLast(new WebSocketServerHandler());
    }
}
