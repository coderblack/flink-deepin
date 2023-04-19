package cn.doitedu.deepsea.my_rpc;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.util.Timeout;
import scala.concurrent.Future;

import java.lang.reflect.Proxy;
import java.time.Duration;

public class ClientEndpointImpl implements ClientEndpoint {

    ActorSystem actorSystem;
    ActorRef c_actor;
    ActorSelection serverActorSelection;

    ServerEndpoint serverEndpoint;


    public ClientEndpointImpl(ActorSystem actorSystem){

        this.actorSystem = actorSystem;
    }

    @Override
    public void connect(String serverPath){
        this.serverActorSelection = actorSystem.actorSelection(serverPath);
        c_actor = actorSystem.actorOf(Props.create(ClientActor.class ,this),"c_actor");

       // 与服务端握手
        serverActorSelection.tell(new HandShake("hds"),c_actor);

        // 构造服务端 gateway
        this.serverEndpoint = (ServerEndpoint) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{ServerEndpoint.class}, new ClientInvocationHandler(serverActorSelection, c_actor));

    }

    @Override
    public void toUpper(String str) {
        serverEndpoint.addSuffix(str);

    }

    @Override
    public String fromServer(String str) {

        System.out.println("收到suffix远程调用返回： " + str);

        return str.toLowerCase();
    }

}
