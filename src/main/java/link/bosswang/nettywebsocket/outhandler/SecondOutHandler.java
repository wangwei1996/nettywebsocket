package link.bosswang.nettywebsocket.outhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class SecondOutHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.err.println("Second Out Handler");
        ctx.writeAndFlush(msg);
    }
}
