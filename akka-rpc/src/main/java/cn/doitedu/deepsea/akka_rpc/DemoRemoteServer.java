package cn.doitedu.deepsea.akka_rpc;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

public class DemoRemoteServer extends AbstractActor {

    @Override
    public Receive createReceive() {
        Receive receive = ReceiveBuilder.create()
                .match(Command.class, this::handleCommand)
                .build();
        return receive;
    }

    public void handleCommand(Command cmd){
        System.out.println(cmd.getCmd());
        if(cmd.getCmd().startsWith("hei")) {
            getContext().getSender().tell(new Command("来自服务器"), getSelf());
        }
    }

    public static void main(String[] args) {
        ActorSystem actorSystem = ActorSystem.create("rmt-server");
        ActorRef selfRef = actorSystem.actorOf(Props.create(DemoRemoteServer.class),"actor1");
        System.out.println(selfRef.path());


        selfRef.tell(new Command("self"),ActorRef.noSender());
    }
}
