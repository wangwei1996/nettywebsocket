#pipeline
  + Netty是如何判断ChannelHandler的类型的 状态:ok AbstractChannelHandlerContext成员属性executionMask（4.0 -> 4.1 由inbound，outbound属性改变为由成员属性executionMask来表示）
  + 对于ChannelHandler的添加应该注意什么顺序 ok 
  + 用户手动触发事件传播，不同的触发方式有什么区别  ok 
  + 异常的传播(最终是传递到TailContext)


# 服务端
 + 服务端Socket在哪里初始化 ok
 + 在哪里accept连接 ok

# NioEventLoopGroup
+ poolName   nioEventLoopGroup(首字母小写) 线程组前缀:poolName + '-' + poolId.incrementAndGet() + '-'  nextId.incrementAndGet() ;
# NioEventLoop  
   + NioEventLoop 创建;
   + NioEventLoop 启动
   + NioEventLoop 执行逻辑
   + 默认情况下，Netty服务端启动多少线程？何时启动？
   + Netty是如何解决JDK空轮询的Bug
   + Netty如何保证异步串行无锁化
   
   
# GoodThings
+ Java中isAssignableFrom的用法
  - class1.isAssignableFrom(class2) 判定此 Class 对象所表示的类或接口与指定的 Class 参数所表示的类或接口是否相同，或是否(class1)是其(class2)超类或超接口
    + class2是不是class1的子类或者子接口
    + Object是所有类的父类
    
    
# 
+ DefaultSelectStrategyFactory 默认的选择策略
+ RejectedExecutionHandlers 线程池拒绝策略


ThreadPerTaskExecutor 是啥？什么作用
FastThreadLocalThread

# Class  为什么要自己实现，这与JDK自带的有什么不同
FastThreadLocalThread extends Thread 
InternalThreadLocalMap 


线程组：
线程组存在的意义，首要原因是安全。
java默认创建的线程都是属于系统线程组，而同一个线程组的线程是可以相互修改对方的数据的。
但如果在不同的线程组中，那么就不能“跨线程组”修改数据，可以从一定程度上保证数据安全.

线程池：
线程池存在的意义，首要作用是效率。
线程的创建和结束都需要耗费一定的系统时间（特别是创建），不停创建和删除线程会浪费大量的时间。所以，在创建出一条线程并使其在执行完任务后不结束，而是使其进入休眠状态，在需要用时再唤醒，那么 就可以节省一定的时间。
如果这样的线程比较多，那么就可以使用线程池来进行管理。保证效率。

线程组和线程池共有的特点：
1,都是管理一定数量的线程
2,都可以对线程进行控制---包括休眠，唤醒，结束，创建，中断（暂停）--但并不一定包含全部这些操作。

