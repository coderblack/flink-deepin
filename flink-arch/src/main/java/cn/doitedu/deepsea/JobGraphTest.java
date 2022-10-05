package cn.doitedu.deepsea;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.connector.source.*;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.core.io.SimpleVersionedSerializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;


public class JobGraphTest {

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        conf.setInteger("rest.port",8088);
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        env.setParallelism(2);

        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setTopics("tp01")
                .setGroupId("gp01")
                .setBootstrapServers("doit01:9092")
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST))
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setProperty("auto.offset.commit", "true")
                .build();


        // env.addSource();  //  接收的是  SourceFunction接口的 实现类
        DataStreamSource<String> streamSource = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "kfk-source");//  接收的是 Source 接口的实现类
        SingleOutputStreamOperator<String> s0 = streamSource.map(s -> s);


        DataStream<String> s1 = env.addSource(new MySource()).setParallelism(2);
        SingleOutputStreamOperator<String> s11 = s1.map(s -> s.toUpperCase()).name("s1:map");
        s11.print();
        SingleOutputStreamOperator<String> s12 = s1.filter(s -> s.startsWith("a")).name("s1:filter");


        DataStream<String> s2 = env.addSource(new MySource()).setParallelism(2).name("s2");
        SingleOutputStreamOperator<String> s21 = s2.map(String::toUpperCase).name("s2:map");






        s0.union(s12).union(s21)
                .map(s->s.toLowerCase()).name("union:map")
                .filter(s->s.startsWith("a")).name("union:filter")
                .print();

        env.execute();




    }

    public static class MySource extends RichParallelSourceFunction<String> {

        @Override
        public void run(SourceContext<String> sourceContext) throws Exception {
            while(true){
                sourceContext.collect("a");
                Thread.sleep(1000);
            }
        }

        @Override
        public void cancel() {

        }
    }

}
