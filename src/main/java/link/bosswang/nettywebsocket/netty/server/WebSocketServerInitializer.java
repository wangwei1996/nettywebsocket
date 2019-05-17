package link.bosswang.nettywebsocket.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.IdleStateHandler;
import link.bosswang.nettywebsocket.netty.outhandler.FirstOutHandler;
import link.bosswang.nettywebsocket.netty.outhandler.SecondOutHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author wei
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //将字节解码为 HttpRequest 、 HttpContent 和 LastHttp-Content 。并将 HttpRequest 、 HttpContent 和 Last-HttpContent 编码为字节
        pipeline.addLast(new HttpServerCodec());
        //启用压缩HTTP消息，减小传输数据的大小
        pipeline.addLast("compressor", new HttpContentCompressor());
        //安装了这个之后,ChannelPipeline 中的下一个 ChannelHandler 将只会收到完整的 HTTP 请求或响应
        //aggregates an HttpMessage and its following HttpContents into a single FullHttpRequest or FullHttpResponse
        // (depending on if it used to handle requests or responses) with no following HttpContents
        pipeline.addLast(new HttpObjectAggregator(65535));
        //当在180秒内没有接收或者发送任何数据，那么IdleStateHandler将会使用一个IdleStateEvent时间来调用fireUserEventTriggered方法
        pipeline.addLast("TimeOutCheck", new IdleStateHandler(0, 0, 180, TimeUnit.SECONDS));
        //out
        pipeline.addLast("second", new SecondOutHandler());
        pipeline.addLast("first", new FirstOutHandler());
        pipeline.addLast(new WebSocketServerHandler());
        //打印WebSocket消息
      //  pipeline.addLast("showMess", new ShowMessageHandler());
        //心跳处理
     //   pipeline.addLast("HeartBeat", new HeartBeatHandler());
    }
}
