package cn.doitedu.deepsea.akka_rpc;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.HashMap;
import java.util.Map;

public class DemoRemoteClient extends AbstractActor {

    public static void main(String[] args) {


        //加载local.conf的配置文件，并替换"akka.remote.artery.canonical.port"这个参数
        Map<String, Object> overrides = new HashMap<>();
        Config config = ConfigFactory.parseMap(overrides)
                .withFallback(ConfigFactory.load("client"));

        ActorSystem actorSystem = ActorSystem.create("clientsystem",config);
        ActorRef clientRef = actorSystem.actorOf(Props.create(DemoRemoteClient.class), "client");

        ActorSelection actorSelection = actorSystem.actorSelection("akka://rmt-server@127.0.0.1:8088/user/actor1");

        actorSelection.tell(new Command("hei:xiaozhang"),clientRef);


    }

    @Override
    public Receive createReceive() {

        Receive receive = ReceiveBuilder.create()
                .match(Command.class, msg -> System.out.println(msg.getCmd()))
                .build();

        return receive;
    }
}
