package cn.doitedu.deepsea.akka_rpc;

import akka.actor.*;
import akka.japi.pf.ReceiveBuilder;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.*;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

import java.io.Serializable;


public class SomeThing {

    public static void main(String[] args) {

        Config config1 = ConfigFactory.load("application");
        ActorSystem sys1 = ActorSystem.create("sys1",config1);
        ActorRef xRef = sys1.actorOf(Props.create(XActor.class), "x");

        Config config2 = ConfigFactory.load("client");
        ActorSystem sys2 = ActorSystem.create("sys2",config2);
        ActorRef yRef = sys2.actorOf(Props.create(YActor.class), "y");


        ActorSelection actorSelection = sys1.actorSelection("akka://sys2@127.0.0.1:8077/user/y");

        actorSelection.tell(new Message("init"),xRef);
        //yRef.tell(new Message("init"),xRef);


    }

    public static class XActor extends AbstractActor{
        @Override
        public Receive createReceive() {

            Receive receive = ReceiveBuilder.create()
                    .match(Message.class,this::handleMessage)
                    .build();

            return receive;
        }

        public void handleMessage(Message msg){
            System.out.println(msg.getMsg());

            ActorRef self = getContext().getSelf();
            getContext().getSender().tell(new Message("X received"),self);
        }

    }

    public static class YActor extends AbstractActor{

        @Override
        public Receive createReceive() {

            Receive receive = ReceiveBuilder.create()
                    .match(Message.class,this::handleMessage)
                    .build();

            return receive;
        }

        public void handleMessage(Message msg){
            System.out.println(msg.getMsg());

            ActorRef self = getContext().getSelf();
            getContext().getSender().tell(new Message("Y received"),self);
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Message implements Serializable {
        private String msg;
    }


}
