package link.bosswang.nettywebsocket.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import link.bosswang.nettywebsocket.netty.outhandler.ServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebSocket 服务端
 *
 * @author wei
 */
public class WebSocketServer {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    /**
     * 服务端端口
     */
    private static final int PORT = 9999;

    /**
     * 启动Netty服务端
     *
     * @param args
     */
    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();


        ServerBootstrap server = new ServerBootstrap();
        server.group(bossGroup, workerGroup)
                //设置channel类型
                .channel(NioServerSocketChannel.class)
                //childOption 是一个LinkedHashMap
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                //childAttrs 是一个LinkedHashMap
                .childAttr(WebSocketServerHandler.ID, "13970261056")
                .handler(new ServerHandler())
                .childHandler(new WebSocketServerInitializer());
        try {
            Channel channel = server.bind(WebSocketServer.PORT).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            WebSocketServer.log.error(e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
