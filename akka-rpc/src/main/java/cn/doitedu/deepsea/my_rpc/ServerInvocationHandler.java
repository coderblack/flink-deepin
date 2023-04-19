package cn.doitedu.deepsea.my_rpc;

import akka.actor.ActorRef;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServerInvocationHandler implements InvocationHandler {

    ActorRef targetRef;
    ActorRef selfRef;

    public ServerInvocationHandler(ActorRef targetRef, ActorRef selfRef){
        this.targetRef = targetRef;
        this.selfRef = selfRef;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Message message = new Message("a.b.class", method.getName(), args);
        targetRef.tell(message,selfRef);

        return null;
    }
}
