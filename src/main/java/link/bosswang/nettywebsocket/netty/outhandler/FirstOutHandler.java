package link.bosswang.nettywebsocket.netty.outhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * First OutBound
 */
public class FirstOutHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
       // System.err.println("FirstOutHandler");
        ctx.writeAndFlush(msg);
    }
}
