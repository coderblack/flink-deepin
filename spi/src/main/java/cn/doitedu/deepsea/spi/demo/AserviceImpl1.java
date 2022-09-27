package cn.doitedu.deepsea.spi.demo;

import cn.doitedu.deepsea.spi.common.Aservice;

public class AserviceImpl1 implements Aservice {
    @Override
    public String sayMyname(String name) {
        return "1 " + name;
    }
}
