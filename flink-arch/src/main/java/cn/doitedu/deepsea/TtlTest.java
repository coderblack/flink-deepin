package cn.doitedu.deepsea;

import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.util.Iterator;

public class TtlTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.enableCheckpointing(2000, CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setCheckpointStorage("file:/d:/ckpt");

        DataStreamSource<String> s1 = env.socketTextStream("localhost", 9999);
        env.setParallelism(1);

        //
        s1.keyBy(new KeySelector<String, String>() {
                    @Override
                    public String getKey(String s) throws Exception {
                        return  s.compareTo("m")<0 ?"a":"b";
                    }
                })
                .process(new KeyedProcessFunction<String, String, String>() {

                    ListState<String> state;
                    MapState<String, Integer> mp_state;

                    @Override
                    public void open(Configuration parameters) throws Exception {

                        ListStateDescriptor<String> stateDescriptor = new ListStateDescriptor<>(
                                "s",
                                String.class);
                        StateTtlConfig ttlConfig = StateTtlConfig.newBuilder(Time.seconds(1))
                                .setTtlTimeCharacteristic(StateTtlConfig.TtlTimeCharacteristic.ProcessingTime)
                                .build();
                        stateDescriptor.enableTimeToLive(ttlConfig);

                        state = getRuntimeContext().getListState(stateDescriptor);

                        mp_state = getRuntimeContext().
                                getMapState(new MapStateDescriptor<String, Integer>("mp_state", String.class, Integer.class));

                    }

                    @Override
                    public void processElement(
                            String value,
                            KeyedProcessFunction<String, String, String>.Context ctx,
                            Collector<String> out) throws Exception {
                        state.add(value);

                        Iterable<String> strings = state.get();
                        Iterator<String> iterator = strings.iterator();
                        if(iterator.hasNext()) out.collect(iterator.next());

                    }
                }).print();

        env.execute();

    }
}
