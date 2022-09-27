package cn.doitedu.deepsea.akka_rpc;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

public class DemoServerActor extends AbstractActor {
    @Override
    public Receive createReceive() {

        Receive receive = ReceiveBuilder.create()
                .match(Command.class, this::onCommand)
                .build();

        return receive;
    }

    public void onCommand(Command cmd){

        System.out.println("收到消息: " + cmd.getCmd());
    }

}
