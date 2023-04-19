package cn.doitedu.deepsea.my_rpc;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.japi.pf.ReceiveBuilder;

public class ClientActor extends AbstractActor {

    ClientEndpoint clientEndpoint;

    public ClientActor(ClientEndpoint clientEndpoint){
        this.clientEndpoint = clientEndpoint;

    }


    @Override
    public Receive createReceive() {

        Receive receive = ReceiveBuilder.create()
                .match(Message.class, this::handle)
                .match(ControllMsg.class,this::control)
                .build();

        return receive;
    }


    public void handle(Message message){
        System.out.println("收到服务端消息：" + message);
        clientEndpoint.fromServer(message.getArgs()[0].toString());
    }


    public void control(ControllMsg controllMsg){
        System.out.println("client 端actor已经就绪....");
    }

}
