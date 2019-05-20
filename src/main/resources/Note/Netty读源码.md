#pipeline
  + Netty是如何判断ChannelHandler的类型的 状态:ok AbstractChannelHandlerContext成员属性executionMask（4.0 -> 4.1 由inbound，outbound属性改变为由成员属性executionMask来表示）
  + 对于ChannelHandler的添加应该注意什么顺序 ok 
  + 用户手动触发事件传播，不同的触发方式有什么区别  ok 
  + 异常的传播


# 服务端
 + 服务端Socket在哪里初始化 ok
 + 在哪里accept连接 ok

# NioEventLoop  
   + NioEventLoop 创建;
   + NioEventLoop 启动
   + NioEventLoop 执行逻辑
   + 默认情况下，Netty服务端启动多少线程？何时启动？
   + Netty是如何解决JDK空轮询的Bug
   + Netty如何保证异步串行无锁化
   
   
# GoodThings
+ Java中isAssignableFrom的用法
  - class1.isAssignableFrom(class2) 判定此 Class 对象所表示的类或接口与指定的 Class 参数所表示的类或接口是否相同，或是否是其超类或超接口
    + class2是不是class1的子类或者子接口
    + Object是所有类的父类
