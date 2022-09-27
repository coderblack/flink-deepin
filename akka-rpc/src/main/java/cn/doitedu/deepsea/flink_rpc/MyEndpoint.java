package cn.doitedu.deepsea.flink_rpc;

import org.apache.flink.runtime.rpc.RpcEndpoint;
import org.apache.flink.runtime.rpc.RpcService;

public class MyEndpoint extends RpcEndpoint implements MyRpcGateway {

    public MyEndpoint(RpcService rpcService, String endpointId) {
        super(rpcService, endpointId);
    }

    public MyEndpoint(RpcService rpcService) {
        super(rpcService);
    }


    @Override
    public void say(String something) {
        System.out.println(something);
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    @Override
    protected void onStart() throws Exception {

        System.out.println("endpoint正式启动完成");
    }
}
