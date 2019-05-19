package link.bosswang.nettywebsocket.netty.outhandler;

import io.netty.channel.*;

/**
 * First OutBound
 */
public class FirstOutHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("FirstOutHandler =================> " + promise.toString() + "---" + ctx.toString());
        ChannelFuture channelFuture = ctx.writeAndFlush(msg);

        channelFuture.addListeners(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    //需要将这里设置为success，之前的ChannelFuture.addListener里的代码才会执行(数据流转的问题？？)
                    promise.setSuccess();
                } else {
                    promise.setFailure(channelFuture.cause());
                }
            }
        });
    }
}
