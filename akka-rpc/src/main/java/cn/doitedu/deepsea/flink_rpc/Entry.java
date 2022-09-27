package cn.doitedu.deepsea.flink_rpc;

import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.rpc.akka.AkkaRpcService;
import org.apache.flink.runtime.rpc.akka.AkkaRpcServiceConfiguration;
import org.apache.flink.runtime.rpc.akka.AkkaUtils;
import org.apache.flink.runtime.rpc.akka.HostAndPort;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

/**
 * endpoint的接口必须在org.apache.flink包下
 * 否则在endpoint启动过程中，创建rpcserver（动态代理对象）时，会报错：接口类不可见
 * 因为底层 AkkaRpcService的类加载器是：SubmoduleClassloader
 *    -- AkkaRpcSystemLoader.loadRpcSystem() 方法中，是使用 SubmoduleClassLoader 进行加载的
 *            return new CleanupOnCloseRpcSystem(
 *                           ServiceLoader.load(RpcSystem.class, submoduleClassLoader).iterator().next(),
 *                           submoduleClassLoader,
 *                           tempFile);
 *   -- 而在创建这个submoduleClassLoader时，构造函数中指定了org.apache.flink包为submoduleClassloader优先加载
 *   public class SubmoduleClassLoader extends ComponentClassLoader {
 *     public SubmoduleClassLoader(URL[] classpath, ClassLoader parentClassLoader) {
 *         super(
 *                 classpath,
 *                 parentClassLoader,
 *                 CoreOptions.PARENT_FIRST_LOGGING_PATTERNS,
 *                 new String[] {"org.apache.flink"},
 *                 Collections.emptyMap());
 *     }
 * }
 *
 * 而在 AkkaRpcService.startServer(rpcEndpoint)启动rpcserver中创建动态代理对象时，
 * 往Proxy.newInstance()传入的classLoader是使用的是 SubmoduleClassloader
 * 因而，如果这些endpoint的接口类不再org.apache.flink包中时，SubmoduleClassloader将不可见
 */
public class Entry {


    public static void main(String[] args) throws Exception {
        /* *
         *
         * 创建rpcService 方式一：
         *   使用底层的serviceLoader来加载akkaRpcService
         *   将使用submoduleClassloader进行加载
         */
        // RpcSystem rpcSystem = RpcSystem.load(new Configuration());
        // RpcService remoteRpcService = RpcUtils.createRemoteRpcService(rpcSystem, new Configuration(), "localhost", "9999,19999", "localhost", Optional.of(8988));

        /* *
         * 创建rpcService 方式二：
         *   直接创建AkkaRpcService
         *   将使用系统classloader进行加载
         *   此时，endpoint的接口类，可以不放在org.apache.flink包下
         */
        Config config = AkkaUtils.getAkkaConfig(new Configuration(), new HostAndPort("localhost", 9999));
        ActorSystem actorSystem = AkkaUtils.createActorSystem("flink", config);

        // 本构造不可访问
        //   包可见，需要将本类放入org.apache.flink.runtime.rpc.akka包
        //   也可以人为防止一个源码在工程中，并将本构造方法改为public
        //AkkaRpcService remoteRpcService = new AkkaRpcService(actorSystem, AkkaRpcServiceConfiguration.defaultConfiguration(),ClassLoader.getSystemClassLoader());

        // 本构造外部可见  @VisibleForTesting
        AkkaRpcService remoteRpcService = new AkkaRpcService(actorSystem, AkkaRpcServiceConfiguration.defaultConfiguration());


        MyEndpoint myEndpoint = new MyEndpoint(remoteRpcService, "aaa");
        myEndpoint.start();

        System.out.println(myEndpoint.getAddress());

        MyRpcGateway selfGateway = myEndpoint.getSelfGateway(MyRpcGateway.class);
        selfGateway.say("self gateway 调用---------");


        CompletableFuture<MyRpcGateway> gatewayCompletableFuture = myEndpoint.getRpcService().connect("akka.tcp://flink@localhost:9999/user/rpc/aaa", MyRpcGateway.class);
        MyRpcGateway myRpcGateway = gatewayCompletableFuture.get();
        myRpcGateway.say("网络 gateway 调用---------");


    }
}
