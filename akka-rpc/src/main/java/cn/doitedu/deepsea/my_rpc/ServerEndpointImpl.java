package cn.doitedu.deepsea.my_rpc;

import org.apache.commons.lang3.RandomUtils;

public class ServerEndpointImpl implements ServerEndpoint {

    @Override
    public String addSuffix(String str) {
        return str+"_"+ RandomUtils.nextInt(1,100);
    }

    @Override
    public String trimSuffix(String str) {
        return str.split("_")[0];
    }
}
