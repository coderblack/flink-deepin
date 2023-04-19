package cn.doitedu.deepsea.my_rpc;

import java.io.Serializable;


public class HandShake implements Serializable {

    private String msg;

    public HandShake(String msg) {
        this.msg = msg;
    }

    public HandShake() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
