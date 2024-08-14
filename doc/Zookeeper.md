### Zookeeper

zookeeper是经典的分布式数据一致性解决方案，致力于为分布式应用提供一个高性能，高可用，且具有严格顺序访问控制能力的分布式协调存储服务。

应用场景：

- 维护配置信息
  Java编程经常会遇到配置项，例如数据库的user、password等，通常配置信息会放在配置文件中，再把配置文件放在服务器上。当需要修改配置信息时，要去服务器上修改对应的配置文件，但在分布式系统中很多服务器都需要使用该配置文件，因此必须保证该配置服务的高可用性和各台服务器上配置的一致性。通常会将配置文件部署在一个集群上，但一个集群涉及的服务器数量是很庞大的，如果一台台服务器逐个修改配置文件是效率很低且危险的，因此需要一种服务可以高效快速且可靠地完成配置项的更改工作。
  zookeeper就可以提供这种服务，使用Zab一致性协议保证一致性。hbase中客户端就是连接zookeeper获得必要的hbase集群的配置信息才可以进一步操作。在开源消息队列Kafka中，也使用zookeeper来维护broker的信息。在dubbo中也广泛使用zookeeper管理一些配置来实现服务治理。

- 分布式锁服务
  一个集群是一个分布式系统，由多台服务器组成。为了提高并发度和可靠性，在多台服务器运行着同一种服务。当多个服务在运行时就需要协调各服务的进度，有时候需要保证当某个服务在进行某个操作时，其他的服务都不能进行该操作，即对该操作进行加锁，如果当前机器故障，释放锁并fall over到其他机器继续执行。

- 集群管理
  zookeeper会将服务器加入/移除的情况通知给集群中其他正常工作的服务器，以及即使调整存储和计算等任务的分配和执行等，此外zookeeper还会对故障的服务器做出诊断并尝试修复。

- 生成分布式唯一ID
  在过去的单库单表系统中，通常使用数据库字段自带的auto_increment熟悉自动为每条记录生成一个唯一的id。但分库分表后就无法依靠该属性来标识一个唯一的记录。此时可以使用zookeeper在分布式环境下生成全局唯一性id。每次要生成一个新id时，创建一个持久顺序结点，创建操作返回的结点序号，即为新id，然后把比自己结点小的删除。

设计目标

- 高性能
  将数据存储在内存中，直接服务于客户端的所有非事务请求，尤其适合读为主的应用场景
- 高可用
  一般以集群方式对外提供服务，每台机器都会在内存中维护当前的服务状态，每台机器之间都保持通信。只要集群中超过一般机器都能正常工作，那么整个集群就能够正常对外服务。
- 严格顺序访问
  对于客户端的每个更新请求，zookeeper都会生成全局唯一的递增编号，这个编号反应了所有事务操作的先后顺序。

#### 数据模型

- ZooKeeper 是一个树形目录服务,其数据模型和Unix的文件系统目录树很类似，拥有一个层次化结构。

- 这里面的每一个节点都被称为： ZNode，每个节点上都会保存自己的数据和节点信息。znode兼具文件和目录两种特点，既像文件一样维护着数据、元信息、ACL、时间戳等数据结构，又像目录一样可以作为路径标识的一部分。
  - znode大体上分为三部分（使用get命令查看）
    ①结点的数据
    ②结点的子结点
    ③结点的状态

- 节点可以拥有子节点，同时也允许少量（1MB）数据存储在该节点之下。

- 节点可以分为四大类：

  - PERSISTENT 持久化节点

  - EPHEMERAL 临时节点 ：-e

  - PERSISTENT_SEQUENTIAL 持久化顺序节点 ：-s

  - EPHEMERAL_SEQUENTIAL 临时顺序节点 ：-es

#### 安装

