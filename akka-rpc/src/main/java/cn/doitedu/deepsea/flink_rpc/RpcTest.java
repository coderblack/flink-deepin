package cn.doitedu.deepsea.flink_rpc;

import akka.actor.Actor;
import akka.actor.ActorSystem;
import akka.actor.Terminated;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.rpc.RpcEndpoint;
import org.apache.flink.runtime.rpc.RpcGateway;
import org.apache.flink.runtime.rpc.RpcService;
import org.apache.flink.runtime.rpc.akka.AkkaRpcService;
import org.apache.flink.runtime.rpc.akka.AkkaRpcServiceConfiguration;
import org.apache.flink.runtime.rpc.akka.AkkaUtils;
import org.apache.flink.runtime.rpc.akka.HostAndPort;
import org.apache.flink.util.concurrent.FutureUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import scala.compat.java8.FutureConverters;
import scala.concurrent.Future;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class RpcTest {

    private static final Time TIMEOUT = Time.seconds(10L);
    private static ActorSystem actorSystem = null;
    private static RpcService rpcService = null;


    // 定义通信协议
    public interface HelloGateway extends RpcGateway {
        String hello();
    }

    public interface HiGateway extends RpcGateway {
        String hi();
    }

    // 具体实现
    public static class HelloRpcEndpoint extends RpcEndpoint implements HelloGateway {
        protected HelloRpcEndpoint(RpcService rpcService) {
            super(rpcService);
        }

        protected HelloRpcEndpoint(RpcService rpcService, String endpointId) {
            super(rpcService, endpointId);
        }

        @Override
        public String hello() {
            return "hello";
        }
    }

    public static class HiRpcEndpoint extends RpcEndpoint implements HiGateway {
        protected HiRpcEndpoint(RpcService rpcService) {
            super(rpcService);
        }

        protected HiRpcEndpoint(RpcService rpcService, String endpointId) {
            super(rpcService, endpointId);
        }

        @Override
        public String hi() {
            return "hi";
        }
    }

    @BeforeClass
    public static void setup() {

        ActorSystem actorSystem2 = AkkaUtils.createDefaultActorSystem();
        actorSystem = AkkaUtils.createActorSystem("haha", AkkaUtils.getAkkaConfig(new Configuration(), new HostAndPort("localhost", 8088)));

        // 创建 RpcService， 基于 AKKA 的实现
        rpcService = new AkkaRpcService(actorSystem, AkkaRpcServiceConfiguration.defaultConfiguration());
    }

    @AfterClass
    public static void teardown() throws Exception {

         final CompletableFuture<Void> rpcTerminationFuture = rpcService.stopService();


        Future<Terminated> terminate = actorSystem.terminate();
        // utils已经没有这个方法了
        //final CompletableFuture<Terminated> actorSystemTerminationFuture = FutureUtils.toJava(terminate);
        // 使用scala自带的工具进行转换
        final CompletableFuture<Terminated> actorSystemTerminationFuture = FutureConverters.<Terminated>toJava(terminate).toCompletableFuture();

        FutureUtils
                .waitForAll(Arrays.asList(rpcTerminationFuture, actorSystemTerminationFuture))
                .get(TIMEOUT.toMilliseconds(), TimeUnit.MILLISECONDS);
    }

    @Test
    public void test() throws Exception {
        HelloRpcEndpoint helloEndpoint = new HelloRpcEndpoint(rpcService, "hello");
        HiRpcEndpoint hiEndpoint = new HiRpcEndpoint(rpcService, "hi");

        helloEndpoint.start();
        //获取 endpoint 的 self gateway
        HelloGateway helloGateway = helloEndpoint.getSelfGateway(HelloGateway.class);
        String hello = helloGateway.hello();
        assertEquals("hello", hello);

        hiEndpoint.start();

        System.out.println(hiEndpoint.getAddress());

        // 通过 endpoint 的地址获得代理
        //HiGateway hiGateway = rpcService.connect(hiEndpoint.getAddress(),HiGateway.class).get();
        HiGateway hiGateway = rpcService.connect("akka.tcp://haha@localhost:8088/user/rpc/hi", HiGateway.class).get();
        String hi = hiGateway.hi();
        assertEquals("hi", hi);
    }



}
