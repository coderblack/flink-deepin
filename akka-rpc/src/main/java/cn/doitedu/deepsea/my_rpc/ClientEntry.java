package cn.doitedu.deepsea.my_rpc;

import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;


public class ClientEntry {

    public static void main(String[] args) throws InterruptedException {

        ActorSystem actorSystem = ActorSystem.create("client", ConfigFactory.load("client"));

        ClientEndpoint clientEndpoint = new ClientEndpointImpl(actorSystem);
        clientEndpoint.connect("akka://server@127.0.0.1:8088/user/s_actor");


        Thread.sleep(1000);


        clientEndpoint.toUpper("haha");

    }
}
