package link.bosswang.nettywebsocket.netty.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author wei
 * 测试解码器
 */
public class FirstDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        int intSize = 4;
        if (byteBuf.readableBytes() >= intSize) {
            list.add(byteBuf.readInt());
        }

    }

    /**
     * 默认实现就是简单调用decode方法，当channel的状态变为非活动状态时，这个方法将会被调用一次。
     *
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        super.decodeLast(ctx, in, out);
    }
}
