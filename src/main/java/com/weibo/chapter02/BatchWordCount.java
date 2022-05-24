package com.weibo.chapter02;

import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.FlatMapOperator;
import org.apache.flink.api.java.operators.UnsortedGrouping;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class BatchWordCount {
    public static void main(String[] args) throws Exception {
        // 1.创建执行环境(DataSet API)
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        // 2.从文件中读取数据：按行读取
        DataSource<String> lineDS = env.readTextFile("input/words.txt");
        // 3.转换数据格式
        FlatMapOperator<String, Tuple2<String,Long>> wordAndOne =
                lineDS.flatMap((String line,Collector<Tuple2<String, Long>> out) ->
        {
            String[] words = line.split(" ");
            for (String word : words) {
                out.collect(Tuple2.of(word, 1L));
            }
        }).returns(Types.TUPLE(Types.STRING, Types.LONG));
        // 4.按照word进行分组
        UnsortedGrouping<Tuple2<String, Long>> wordAndOneUG = wordAndOne.groupBy(0);
        // 5.分组内聚合统计
        AggregateOperator<Tuple2<String, Long>> sum = wordAndOneUG.sum(1);
        // 6.输出结果
        sum.print();
    }
}
