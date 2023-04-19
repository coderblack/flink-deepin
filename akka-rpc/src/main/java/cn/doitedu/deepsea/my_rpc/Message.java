package cn.doitedu.deepsea.my_rpc;


import java.io.Serializable;

public class Message implements Serializable {

    private String className;
    private String methodName;
    private Object[] args;


    public Message(String className, String methodName, Object[] args) {
        this.className = className;
        this.methodName = methodName;
        this.args = args;
    }

    public Message() {
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getArgs() {
        return args;
    }
}
