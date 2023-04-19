package cn.doitedu.deepsea.my_rpc;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;

public class ServerEntry {

    public static void main(String[] args) {

        ActorSystem actorSystem = ActorSystem.create("server", ConfigFactory.load("application"));
        ActorRef s_actor = actorSystem.actorOf(Props.create(ServerActor.class), "s_actor");
        s_actor.tell(new ControllMsg(),ActorRef.noSender());

    }

}
