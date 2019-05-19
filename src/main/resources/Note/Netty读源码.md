#pipeline
  + Netty是如何判断ChannelHandler的类型的 ok
  + 对于ChannelHandler的添加应该注意什么顺序 ok
  + 用户手动触发事件传播，不同的触发方式有什么区别  ok


# 服务端
 + 服务端Socket在哪里初始化 ok
 + 在哪里accept连接 ok

 channelRegistered 当 Channel 已经注册到它的 EventLoop 并且能够处理 I/O 时被调用

 handlerAdded 当把 ChannelHandler 添加到 ChannelPipeline 中时被调用

 # NioEventLoop  
   + NioEventLoop 创建;
   + NioEventLoop 启动
   + NioEventLoop 执行逻辑
   + 默认情况下，Netty服务端启动多少线程？何时启动？
   + Netty是如何解决JDK空轮询的Bug
   + Netty如何保证异步串行无锁化
