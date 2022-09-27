package cn.doitedu.deepsea.akka_rpc;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DemoLocalTest {

    public static void main(String[] args) {

        //指定akka系统启动的端口
        String port = "5001";

        //加载local.conf的配置文件，并替换"akka.remote.artery.canonical.port"这个参数
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("akka.remote.artery.canonical.port", port);

        Config config = ConfigFactory.parseMap(overrides)
                .withFallback(ConfigFactory.load("local"));

        //启动akka系统，启动时指定一个behavior，并给系统命名
        ActorSystem system = ActorSystem.create("demoServer", config);
        log.info("=== actor system started ===");

        // 自己给自己发消息
        ActorRef demoServerRef = system.actorOf(Props.create(DemoServerActor.class));

        System.out.println(demoServerRef.path());

        demoServerRef.tell(new Command("haha"),ActorRef.noSender());


    }



}