[windows环境下安装](https://blog.csdn.net/tttzzzqqq2018/article/details/132093374?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522172149339116800211548359%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&amp;request_id=172149339116800211548359&amp;biz_id=0&amp;utm_medium=distribute.pc_search_result.none-task-blog-2~all~top_positive~default-2-132093374-null-null.142^v100^pc_search_result_base9&amp;utm_term=zookeeper%E5%AE%89%E8%A3%85%E4%B8%8E%E9%85%8D%E7%BD%AE&amp;spm=1018.2226.3001.4187)

#### 命令操作：Linux环境下

##### 服务端常用命令：

- 启动 ZooKeeper 服务: ./zkServer.sh start

- 查看 ZooKeeper 服务状态: ./zkServer.sh status

- 停止 ZooKeeper 服务: ./zkServer.sh stop 

- 重启 ZooKeeper 服务: ./zkServer.sh restart 

##### 客户端常用命令：

- 连接ZooKeeper服务端：./zkCli.sh –server ip:port

- 断开连接：quit

- 显示指定目录下节点：ls 目录

- 创建节点：create /节点path value
- 设置节点值：set /节点path value

- 获取节点值：get /节点path

- 删除带有子节点的节点：deleteall /节点path
- 删除单个节点：delete /节点path

- 创建临时节点连接断开后自动删除：create -e /节点path value
- 创建顺序节点：create -s /节点path value
- 查询节点详细信息：ls –s /节点path 
  - czxid：节点被创建的事务ID ，ctime: 创建时间 ，mzxid: 最后一次被更新的事务ID ，mtime: 修改时间 ，pzxid：子节点列表最后一次被更新的事务ID，cversion：子节点的版本号 ，dataversion：数据版本号 ，aclversion：权限版本号 ，ephemeralOwner：用于临时节点，代表临时节点的事务ID，如果为持久节点则为0 ，dataLength：节点存储的数据的长度 ，numChildren：当前节点的子节点个数 

### Java API操作

#### Curator

- Curator 是 Apache ZooKeeper 的Java客户端库。

- 常见的ZooKeeper Java API ：
  - 原生Java API
  - ZkClient
  - Curator

- Curator 项目的目标是简化 ZooKeeper 客户端的使用。Curator 最初是 Netfix 研发的,后来捐献了 Apache 基金会,目前是 Apache 的顶级项目。需要根据安装的Zookeeper版本来选择Curator：参考官网地址http://curator.apache.org/

##### 建立连接

```xml
<dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
            <version>1.7.25</version>
        </dependency>
        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-1.2-api</artifactId>
            <version>2.8.2</version>
        </dependency>

        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>4.1.51.Final</version>
            <scope>compile</scope>
        </dependency>
        <!--这个jar包应该依赖log4j,不引入log4j会有控制台会有warn，但不影响正常使用-->
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-recipes</artifactId>
            <version>5.1.0</version>
        </dependency>
```

在测试类中编写：

```java
private CuratorFramework client;

    /**
     * 建立连接
     */
    @Before
    public void testConnect() {

        /*
         *
         * @param connectString       连接字符串。zk server 地址和端口"192.168.149.135:2181"
         * @param sessionTimeoutMs    会话超时时间 单位ms
         * @param connectionTimeoutMs 连接超时时间 单位ms
         * @param retryPolicy         重试策略
         */
       /* //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000,10);
        //1.第一种方式
        CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.149.135:2181",
                60 * 1000, 15 * 1000, retryPolicy);*/
        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
        //2.第二种方式
        //CuratorFrameworkFactory.builder();
        client = CuratorFrameworkFactory.builder()
                .connectString("10.211.55.5:2181")
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(15 * 1000)
                .retryPolicy(retryPolicy)
                .namespace("itheima")
                .build();
        //开启连接
        client.start();
    }
```

##### 添加节点

```java
/**
  * 创建节点：create 持久 临时 顺序 数据
  * 1. 基本创建 ：create().forPath("")
  * 2. 创建节点 带有数据:create().forPath("",data)
  * 3. 设置节点的类型：create().withMode().forPath("",data)
  * 4. 创建多级节点  /app1/p1 ：create().creatingParentsIfNeeded().forPath("",data)
  */
//1. 基本创建
//如果创建节点，没有指定数据，则默认将当前客户端的ip作为数据存储
String path = client.create().forPath("/app1");
//2. 创建节点 带有数据
//如果创建节点，没有指定数据，则默认将当前客户端的ip作为数据存储
String path = client.create().forPath("/app2", "hehe".getBytes());
//3. 设置节点的类型
//默认类型：持久化
String path = client.create().withMode(CreateMode.EPHEMERAL).forPath("/app3");
//4. 创建多级节点  /app1/p1
//creatingParentsIfNeeded():如果父节点不存在，则创建父节点
String path = client.create().creatingParentsIfNeeded().forPath("/app4/p1");
```

##### 查询节点

```java
/**
 * 查询节点：
 * 1. 查询数据：get: getData().forPath()
 * 2. 查询子节点： ls: getChildren().forPath()
 * 3. 查询节点状态信息：ls -s:getData().storingStatIn(状态对象).forPath()
 */
//1. 查询数据：get
byte[] data = client.getData().forPath("/app1");
// 2. 查询子节点： ls
List<String> path = client.getChildren().forPath("/");
//3. 查询节点状态信息：ls -s,数据映射到Stat类
Stat status = new Stat();
client.getData().storingStatIn(status).forPath("/app1");
```

##### 修改节点

```java
/**
 * 修改数据
 * 1. 基本修改数据：setData().forPath()
 * 2. 根据版本修改: setData().withVersion().forPath()
 * * version 是通过查询出来的。目的就是为了让其他客户端或者线程不干扰当前线程。（乐观锁）
 *
 * @throws Exception
 */
//1. 基本修改数据
client.setData().forPath("/app1", "itcast".getBytes());

//2. 根据版本修改
Stat status = new Stat();
client.getData().storingStatIn(status).forPath("/app1");
int version = status.getVersion();
client.setData().withVersion(version).forPath("/app1", "hehe".getBytes());
```

##### 删除节点

```java
/**
 * 删除节点： delete deleteall
 * 1. 删除单个节点:delete().forPath("/app1");
 * 2. 删除带有子节点的节点:delete().deletingChildrenIfNeeded().forPath("/app1");
 * 3. 必须成功的删除:为了防止网络抖动。本质就是重试。  client.delete().guaranteed().forPath("/app2");
 * 4. 回调：inBackground
 * @throws Exception
 */
// 1. 删除单个节点
client.delete().forPath("/app1");
//2. 删除带有子节点的节点
client.delete().deletingChildrenIfNeeded().forPath("/app4");
//3. 必须成功的删除不断重试
client.delete().guaranteed().forPath("/app2");
//4. 回调
client.delete().guaranteed().inBackground(new BackgroundCallback(){
    @Override
    public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
        System.out.println("我被删除了~");
        System.out.println(event);
    }
}).forPath("/app1");
```

##### Watch事件

- ZooKeeper 允许用户在指定节点上注册一些Watcher，并且在一些特定事件触发的时候，ZooKeeper 服务端会将事件通知到感兴趣的客户端上去，该机制是 ZooKeeper 实现分布式协调服务的重要特性。
- ZooKeeper 中引入了Watcher机制来实现了发布/订阅功能能，能够让多个订阅者同时监听某一个对象，当一个对象自身状态变化时，会通知所有订阅者。
- ZooKeeper 原生支持通过注册Watcher来进行事件监听，但是其使用并不是特别方便需要开发人员自己反复注册Watcher，比较繁琐。
- Curator引入了 Cache 来实现对 ZooKeeper 服务端事件的监听。
- Curator4中提供了三种Watcher：
  - NodeCache : 只是监听某一个特定的节点
  - PathChildrenCache : 监控一个ZNode的子节点. 
  - TreeCache : 可以监控整个树上的所有节点，类似于PathChildrenCache和NodeCache的组合
- Curator5中上述三种方式弃用，使用CuratorCache进行所有节点的事件监听，**CuratorCache**可以监听当前节点及其所有子节点的事件。(在本项目中将使用CuratorCache处理动态更新缓存)

**NodeCache :**

```java

/**
 * 演示 NodeCache：给指定一个节点注册监听器
 */

@Test
public void testNodeCache() throws Exception {
    //1. 创建NodeCache对象
    final NodeCache nodeCache = new NodeCache(client,"/app1");
    //2. 注册监听
    nodeCache.getListenable().addListener(new NodeCacheListener() {
        @Override
        public void nodeChanged() throws Exception {
            System.out.println("节点变化了~");

            //获取修改节点后的数据nodeCache.getCurrentData()获得data对象，需要再getData获得数据
            byte[] data = nodeCache.getCurrentData().getData();
            System.out.println(new String(data));
        }
    });
    //3. 开启监听.如果设置为true，则开启监听是，加载缓冲数据
    nodeCache.start(true);
    while (true){

    }
}
```

**PathChildrenCache**

```java
/**
     * 演示 PathChildrenCache：监听某个节点的所有子节点们
     */

    @Test
    public void testPathChildrenCache() throws Exception {
        //1.创建监听对象
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client,"/app2",true);

        //2. 绑定监听器
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                System.out.println("子节点变化了~");
                System.out.println(event);
                //监听子节点的数据变更，并且拿到变更后的数据
                //1.获取类型
                PathChildrenCacheEvent.Type type = event.getType();
                //2.判断类型是否是update
                if(type.equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)){
                    System.out.println("数据变了！！！");
                    byte[] data = event.getData().getData();
                    System.out.println(new String(data));

                }
            }
        });
        //3. 开启
        pathChildrenCache.start();

        while (true){

        }
    }

```

**TreeCache**

```java
/**
   * 演示 TreeCache：监听某个节点自己和所有子节点们
   */

  @Test
  public void testTreeCache() throws Exception {
      //1. 创建监听器
      TreeCache treeCache = new TreeCache(client,"/app2");

      //2. 注册监听
      treeCache.getListenable().addListener(new TreeCacheListener() {
          @Override
          public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
              System.out.println("节点变化了");
              System.out.println(event);
          }
      });

      //3. 开启
      treeCache.start();

      while (true){

      }
  }
```

**CuratorCache**

```java
        // curator 5.1.0：NODE_CREATED、NODE_CHANGED、NODE_DELETED
        CuratorCache curatorCache = CuratorCache.build(curatorFramework, "/curatorCache");
        curatorCache.listenable().addListener(new CuratorCacheListener() {
            /**
            *type:事件类型NODE_CREATED,NODE_CHANGED,NODE_DELETED
            *childData 更新前状态、数据
            *childData1 更新后状态、数据
            *创建节点时：节点刚被创建，不存在 更新前节点 ，所以第二个参数为 null
            *节点创建时没有赋予值 create /curator/app1 只创建节点，在这种情况下，更新前节点的 data 为 null，获取不到更新前节点的数据
            *删除节点时：节点被删除，不存在 更新后节点 ，所以第三个参数为 null
            */
            @Override
            public void event(Type type, ChildData oldData, ChildData data) {
                if (type.name().equals(CuratorCacheListener.Type.NODE_CREATED.name())) {
                //（注意：创建节点时，oldData为null）
                    log.info("A new node was added to the cache :{}",data.getPath());
                    //TODO...
                } else if (type.name().equals(CuratorCacheListener.Type.NODE_CHANGED.name())) {
                    log.info("A node already in the cache has changed :{}", data.getPath());
                    //TODO...
                } else {
                    //NODE_DELETED： node already in the cache was deleted.（注意：删除节点时，data为null）
                    log.info("A node already in the cache was deleted :{}", oldData.getPath());
                    //TODO...
                }
            }
        });
        curatorCache.start();

```

##### 分布式锁

- 在我们进行单机应用开发，涉及并发同步的时候，我们往往采用synchronized或者Lock的方式来解决多线程间的代码同步问题，这时多线程的运行都是在同一个JVM之下，没有任何问题。
- 但当我们的应用是分布式集群工作的情况下，属于多JVM下的工作环境，跨JVM之间已经无法通过多线程的锁解决同步问题。
- 那么就需要一种更加高级的锁机制，来处理种跨机器的进程之间的数据同步问题——这就是分布式锁。

原理：

核心思想：当客户端要获取锁，则创建节点，使用完锁，则删除该节点。

- 客户端获取锁时，在lock节点下创建==临时顺序==节点。
- 然后获取lock下面的所有子节点，客户端获取到所有的子节点之后，如果发现自己创建的子节点序号最小，那么就认为该客户端获取到了锁。使用完锁后，将该节点删除。
- 如果发现自己创建的节点并非lock所有子节点中最小的，说明自己还没有获取到锁，此时客户端需要找到比自己小的那个节点，同时对其注册事件监听器，监听删除事件。
- 如果发现比自己小的那个节点被删除，则客户端的Watcher会收到相应通知，此时再次判断自己创建的节点是否是lock子节点中序号最小的，如果是则获取到了锁，如果不是则重复以上步骤继续获取到比自己小的一个节点并注册监听。

在Curator中有五种锁方案：

- InterProcessSemaphoreMutex：分布式排它锁（非可重入锁）
- InterProcessMutex：分布式可重入排它锁
- InterProcessReadWriteLock：分布式读写锁
- InterProcessMultiLock：将多个锁作为单个实体管理的容器
- InterProcessSemaphoreV2：共享信号量

模拟售票操作：

```java
package com.itheima.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

public class Ticket12306 implements Runnable{
    private int tickets = 10;//数据库的票数
    private InterProcessMutex lock ;
    public Ticket12306(){
        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);
        //2.第二种方式
        //CuratorFrameworkFactory.builder();
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("10.211.55.5:2181")
                .sessionTimeoutMs(60 * 1000)
                .connectionTimeoutMs(15 * 1000)
                .retryPolicy(retryPolicy)
                .build();

        //开启连接
        client.start();
        lock = new InterProcessMutex(client,"/lock");
    }
    @Override
    public void run() {

        while(true){
            //获取锁
            try {
                lock.acquire(3, TimeUnit.SECONDS);
                if(tickets > 0){

                    System.out.println(Thread.currentThread()+":"+tickets);
                    Thread.sleep(100);
                    tickets--;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                //释放锁
                try {
                    lock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
```

##### 集群简单介绍

Leader选举：

- Serverid：服务器ID比如有三台服务器，编号分别是1,2,3。编号越大在选择算法中的权重越大。
- Zxid：数据ID服务器中存放的最大数据ID.值越大说明数据  越新，在选举算法中数据越新权重越大。
- 在Leader选举的过程中，如果某台ZooKeeper获得了超过半数的选票，则此ZooKeeper就可以成为Leader了。