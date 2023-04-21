package cn.doitedu.deepsea.my_rpc;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.pattern.Patterns;
import org.apache.flink.runtime.concurrent.akka.AkkaFutureUtils;
import scala.concurrent.Future;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ClientInvocationHandler implements InvocationHandler {

    ActorSelection targetRef;
    ActorRef selfRef;

    public ClientInvocationHandler(ActorSelection targetRef, ActorRef selfRef){
        this.targetRef = targetRef;
        this.selfRef = selfRef;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Message message = new Message("a.b.class", method.getName(), args);

        /*Future<Object> future = Patterns.ask(targetRef, message, 1000L);
        String o = (String) AkkaFutureUtils.toJava(future).get();*/

        targetRef.tell(message,selfRef);

        return null;
    }
}
