package com.weibo.transform;

import com.weibo.pojo.Event;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class AggregateTest {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
//        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);
        DataStreamSource<Event> stream = env.fromElements(
                new Event("marry", "/home", 1000L),
                new Event("jack", "/school", 2000L),
                new Event("bob", "/car", 6000L),
                new Event("chen", "/school", 3000L)
        );
        //flink中做聚合先要进行分区
        //keyby实现，计算key的哈希值对分区数进行取模运算来实现，如果key是POJO，则注意需要重写hashCode方法。
        //keyBy得到的不再DataStream，而是将其转换为KeyedStream。
        //lambda表达式
        stream.keyBy(e -> e.user);
        //匿名内部类
        KeyedStream<Event, String> keyedStream = stream.keyBy(new KeySelector<Event, String>() {
            @Override
            public String getKey(Event value) throws Exception {
                return value.url
                        ;
            }
        });

        //如果数据流的类型是POJO，只能通过字段名来指定，不能通过位置。
//        keyedStream.max("timeStamp").print();

        DataStreamSource<Tuple2<String, Integer>> stream2 = env.fromElements(
                Tuple2.of("a", 1),
                Tuple2.of("a", 3),
                Tuple2.of("b", 3),
                Tuple2.of("b", 4)
        );
        stream2.keyBy(r -> r.f0).sum(1).print("sum1=");
        stream2.keyBy(r -> r.f0).sum("f1").print("sum f1=");
        stream2.keyBy(r -> r.f0).max(1).print("max=");
        stream2.keyBy(r -> r.f0).maxBy(1).print("maxBy=");
        stream2.keyBy(r -> r.f0).min("f1").print("min=");
        stream2.keyBy(r -> r.f0).minBy(1).print("minBy=");
        env.execute();

    }
}
