package cn.doitedu.deepsea.akka_rpc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Command implements Serializable {

    private String cmd;

}
