package cn.doitedu.deepsea.my_rpc;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;

import java.lang.reflect.Proxy;

public class ServerActor extends AbstractActor {

    ServerEndpoint serverEndpoint;
    ClientEndpoint clientEndpoint;

    public ServerActor(){
        this.serverEndpoint = new ServerEndpointImpl();
    }


    @Override
    public Receive createReceive() {

        Receive receive = ReceiveBuilder.create()
                .match(HandShake.class,this::handShake)
                .match(Message.class, this::handle)
                .match(ControllMsg.class,this::control)
                .build();

        return receive;
    }


    public void handle(Message message){
        System.out.println("服务端收到调用信息： " + message);

        if(message.getMethodName().equals("addSuffix")){

            String s = serverEndpoint.addSuffix(message.getArgs()[0].toString());
            System.out.println("服务端处理结果为： " + s);

            // 这里的真实调用应该是远程通信
            clientEndpoint.fromServer(s);
        }
    }


    public void handShake(HandShake handShake){

        this.clientEndpoint = (ClientEndpoint) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{ClientEndpoint.class},
                new ServerInvocationHandler(getSender(),getSelf()));

        System.out.println("握手成功，来者为: " + getSender().path());
    }


    public void control(ControllMsg controllMsg){
        System.out.println("server端actor已经就绪....");
    }

}
