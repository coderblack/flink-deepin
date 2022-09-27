package cn.doitedu.deepsea.flink_rpc;

import org.apache.flink.runtime.rpc.RpcGateway;

public interface MyRpcGateway extends RpcGateway {
    void say(String something);
}
