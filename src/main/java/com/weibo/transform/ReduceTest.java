package com.weibo.transform;

import com.weibo.pojo.Event;
import com.weibo.source.SourceCustomizeTest;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class ReduceTest {
    public static void main(String[] args) throws Exception{
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.addSource(new SourceCustomizeTest.CustomSource1())
                //将Event数据类型转换为元组类型
                .map(new MapFunction<Event, Tuple2<String, Long>>() {
                    @Override
                    public Tuple2<String, Long> map(Event value) throws Exception {
                        return Tuple2.of(value.user, 1L);
                    }
                })
                //使用用户名进行分流
                .keyBy(r -> r.f0)
                .reduce(new ReduceFunction<Tuple2<String, Long>>() {
                    @Override
                    public Tuple2<String, Long> reduce(Tuple2<String, Long> value1, Tuple2<String, Long> value2) throws Exception {
                        // 每到一条数据，用户PV的统计值加1
                        return Tuple2.of(value1.f0, value1.f1 + value2.f1);
                    }
                })
                //记录所有用户中访问频次最高的那个用户。
                //为每一条数据分配同一个Key，将聚合结果发送到一条数据流中
                .keyBy(r -> true)
                .reduce(new ReduceFunction<Tuple2<String, Long>>() {
                    @Override
                    public Tuple2<String, Long> reduce(Tuple2<String, Long> value1, Tuple2<String, Long> value2) throws Exception {
                        //将累加器更新为当前最大的PV统计值，然后向下游发送累加器的值。
                        return value1.f1 > value2.f1 ? value1 : value2;
                    }
                })
                .print();

        //reduce同简单聚合算子一样，也要针对每个key保存状态。
        // 因为状态不会清空，所以需要将reduce算子作用在一个有限key的数据流上。
        env.execute();
    }
}
