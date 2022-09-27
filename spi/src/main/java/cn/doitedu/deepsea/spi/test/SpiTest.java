package cn.doitedu.deepsea.spi.test;

import cn.doitedu.deepsea.spi.common.Aservice;

import java.util.ServiceLoader;

public class SpiTest {
    public static void main(String[] args) {

        ServiceLoader<Aservice> loader = ServiceLoader.load(Aservice.class);
        for (Aservice aservice : loader) {
            System.out.println(aservice.sayMyname("doitedu deepsea"));
        }
    }
}
