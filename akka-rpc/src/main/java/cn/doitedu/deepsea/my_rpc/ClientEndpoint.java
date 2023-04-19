package cn.doitedu.deepsea.my_rpc;

public interface ClientEndpoint {

    public void connect(String serverPath);
    public void toUpper(String str);
    public String fromServer(String str);


}
