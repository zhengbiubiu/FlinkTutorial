package com.weibo.transform;

import com.weibo.pojo.Event;
import com.weibo.source.SourceCustomizeTest;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

/*
物理分区算子，区别与keyBy（逻辑分区）
 */
public class PartitionTest {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<Event> steam = env.addSource(new SourceCustomizeTest.CustomSource1());
        // 1.随机分区（shuffle），将数据随机分配到下游算子的并行任务中
//        steam.shuffle().print("shuffle").setParallelism(4);

        // 2.轮询分区（Round-Robin），按照先后顺序将数据一次发放。
//        steam.rebalance().print("rebalance").setParallelism(4);

        // 3.重缩放分区（rescale），底层也使用Round-Bobin算法，但是只会将数据轮询发送到下游并行任务的一部分中。
        // 当下游任务（数据接收方）的数量是上游任务（数据发送方）的整数倍时，rescale的效率会明显更高。
        // 如果配置的任务槽数量合适，局部重缩放rescale可以让数据只在当前TaskManager的多个任务槽之间重新分配 ，
        // 从而避免网络传输带来的损耗。
        env.addSource(new RichParallelSourceFunction<Integer>() {
                    @Override
                    public void run(SourceContext<Integer> ctx) throws Exception {
                        for (int i = 0; i < 8; i++) {
                            // 将奇数发送到索引为1的并行子任务上
                            // 将偶数发送到索引为0的并行子任务上
                            if ((i + 1) % 2 == getRuntimeContext().getIndexOfThisSubtask()) {
                                ctx.collect(i + 1);
                            }
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                }).setParallelism(2)
                .rebalance().print("rebalance").setParallelism(4);
        env.execute();
    }
}
